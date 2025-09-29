package br.all.user.shared

import br.all.application.user.repository.UserAccountDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.util.*

class TestDataFactory {
    private val faker = Faker()

    val userId: UUID = UUID.randomUUID()
    val username: String = faker.name.firstName()
    val name: String = faker.name.neutralFirstName()
    val email: String = faker.internet.email()
    val rawPassword: String = faker.pearlJam.songs()
    val affiliation: String = faker.university.name()
    val country: String = faker.address.country()

    fun createUserDto(
        id: UUID = userId,
        name: String = this.name,
        username: String = this.username,
        email: String = this.email,
        password: String,
        country: String = this.country,
        affiliation: String = this.affiliation,
        createdAt: LocalDateTime = LocalDateTime.now(),
        authorities: Set<String> = setOf("ROLE_USER")
    ) = UserAccountDto(
        id = id,
        name = name,
        username = username,
        password = password,
        email = email,
        country = country,
        affiliation = affiliation,
        createdAt = createdAt,
        authorities = authorities,
        refreshToken = null,
        isAccountNonExpired = true,
        isAccountNonLocked = true,
        isCredentialsNonExpired = true,
        isEnabled = true
    )

    fun createValidRegisterRequestJson(
        name: String = faker.name.name(),
        username: String = faker.name.firstName(),
        email: String = faker.internet.email(),
        password: String = faker.pearlJam.songs(),
        affiliation: String = faker.university.name(),
        country: String = faker.address.country()
    ): String {
        val map = mapOf(
            "name" to name,
            "username" to username,
            "email" to email,
            "password" to password,
            "affiliation" to affiliation,
            "country" to country
        )
        return jacksonObjectMapper().writeValueAsString(map)
    }

    fun createPatchProfileRequestJson(
        name: String? = faker.name.name(),
        email: String? = faker.internet.email(),
        affiliation: String? = faker.university.name(),
        country: String? = faker.address.country()
    ): String {
        val map = mutableMapOf<String, String>()
        name?.let { map["name"] = it }
        email?.let { map["email"] = it }
        affiliation?.let { map["affiliation"] = it }
        country?.let { map["country"] = it }
        return jacksonObjectMapper().writeValueAsString(map)
    }

    fun createChangePasswordRequestJson(
        oldPassword: String = this.rawPassword,
        newPassword: String = faker.pearlJam.songs(),
        confirmPassword: String = newPassword
    ): String {
        val map = mapOf(
            "oldPassword" to oldPassword,
            "newPassword" to newPassword,
            "confirmPassword" to confirmPassword
        )
        return jacksonObjectMapper().writeValueAsString(map)
    }
}