package br.all.application.review.find.services

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.find.presenter.FindOneSystematicStudyPresenter
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthenticated
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthorized
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.EntityNotFoundException
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindOneSystematicStudyServiceImplTest {
    @MockK
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: FindOneSystematicStudyPresenter
    @InjectMockKs
    private lateinit var sut: FindOneSystematicStudyServiceImpl

    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() = run { factory = TestDataFactory() }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully finding one existent systematic study")
    inner class WhenSuccessfullyFindingOneExistentSystematicStudy {
        @Test
        fun `should correctly find a systematic study and prepare a success view`() {
            val (researcher, systematicStudy) = factory
            val response = factory.findOneResponseModel()

            makeResearcherToBeAllowed(credentialsService, presenter, researcher)
            makeSystematicStudyExist(systematicStudy, researcher, response.content)

            sut.findById(presenter, researcher, systematicStudy)
            verify { presenter.prepareSuccessView(response) }
        }

        private fun makeSystematicStudyExist(
            systematicStudyId: UUID,
            researcherId: UUID,
            dto: SystematicStudyDto
        ) {
            every { repository.existsById(systematicStudyId) } returns true
            every { repository.hasReviewer(systematicStudyId, researcherId) } returns true
            every { repository.findById(systematicStudyId) } returns dto
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to find a systematic study")
    inner class WhenBeingUnableToFindASystematicStudy {
        @Test
        fun `should prepare a fail view when trying to find a nonexistent systematic study`() {
            val (researcher, systematicStudy) = factory

            makeResearcherToBeAllowed(credentialsService, presenter, researcher)
            every { repository.existsById(systematicStudy) } returns false
            every { presenter.isDone() } returns false andThen true

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.prepareFailView(any<EntityNotFoundException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should prepare a fail view if the researcher is not a collaborator`() {
            val (researcher, systematicStudy) = factory

            makeResearcherToBeAllowed(credentialsService, presenter, researcher)
            every { repository.existsById(systematicStudy) } returns true
            every { repository.hasReviewer(systematicStudy, researcher) } returns false
            every { presenter.isDone() } returns false andThen true

            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should a unauthenticated researcher be unable to find any systematic study`() {
            val (researcher, systematicStudy) = factory

            makeResearcherToBeUnauthenticated(credentialsService, presenter, researcher)
            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthenticatedUserException>())
            }
        }

        @Test
        fun `should a unauthorized researcher be unable to find any systematic study`() {
            val (researcher, systematicStudy) = factory

            makeResearcherToBeUnauthorized(credentialsService, presenter, researcher)
            sut.findById(presenter, researcher, systematicStudy)
            verify {
                presenter.isDone()
                presenter.prepareFailView(any<UnauthorizedUserException>())
            }
        }
    }
}