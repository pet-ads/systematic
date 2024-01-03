package br.all.review.shared

import br.all.infrastructure.review.SystematicStudyDocument
import io.github.serpro69.kfaker.Faker
import java.util.*

class DummyFactory {
    private val faker = Faker()

    fun createSystematicStudyDocument(
        id: UUID = UUID.randomUUID(),
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = UUID.randomUUID(),
        collaborators: MutableSet<UUID> = mutableSetOf(),
    ) = SystematicStudyDocument(id, title, description, owner, collaborators.also { it.add(owner) }.toSet())
}