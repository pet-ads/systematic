package br.all.domain.shared.presenter

interface GenericPresenter <T> {
    fun prepareSuccessView(response: T)
    fun prepareFailView(throwable: Throwable)
    fun isDone(): Boolean
}