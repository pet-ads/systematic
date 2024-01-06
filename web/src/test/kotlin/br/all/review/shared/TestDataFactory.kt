package br.all.review.shared

import br.all.infrastructure.review.SystematicStudyDocument
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    private val faker = Faker()
    val researcherId = UUID.randomUUID()

    fun createSystematicStudyDocument(
        id: UUID = UUID.randomUUID(),
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = UUID.randomUUID(),
        collaborators: MutableSet<UUID> = mutableSetOf(),
    ) = SystematicStudyDocument(id, title, description, owner, collaborators.also { it.add(owner) }.toSet())

    fun createValidPostRequest(
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        collaborators: Set<UUID> = emptySet()
    ) = """
        {
            "title": "$title",
            "description": "$description",
            "collaborators": $collaborators
        }
    """.trimIndent()

    fun createInvalidPostRequest() = """
        {
            "title": "",
            "description": "",
            "collaborators": [],
        }
    """.trimIndent()
}