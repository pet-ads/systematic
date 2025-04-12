package br.all.application.report.find.presenter

import br.all.application.shared.presenter.GenericPresenter
import br.all.application.report.find.service.FindAnswersService.ResponseModel

interface FindAnswersPresenter: GenericPresenter<ResponseModel<*>>