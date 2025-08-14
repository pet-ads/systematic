package br.all.application.report.find

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import br.all.application.report.find.service.AuthorNetworkService
import br.all.application.report.find.service.AuthorNetworkService.Edge
import br.all.application.report.find.service.AuthorNetworkService.PaperNode
import br.all.application.report.find.service.AuthorNetworkServiceImpl
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewDto
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertTrue

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AuthorNetworkServiceImplTest {

    @MockK(relaxUnitFun = true)
    lateinit var presenter: AuthorNetworkPresenter

    @MockK(relaxUnitFun = true)
    lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    lateinit var credentialsService: CredentialsService

    @InjectMockKs
    lateinit var sut: AuthorNetworkServiceImpl

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID
    private lateinit var precondition: PreconditionCheckerMockingNew

    @BeforeEach
    fun setUp() {
        researcherId = UUID.randomUUID()
        systematicStudyId = UUID.randomUUID()
        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            researcherId,
            systematicStudyId
        )
        precondition.makeEverythingWork()
    }

    @Nested
    @DisplayName("When being successful")
    inner class WhenBeingSuccessful {

        @Test
        fun `should generate correct nodes and edges with shared authors`() {
            val study1 = mockk<StudyReviewDto>(relaxed = true)
            every { study1.studyReviewId } returns 100L
            every { study1.authors } returns "Alice, Bob, Carol"

            val study2 = mockk<StudyReviewDto>(relaxed = true)
            every { study2.studyReviewId } returns 200L
            every { study2.authors } returns "Bob, Alice, Dave"

            val study3 = mockk<StudyReviewDto>(relaxed = true)
            every { study3.studyReviewId } returns 300L
            every { study3.authors } returns "Xavier, Yolanda, Zach"

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns
                    listOf(study1, study2, study3)

            val request = AuthorNetworkService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.findAuthors(presenter, request)

            val expectedNode1 = PaperNode(100L, listOf("Alice", "Bob", "Carol"))
            val expectedNode2 = PaperNode(200L, listOf("Bob", "Alice", "Dave"))
            val expectedNode3 = PaperNode(300L, listOf("Xavier", "Yolanda", "Zach"))
            val expectedNodes = setOf(expectedNode1, expectedNode2, expectedNode3)

            val expectedEdge = Edge(100L, 200L)

            val expectedResponse = AuthorNetworkService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                nodes = expectedNodes,
                edges = listOf(expectedEdge)
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }

        @Test
        fun `should generate nodes with no edges when insufficient shared authors`() {
            val study1 = mockk<StudyReviewDto>(relaxed = true)
            every { study1.studyReviewId } returns 10L
            every { study1.authors } returns "Anna, Ben"

            val study2 = mockk<StudyReviewDto>(relaxed = true)
            every { study2.studyReviewId } returns 20L
            every { study2.authors } returns "Ben, Carl"

            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns
                    listOf(study1, study2)

            val request = AuthorNetworkService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.findAuthors(presenter, request)

            val expectedNode1 = PaperNode(10L, listOf("Anna", "Ben"))
            val expectedNode2 = PaperNode(20L, listOf("Ben", "Carl"))
            val expectedNodes = setOf(expectedNode1, expectedNode2)

            val expectedResponse = AuthorNetworkService.ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                nodes = expectedNodes,
                edges = emptyList()
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }

    @Nested
    @DisplayName("When being unsuccessful")
    inner class WhenBeingUnsuccessful {

        @Test
        fun `should fail when stage precondition fails due to missing systematic study`() {
            precondition.makeSystematicStudyNonexistent()

            val dummyStudy = mockk<StudyReviewDto>(relaxed = true)
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns listOf(dummyStudy)

            val request = AuthorNetworkService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.findAuthors(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }
            assertTrue { presenter.isDone() }
        }

        @Test
        fun `should fail when stage precondition fails due to unauthenticated user`() {
            precondition.makeUserUnauthenticated()

            val dummyStudy = mockk<StudyReviewDto>(relaxed = true)
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns listOf(dummyStudy)

            val request = AuthorNetworkService.RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.findAuthors(presenter, request)

            verify(exactly = 1) {
                presenter.prepareIfFailsPreconditions(any(), any())
            }
            assertTrue { presenter.isDone() }
        }
    }
}
