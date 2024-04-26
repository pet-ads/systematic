package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.application.user.update.UpdateRefreshTokenService.RequestModel

class UpdateRefreshTokenServiceImpl(val repository: UserAccountRepository) : UpdateRefreshTokenService {

    override fun update(request: RequestModel) {
        repository.updateRefreshToken(request.userId, request.refreshToken)
    }
}