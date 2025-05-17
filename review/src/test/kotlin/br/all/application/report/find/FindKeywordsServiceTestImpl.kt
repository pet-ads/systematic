package br.all.application.report.find

import br.all.application.report.find.presenter.FindKeywordsPresenter
import br.all.application.report.find.service.FindKeywordsService
import br.all.application.report.find.service.FindKeywordsServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import br.all.application.study.util.TestDataFactory as StudyDataFactory
import br.all.application.report.util.TestDataFactory
import io.mockk.MockKAnnotations
import io.mockk.slot

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindKeywordsServiceTestImpl {

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindKeywordsPresenter

    @InjectMockKs
    private lateinit var sut: FindKeywordsServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: TestDataFactory
    private lateinit var studyFactory: StudyDataFactory

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        factory = TestDataFactory()
        studyFactory = StudyDataFactory()
        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            factory.researcher,
            factory.systematicStudy
        )
        // garante que as pré-condições (autenticação, autorização, existência do estudo) passam
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When filtering by selection")
    inner class FilterBySelection {
        @Test
        fun `should count only INCLUDED selection keywords`() {
            // Arrange: três estudos com keywords e status variados
            val dto1 = studyFactory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("key1;key2")
            )
            val dto2 = studyFactory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("key1;key3")
            )
            val dto3 = studyFactory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("key1;key4")
            )
            every { studyReviewRepository.findAllFromReview(factory.systematicStudy) }
                .returns(listOf(dto1, dto2, dto3))

            val request = FindKeywordsService.RequestModel(
                userId = factory.researcher,
                systematicStudyId = factory.systematicStudy,
                filter = "selection"
            )

            // Act
            sut.findKeywords(presenter, request)

            // Assert
            val slot = slot<FindKeywordsService.ResponseModel>()
            verify(exactly = 1) {
                presenter.prepareSuccessView(capture(slot))
            }
            val result = slot.captured
            with(result.keywords) {
                // key1 aparece em dto1 e dto2 = 2 vezes
                assert(this["key1"] == 2)
                // key2 aparece uma vez
                assert(this["key2"] == 1)
                // key3 aparece uma vez
                assert(this["key3"] == 1)
                // key4 não deve entrar
                assert(!containsKey("key4"))
            }
            // quantidade total de keywords = soma dos valores
            assert(result.keywordsQuantity == 2 + 1 + 1)
        }
    }

    @Nested
    @DisplayName("When filtering by extraction")
    inner class FilterByExtraction {
        @Test
        fun `should count only INCLUDED extraction keywords`() {
            val dto1 = studyFactory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("a;b")
            )
            val dto2 = studyFactory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("b;c")
            )
            val dto3 = studyFactory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("a;d")
            )
            every { studyReviewRepository.findAllFromReview(factory.systematicStudy) }
                .returns(listOf(dto1, dto2, dto3))

            val request = FindKeywordsService.RequestModel(
                userId = factory.researcher,
                systematicStudyId = factory.systematicStudy,
                filter = "extraction"
            )

            sut.findKeywords(presenter, request)

            val slot = slot<FindKeywordsService.ResponseModel>()
            verify {
                presenter.prepareSuccessView(capture(slot))
            }
            val counts = slot.captured.keywords
            // distinct keywords from INCLUDED extraction: a, b, c
            assert(counts["a"] == 1)
            assert(counts["b"] == 2)
            assert(counts["c"] == 1)
            assert(!counts.containsKey("d"))
            // quantity = number of distinct keywords
            assert(slot.captured.keywordsQuantity == counts.size)
        }
    }

    @Nested
    @DisplayName("When no filter (null)")
    inner class NoFilter {
        @Test
        fun `should count all keywords across studies`() {
            val dto1 = studyFactory.generateDto(
                selectionStatus = "INCLUDED",
                extractionStatus = "INCLUDED",
                keywords = setOf("x;y")
            )
            val dto2 = studyFactory.generateDto(
                selectionStatus = "EXCLUDED",
                extractionStatus = "EXCLUDED",
                keywords = setOf("y;z")
            )
            every { studyReviewRepository.findAllFromReview(factory.systematicStudy) }
                .returns(listOf(dto1, dto2))

            val request = FindKeywordsService.RequestModel(
                userId = factory.researcher,
                systematicStudyId = factory.systematicStudy,
                filter = null
            )

            sut.findKeywords(presenter, request)

            val slot = slot<FindKeywordsService.ResponseModel>()
            verify { presenter.prepareSuccessView(capture(slot)) }
            val counts = slot.captured.keywords
            assert(counts["x"] == 1)
            assert(counts["y"] == 2)
            assert(counts["z"] == 1)
            assert(slot.captured.keywordsQuantity == counts.size)
        }
    }
}