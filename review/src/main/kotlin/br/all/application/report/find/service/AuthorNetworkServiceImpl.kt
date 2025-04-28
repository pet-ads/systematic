package br.all.application.report.find.service

import br.all.application.report.find.presenter.AuthorNetworkPresenter
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromDto
import br.all.application.shared.presenter.prepareIfFailsPreconditions
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.user.CredentialsService
import br.all.domain.model.review.SystematicStudy

class AuthorNetworkServiceImpl(
    private val credentialsService: CredentialsService,
    private val studyReviewRepository: StudyReviewRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
): AuthorNetworkService {
    override fun findAuthors(presenter: AuthorNetworkPresenter, request: AuthorNetworkService.RequestModel) {
        val user = credentialsService.loadCredentials(request.userId)?.toUser()

        val systematicStudyDto = systematicStudyRepository.findById(request.systematicStudyId)
        val systematicStudy = systematicStudyDto?.let { SystematicStudy.fromDto(it) }

        presenter.prepareIfFailsPreconditions(user, systematicStudy)

        if(presenter.isDone()) return

        val allStudies = studyReviewRepository.findAllFromReview(request.systematicStudyId)

        val listOfPapers = allStudies.map { study ->
            PaperNode(
                paperId = study.studyReviewId,
                authors = study.authors.split(",").map { it.trim() }
            )
        }

        val (nodes, edges) = generateGraph(listOfPapers)

        val response = AuthorNetworkService.ResponseModel(
            userId = request.userId,
            systematicStudyId = request.systematicStudyId,
            nodes = nodes,
            edges = edges
        )

        presenter.prepareSuccessView(response)
    }
}

data class PaperNode(
    val paperId: Long,
    val authors: List<String>
)

data class Edge(
    val paper1Id: Long,
    val paper2Id: Long
)


fun generateGraph(papers: List<PaperNode>): Pair<Set<PaperNode>, List<Edge>> {
    val nodes = papers.toSet()
    val edges = mutableListOf<Edge>()

    for (i in papers.indices) {
        for (j in i + 1 until papers.size) {
            val paper1 = papers[i]
            val paper2 = papers[j]

            if (paper1.authors.intersect(paper2.authors.toSet()).size >= 2) {
                edges.add(Edge(paper1.paperId, paper2.paperId))
            }
        }
    }

    return nodes to edges
}
