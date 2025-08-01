package br.all.application.user.find

import br.all.application.user.find.RetrieveAccountPublicDataService.RequestModel

class RetrieveAccountPublicDataServiceImpl(
    private val credentialsService: LoadAccountCredentialsService
) : RetrieveAccountPublicDataService {
    override fun retrieveData(
        presenter: RetrieveAccountPublicDataPresenter,
        request: RequestModel
    ) {
        val user = credentialsService.loadSimpleCredentialsById(request.userId)

    }

}