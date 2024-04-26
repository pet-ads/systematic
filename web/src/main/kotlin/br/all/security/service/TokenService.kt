package br.all.security.service

import br.all.security.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(jwtProperties: JwtProperties) {

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())

    fun generateToken(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

    fun extractUsername(token: String): String? = getAllClaims(token).subject

    fun isExpired(token: String) = getAllClaims(token).expiration.before(Date(System.currentTimeMillis()))

    fun isValid(token: String, userDetails: UserDetails) =
        extractUsername(token) == userDetails.username && !isExpired(token)

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser().verifyWith(secretKey).build()
        return parser.parseSignedClaims(token).payload
    }

}