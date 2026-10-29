package br.all.application.study.majority

import br.all.application.reviewmode.repository.ReviewModeDto
import br.all.application.reviewmode.repository.ReviewModeRepository
import br.all.application.reviewmode.repository.ReviewerStudyDto
import br.all.application.reviewmode.repository.ReviewerStudyRepository
import br.all.application.reviewmode.repository.SplitDto
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.util.TestDataFactory
import br.all.domain.model.study.StudyReviewStage
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class MajorityDecisionApplierTest {

    @MockK
    private lateinit var reviewModeRepository: ReviewModeRepository

    @MockK
    private lateinit var reviewerStudyRepository: ReviewerStudyRepository

    @MockK
    private lateinit var studyReviewRepository: StudyReviewRepository

    private lateinit var sut: MajorityDecisionApplier
    private lateinit var factory: TestDataFactory

    private val studyId = 1L
    private val reviewers = List(3) { UUID.randomUUID() }
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        systematicStudyId = factory.systematicStudyId
        sut = MajorityDecisionApplier(
            reviewModeRepository,
            reviewerStudyRepository,
            studyReviewRepository
        )

        every { studyReviewRepository.saveOrUpdate(any()) } returns Unit
    }

    private fun modeWith(
        selectionMajority: Boolean,
        extractionMajority: Boolean = false
    ) = ReviewModeDto(
        systematicStudyId,
        selection = SplitDto(
            reviewers = reviewers,
            reviewPerStudy = 2,
            acceptMajority = selectionMajority
        ),
        extraction = SplitDto(
            reviewers = reviewers,
            reviewPerStudy = 2,
            acceptMajority = extractionMajority
        ),
    )

    private fun original(
        selectionStatus: String = "UNCLASSIFIED",
        extractionStatus: String = "UNCLASSIFIED"
    ) = factory.generateDto(
        systematicStudyId = systematicStudyId,
        studyReviewId = studyId,
        selectionStatus = selectionStatus,
        extractionStatus = extractionStatus,
    )

    private fun copyOf(
        stage: StudyReviewStage,
        selection: String = "UNCLASSIFIED",
        extraction: String = "UNCLASSIFIED",
        criteria: Set<String> = setOf("Crit"),
    ): ReviewerStudyDto {
        val base = factory.generateReviewerStudyDto(
            studyId,
            UUID.randomUUID(),
            stage
        )

        return base.copy(
            studyReview = base.studyReview.copy(
                selectionStatus = selection,
                extractionStatus = extraction,
                selectionCriteria = criteria,
                extractionCriteria = criteria,
            )
        )
    }

    private fun selectionCopies(vararg statuses: String) =
        statuses.map {
            copyOf(
                StudyReviewStage.SELECTION,
                selection = it
            )
        }

    private fun extractionCopies(vararg statuses: String) =
        statuses.map {
            copyOf(
                StudyReviewStage.EXTRACTION,
                selection = "INCLUDED",
                extraction = it
            )
        }

    private fun givenCopies(
        stage: StudyReviewStage,
        copies: List<ReviewerStudyDto>
    ) {
        every {
            reviewerStudyRepository.findAllByStudy(
                systematicStudyId,
                studyId,
                stage
            )
        } returns copies
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When there is a majority")
    inner class WhenThereIsAMajority {

        @Test
        fun `should include the original study in selection when most reviewers included it`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "UNCLASSIFIED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original()

            val applied = sut.apply(
                systematicStudyId,
                studyId,
                StudyReviewStage.SELECTION
            )

            assertTrue(applied)

            val saved = slot<StudyReviewDto>()

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(capture(saved))
            }

            assertEquals("INCLUDED", saved.captured.selectionStatus)
            assertTrue(saved.captured.selectionCriteria.contains("Crit"))
        }

        @Test
        fun `should exclude the original study in selection when most reviewers excluded it`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("EXCLUDED", "EXCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original()

            val applied = sut.apply(
                systematicStudyId,
                studyId,
                StudyReviewStage.SELECTION
            )

            assertTrue(applied)

            val saved = slot<StudyReviewDto>()

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(capture(saved))
            }

            assertEquals("EXCLUDED", saved.captured.selectionStatus)
        }

        @Test
        fun `should apply the majority when 3 of 4 reviewers agree`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "INCLUDED", "EXCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original()

            assertTrue(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )
        }

        @Test
        fun `should apply the majority when both of 2 reviewers agree`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original()

            assertTrue(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )
        }

        @Test
        fun `should include the original study in extraction when most reviewers included it`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(
                selectionMajority = false,
                extractionMajority = true
            )

            givenCopies(
                StudyReviewStage.EXTRACTION,
                extractionCopies("INCLUDED", "INCLUDED", "UNCLASSIFIED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original(selectionStatus = "INCLUDED")

            val applied = sut.apply(
                systematicStudyId,
                studyId,
                StudyReviewStage.EXTRACTION
            )

            assertTrue(applied)

            val saved = slot<StudyReviewDto>()

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(capture(saved))
            }

            assertEquals("INCLUDED", saved.captured.extractionStatus)
            assertEquals("INCLUDED", saved.captured.selectionStatus)
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When the majority must not be applied")
    inner class WhenTheMajorityMustNotBeApplied {

        @Test
        fun `should not apply when there is no majority`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "EXCLUDED", "UNCLASSIFIED")
            )

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply on a 2 to 2 tie`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "EXCLUDED", "EXCLUDED")
            )

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply when the two reviewers disagree`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "EXCLUDED")
            )

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply when the option is disabled for the stage`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = false)

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                reviewerStudyRepository.findAllByStudy(any(), any(), any())
            }

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should only consider the option of the stage being classified`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(
                selectionMajority = true,
                extractionMajority = false
            )

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.EXTRACTION
                )
            )

            verify(exactly = 0) {
                reviewerStudyRepository.findAllByStudy(any(), any(), any())
            }
        }

        @Test
        fun `should not apply when the review mode does not exist`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns null

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply when the study has no reviewer copies`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                emptyList()
            )

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )
        }

        @Test
        fun `should not save again when the original already has the winning status`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original(selectionStatus = "INCLUDED")

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply when the original study was not found`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns null

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )
        }

        @Test
        fun `should not apply to a duplicated study`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(selectionMajority = true)

            givenCopies(
                StudyReviewStage.SELECTION,
                selectionCopies("INCLUDED", "INCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original(selectionStatus = "DUPLICATED")

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.SELECTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }

        @Test
        fun `should not apply an extraction inclusion when the study was excluded in selection`() {
            every {
                reviewModeRepository.findById(systematicStudyId)
            } returns modeWith(
                selectionMajority = false,
                extractionMajority = true
            )

            givenCopies(
                StudyReviewStage.EXTRACTION,
                extractionCopies("INCLUDED", "INCLUDED", "INCLUDED")
            )

            every {
                studyReviewRepository.findById(systematicStudyId, studyId)
            } returns original(selectionStatus = "EXCLUDED")

            assertFalse(
                sut.apply(
                    systematicStudyId,
                    studyId,
                    StudyReviewStage.EXTRACTION
                )
            )

            verify(exactly = 0) {
                studyReviewRepository.saveOrUpdate(any())
            }
        }
    }
}