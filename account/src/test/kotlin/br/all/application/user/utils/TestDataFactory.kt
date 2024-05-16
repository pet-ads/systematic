package br.all.application.user.utils

import br.all.application.user.create.RegisterUserAccountService
import io.github.serpro69.kfaker.Faker

class TestDataFactory {
    val faker = Faker()

    fun registerRequest() = RegisterUserAccountService.RequestModel(
        username = faker.name.firstName(),
        password = faker.pearlJam.songs(),
        email = faker.internet.email(),
        country = faker.address.country(),
        affiliation = faker.lorem.words()
    )
}