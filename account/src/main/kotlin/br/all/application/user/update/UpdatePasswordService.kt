package br.all.application.user.update

import br.all.application.user.repository.UserAccountRepository
import br.all.domain.shared.service.PasswordEncoderPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdatePasswordService(
    private val repository: UserAccountRepository,
    private val encoder: PasswordEncoderPort
) {

    fun update(userId: UUID, rawPassword: String) {
        require(rawPassword.isNotBlank()) {
            "Password cannot be empty"
        }

        val newHashedPassword = encoder.encode(rawPassword)

        repository.updatePassword(userId, newHashedPassword)
    }
}