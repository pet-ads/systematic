package br.all.application.review.find.presenter

import br.all.application.review.find.services.SearchCollaboratorCandidatesService
import br.all.domain.shared.presenter.GenericPresenter

interface SearchCollaboratorCandidatesPresenter : GenericPresenter<SearchCollaboratorCandidatesService.ResponseModel>
