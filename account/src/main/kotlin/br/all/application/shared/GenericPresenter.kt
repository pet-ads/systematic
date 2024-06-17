package br.all.application.shared

interface GenericPresenter <T> {
    fun prepareSuccessView(response: T)
    fun prepareFailView(throwable: Throwable)
    fun isDone(): Boolean
}