package br.all.application.report.util

import br.all.application.protocol.repository.CriterionDto
import io.github.serpro69.kfaker.Faker

class TestDataFactory {
    val faker = Faker()

    fun criteriaDto(
        description: String = faker.leagueOfLegends.quote(),
        type: String = faker.clashOfClans.defensiveBuildings(),
    ): CriterionDto {
        return CriterionDto(
            description = description,
            type = type,
        )
    }
}