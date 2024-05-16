package br.all.application.user.utils

import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.repository.AccountCredentialsDto
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val faker = Faker()

    fun registerRequest() = RegisterUserAccountService.RequestModel(
        username = faker.name.firstName(),
        password = faker.pearlJam.songs(),
        email = faker.internet.email(),
        country = faker.address.country(),
        affiliation = faker.lorem.words()
    )

    fun accountCredentials()
    = AccountCredentialsDto(
        id = UUID.randomUUID(),
        username = faker.name.firstName(),
        password = faker.pearlJam.songs(),
        authorities = setOf("USER"),
        refreshToken = faker.lorem.words()
    )

}