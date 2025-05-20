package br.all.application.report.find

import br.all.application.protocol.repository.ProtocolRepository
import br.all.application.report.find.presenter.FindSourcePresenter
import br.all.application.report.find.service.FindSourceServiceImpl
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.application.util.PreconditionCheckerMockingNew
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import br.all.application.study.util.TestDataFactory as StudyReviewFactory
import br.all.application.protocol.util.TestDataFactory as ProtocolDtoFactory


@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class FindSourceServiceImplTest {

    @MockK(relaxUnitFun = true)
    private lateinit var protocolRepository: ProtocolRepository

    @MockK(relaxUnitFun = true)
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK(relaxUnitFun = true)
    private lateinit var studyReviewRepository: StudyReviewRepository

    @MockK
    private lateinit var credentialsService: CredentialsService

    @MockK(relaxUnitFun = true)
    private lateinit var presenter: FindSourcePresenter

    @InjectMockKs
    private lateinit var sut: FindSourceServiceImpl

    private lateinit var precondition: PreconditionCheckerMockingNew
    private lateinit var studyFactory: StudyReviewFactory
    private lateinit var protocolDtoFactory: ProtocolDtoFactory

    @BeforeEach
    fun setup() {
        studyFactory = StudyReviewFactory()
        protocolDtoFactory = ProtocolDtoFactory()

        precondition = PreconditionCheckerMockingNew(
            presenter,
            credentialsService,
            systematicStudyRepository,
            studyFactory.researcherId,
            studyFactory.systematicStudyId
        )

        precondition.makeEverythingWork()
    }


    @Nested
    @DisplayName("When successfully finding studies by source")
    inner class SuccessfullyFindingBySource {

    }
    

    @Nested
    @DisplayName("When failing finding studies by source")
    inner class FailingFindingBySource {

    }
}