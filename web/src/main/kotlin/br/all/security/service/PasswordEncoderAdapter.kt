package br.all.security.service

import br.all.domain.shared.service.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(
    private val encoder: PasswordEncoder
) : PasswordEncoderPort {
    override fun encode(rawPassword: String): String {
        return encoder.encode(rawPassword)
    }

    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }
}