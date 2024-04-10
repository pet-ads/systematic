package br.all.search.presenter

import br.all.application.search.find.presenter.FindAllSearchSessionsPresenter
import br.all.application.search.find.service.FindAllSearchSessionsService.ResponseModel
import br.all.application.search.repository.SearchSessionDto
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@Component
class RestfulFindAllSearchSessionsPresenter : FindAllSearchSessionsPresenter {

    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.systematicStudyId, response.searchSessions.size, response.searchSessions)

        val selfRef = linkSelfRef(response)
//      val createSession = linkCreateSearchSession(response)

        restfulResponse.add(selfRef)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    private fun linkSelfRef(response: ResponseModel) =
        linkTo<SearchSessionController> {
            findAllSearchSessions(response.researcherId, response.systematicStudyId)
        }.withSelfRel()

    /*
    private fun linkCreateSearchSession(response: ResponseModel) =
        linkTo<SearchSessionController> {
            createSearchSession(response.researcherId,
                                response.systematicStudyId,
                                createDummyMultipartFile(),
                                "data")
        }.withRel("createSession")

    // arquivo fictício
    private fun createDummyMultipartFile(): MultipartFile {
        val content = "dummy content"
        val tempFile = File.createTempFile("dummy", ".tmp")
        tempFile.deleteOnExit()
        tempFile.writeText(content)
        return createDummyMultipartFile()
    }
    */

    override fun prepareFailView(throwable: Throwable) = run {responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null
    private data class ViewModel (
        val systematicStudyId: UUID,
        val size: Int,
        val searchSessions: List<SearchSessionDto>
    ) : RepresentationModel<ViewModel>()
}