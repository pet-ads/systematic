package br.all.application.study.create

import br.all.application.study.create.CreateStudyReviewService.ResponseModel

interface CreateStudyReviewPresenter {
    fun prepareSuccessView(response: ResponseModel)
    fun prepareFailView(throwable: Throwable)
}