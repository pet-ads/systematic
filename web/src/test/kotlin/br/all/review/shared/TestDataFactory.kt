package br.all.review.shared

import br.all.infrastructure.review.SystematicStudyDocument
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    private val faker = Faker()
    private val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val ownerId: UUID = UUID.randomUUID()

    fun createSystematicStudyDocument(
        id: UUID = this.systematicStudyId,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = this.researcherId,
        collaborators: MutableSet<UUID> = mutableSetOf(),
        objectives: String = faker.adjective.positive(),
    ) = SystematicStudyDocument(id, title, description, owner, collaborators.also { it.add(owner) }.toSet(), objectives)

    fun createValidPostRequest(
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        collaborators: Set<UUID> = emptySet(),
        objectives: String = faker.animal.name(),
    ): String {
        val collaboratorsJson = collaborators.joinToString(
            prefix = "[",
            postfix = "]"
        ) { "\"$it\"" }

        return """
        {
            "title": "$title",
            "description": "$description",
            "collaborators": $collaboratorsJson,
            "objectives": "$objectives"
        }
    """.trimIndent()
    }

    fun createInvalidPostRequest() = """
        {
            "title": "",
            "description": "",
            "collaborators": [],
            "objectives": ""
        }
    """.trimIndent()

    fun createValidPutRequest(
        title: String?,
        description: String?
    ) = """
        {
            ${if (title == null) "" else "\"title\": \"$title\""}${if (title != null && description != null) "," else ""}
            ${if (description == null) "" else "\"description\": \"$description\""}
        }
    """.trimIndent()

    operator fun component1() = researcherId

    operator fun component2() = systematicStudyId

    operator fun component3() = ownerId
}