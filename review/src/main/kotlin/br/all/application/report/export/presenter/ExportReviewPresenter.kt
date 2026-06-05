package br.all.application.report.export.presenter

import br.all.application.report.export.service.ExportReviewService
import br.all.domain.shared.presenter.GenericPresenter

interface ExportReviewPresenter : GenericPresenter<ExportReviewService.ResponseModel>
