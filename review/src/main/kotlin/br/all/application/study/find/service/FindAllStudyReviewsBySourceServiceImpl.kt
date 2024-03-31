package br.all.application.study.find.service

import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.shared.presenter.PreconditionChecker
import br.all.application.study.find.presenter.FindAllStudyReviewsBySourcePresenter
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.RequestModel
import br.all.application.study.find.service.FindAllStudyReviewsBySourceService.ResponseModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.domain.model.protocol.SearchSource
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId

class FindAllStudyReviewsBySourceServiceImpl(
    private val systematicStudyRepository: SystematicStudyRepository,
    private val studyReviewRepository: StudyReviewRepository,
   // private val protocolRepository: ProtocolRepository,
    private val credentialsService: ResearcherCredentialsService,
) : FindAllStudyReviewsBySourceService {

    override fun findAllFromSearchSession(presenter: FindAllStudyReviewsBySourcePresenter, request: RequestModel) {
        val researcherId = ResearcherId(request.researcherId)
        val systematicStudyId = SystematicStudyId(request.systematicStudyId)
        val searchSource = SearchSource(request.searchSource)
        val preconditionChecker = PreconditionChecker(systematicStudyRepository, credentialsService)
        preconditionChecker.prepareIfViolatesPreconditions(presenter, researcherId, systematicStudyId)

//        val protocolDto = protocolRepository.findBySystematicStudy(systematicStudyId.value)
//        val hasSource = protocolDto?.informationSources?.contains(searchSource.id) ?: false
//
//        if (!hasSource) {
//            val message = "Protocol ID $systematicStudyId does not contain ${searchSource.id} as a search source"
//            presenter.prepareFailView(NoSuchElementException(message))
//        }

        if (presenter.isDone()) return

        val studyReviews = studyReviewRepository.findAllBySource(request.systematicStudyId, searchSource.toString())
        presenter.prepareSuccessView(
            ResponseModel(
                request.researcherId, request.systematicStudyId,
                request.searchSource, studyReviews
            )
        )
    }
}

