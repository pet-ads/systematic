package br.all.agreement.presenter

import br.all.application.agreement.reviews.FindAgreementReviewsPresenter
import br.all.application.agreement.reviews.FindAgreementReviewsService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok

class RestfulFindAgreementReviewsPresenter : FindAgreementReviewsPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        responseEntity = ok(response)
    }

    override fun prepareFailView(throwable: Throwable) = run {
        responseEntity = createErrorResponseFrom(throwable)
    }

    override fun isDone() = responseEntity != null
}