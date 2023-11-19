package br.all.application.shared.presenter

import br.all.application.study.create.CreateStudyReviewService

interface GenericPresenter <T> {
    fun prepareSuccessView(response: T)
    fun prepareFailView(throwable: Throwable)
}