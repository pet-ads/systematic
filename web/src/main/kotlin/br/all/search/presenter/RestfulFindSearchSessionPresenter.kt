package br.all.search.presenter

import br.all.application.search.find.presenter.FindSearchSessionPresenter
import br.all.application.search.find.service.FindSearchSessionService.ResponseModel
import br.all.application.search.repository.SearchSessionDto
import br.all.search.controller.SearchSessionController
import br.all.shared.error.createErrorResponseFrom
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class RestfulFindSearchSessionPresenter : FindSearchSessionPresenter {

    var responseEntity : ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val restfulResponse = ViewModel(response.content)

        val self = linkTo<SearchSessionController> {
            findSearchSession(response.researcherId, response.content.systematicStudyId, response.content.id)
        }.withSelfRel()

        restfulResponse.add(self)
        responseEntity = ResponseEntity.status(HttpStatus.OK).body(restfulResponse)
    }

    override fun prepareFailView(throwable: Throwable) = run {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null

    private data class ViewModel(private val content: SearchSessionDto) : RepresentationModel<ViewModel>(){
        val id = content.id
        val systematicStudyId = content.systematicStudyId
        val searchString = content.searchString
        val additionalInfo = content.additionalInfo
        val timeStamp = content.timestamp
        val source = content.source
    }
}