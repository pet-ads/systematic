package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReviewStage
import br.all.domain.shared.user.ResearcherId
import com.mongodb.assertions.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.Test

@Tag("UnitTest")
class SystematicStudyReviewModeTest {
    private lateinit var sut: SystematicStudyReviewMode

    @BeforeEach
    fun setUp() {
        sut = SystematicStudyReviewMode(SystematicStudyId(UUID.randomUUID()))
    }

    @Nested
    @DisplayName("When defining a review mode")
    inner class WhenDefiningAReviewMode {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should define replication on selection when providing a main reviewer`() {
                val mainReviewer = ResearcherId(UUID.randomUUID())

                sut.defineMode(StudyReviewStage.SELECTION, mainReviewer)

                assertAll(
                    { assertTrue(sut.selection is Replication) },
                    { assertEquals(mainReviewer, (sut.selection as Replication).mainReviewer) },
                    { assertNull(sut.extraction) }
                )
            }

            @Test
            fun `should define split on extraction when providing review per study`() {
                val reviewers = List(3) { ResearcherId(UUID.randomUUID()) }

                sut.defineMode(StudyReviewStage.EXTRACTION,
                    SystematicStudyReviewMode.SplitParams(reviewers, reviewPerStudy = 2)
                )

                assertAll(
                    { assertTrue(sut.extraction is Split) },
                    { assertEquals(reviewers, (sut.extraction as Split).reviewers) },
                    { assertEquals(2, (sut.extraction as Split).reviewPerStudy) },
                    { assertNull(sut.selection) }
                )
            }
        }
    }
}