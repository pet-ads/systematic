package br.all.application.user.utils

import br.all.application.user.create.RegisterUserAccountService
import br.all.application.user.repository.AccountCredentialsDto
import br.all.application.user.repository.UserProfileDto
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val faker = Faker()

    fun registerRequest() = RegisterUserAccountService.RequestModel(
        username = faker.leagueOfLegends.champion(),
        password = faker.pearlJam.songs(),
        email = faker.internet.email(),
        country = faker.address.countryCode(),
        affiliation = faker.lorem.words(),
        name = faker.name.neutralFirstName()
    )

    fun accountCredentials()
    = AccountCredentialsDto(
        id = UUID.randomUUID(),
        username = faker.name.firstName(),
        password = faker.pearlJam.songs(),
        authorities = setOf("USER"),
        refreshToken = faker.lorem.words()
    )

    fun userProfile() = UserProfileDto(
        id = UUID.randomUUID(),
        email = faker.internet.email(),
        country = faker.address.countryCode(),
        affiliation = faker.leagueOfLegends.rank(),
        name = faker.name.firstName()
    )
}