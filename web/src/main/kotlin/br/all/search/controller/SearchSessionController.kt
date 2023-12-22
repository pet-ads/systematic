package br.all.search.controller

import br.all.application.search.create.CreateSearchSessionService
import br.all.domain.model.protocol.SearchSource
import br.all.search.presenter.RestfulCreateSearchSessionPresenter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import br.all.application.search.create.CreateSearchSessionService.RequestModel as CreateRequest
import java.util.UUID

@RestController
@RequestMapping("api/v1/researcher/{researcherId}/systematic-study/{systematicStudyId}/search-session")
class SearchSessionController(
    val createService : CreateSearchSessionService,
) {

    @PostMapping
    fun createSearchSession(
        @PathVariable researcher: UUID,
        @PathVariable systematicStudy: UUID,
        @RequestBody request : CreateRequest,
        @RequestPart("bibFile") bibFile: MultipartFile

    ) : ResponseEntity<*> {
        val presenter = RestfulCreateSearchSessionPresenter()
        createService.createSession(
            presenter,
            request)
        return presenter.responseEntity?: ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}