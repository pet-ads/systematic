package br.all.application.question.create

import br.all.application.question.create.CreateQuestionService.ResponseModel
import br.all.application.shared.presenter.GenericPresenter
import org.springframework.hateoas.Link
interface CreateQuestionPresenter : GenericPresenter<ResponseModel>