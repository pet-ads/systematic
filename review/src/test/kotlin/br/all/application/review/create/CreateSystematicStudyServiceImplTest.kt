package br.all.application.review.create

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeAllowed
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthenticated
import br.all.application.review.util.CredentialsServiceMockBuilder.makeResearcherToBeUnauthorized
import br.all.application.review.util.TestDataFactory
import br.all.application.shared.exceptions.UnauthenticatedUserException
import br.all.application.shared.exceptions.UnauthorizedUserException
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceImplTest {
    @MockK(relaxUnitFun = true)
    private lateinit var repository: SystematicStudyRepository
    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService
    @MockK
    private lateinit var credentialsService: ResearcherCredentialsService
    @MockK(relaxed = true)
    private lateinit var presenter: CreateSystematicStudyPresenter

    private lateinit var factory: TestDataFactory
    @InjectMockKs
    private lateinit var sut: CreateSystematicStudyServiceImpl

    @BeforeEach
    fun setUp() = run { factory = TestDataFactory() }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully creating a Systematic Study")
    inner class WhenSuccessfullyCreatingASystematicStudy {
        @Test
        fun `should successfully create a systematic study`() {
            val (researcher, systematicStudy) = factory
            val request = factory.createRequestModel()
            val response = factory.createResponseModel()
            val dto = factory.dtoFromCreateRequest(request)

            makeResearcherToBeAllowed(credentialsService, presenter, researcher)
            every { uuidGeneratorService.next() } returns systematicStudy

            sut.create(presenter, researcher, request)
            verify(exactly = 1) {
                uuidGeneratorService.next()
                repository.saveOrUpdate(dto)
                presenter.prepareSuccessView(response)
            }
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When unable to create a new Systematic Study")
    inner class WhenUnableToCreateANewSystematicStudy {
        @Test
        fun `should not the researcher be allowed to create a new study when unauthenticated`() {
            val (researcher) = factory
            val request = factory.createRequestModel()

            makeResearcherToBeUnauthenticated(credentialsService, presenter, researcher)
            sut.create(presenter, researcher, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthenticatedUserException>())
                presenter.isDone()
            }
        }

        @Test
        fun `should not the researcher be allowed to create a study when unauthorized`() {
            val (researcher) = factory
            val request = factory.createRequestModel()

            makeResearcherToBeUnauthorized(credentialsService, presenter, researcher)
            sut.create(presenter, researcher, request)

            verify(exactly = 1) {
                presenter.prepareFailView(any<UnauthorizedUserException>())
                presenter.isDone()
            }
        }
    }
}
