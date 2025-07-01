package br.all.application.protocol.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.protocol.util.TestDataFactory
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.Test
import br.all.application.protocol.find.GetProtocolStageService.RequestModel
import br.all.application.protocol.find.GetProtocolStageService.ResponseModel
import br.all.application.protocol.find.GetProtocolStageService.ProtocolStage
import io.mockk.verify

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class GetProtocolStageServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: GetProtocolStagePresenter

    @InjectMockKs
    private lateinit var sut: GetProtocolStageServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var factory: TestDataFactory

    private lateinit var researcherId: UUID
    private lateinit var systematicStudyId: UUID

    @BeforeEach
    fun setup() {
        factory = TestDataFactory()

        researcherId = factory.researcher
        systematicStudyId = factory.systematicStudy

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
    @DisplayName("When successfully getting protocol's current stage")
    inner class SuccessfullyGettingProtocolStage {
        @Test
        fun `should return protocol's first stage`() {
            val protocolDto = factory.protocolDto(
                systematicStudy = systematicStudyId,
                goal = null,
                justification = null
            )

            every { protocolRepository.findById(systematicStudyId) } returns protocolDto
            every { studyReviewRepository.findAllFromReview(systematicStudyId) } returns emptyList()

            val request = RequestModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId
            )

            sut.getStage(presenter, request)

            val expectedResponse = ResponseModel(
                userId = researcherId,
                systematicStudyId = systematicStudyId,
                currentStage = ProtocolStage.PROTOCOL_PART_I
            )

            verify(exactly = 1) {
                presenter.prepareSuccessView(expectedResponse)
            }
        }
    }
}