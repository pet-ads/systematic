package br.all.application.review.create

import br.all.application.review.create.CreateSystematicStudyService.ResponseModel

class FakeCreateSystematicStudyPresenter: CreateSystematicStudyPresenter {
    var responseModel: ResponseModel? = null
    var throwable: Throwable? = null

    override fun prepareSuccessView(response: ResponseModel) = run { responseModel = response }

    override fun prepareFailView(throwable: Throwable) = run { this.throwable = throwable }

    override fun isDone() = responseModel != null || throwable != null
}