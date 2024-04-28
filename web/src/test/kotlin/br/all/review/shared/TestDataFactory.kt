package br.all.review.shared

import br.all.application.user.repository.UserAccountDto
import br.all.infrastructure.review.SystematicStudyDocument
import io.github.serpro69.kfaker.Faker
import java.time.LocalDateTime
import java.util.*

class TestDataFactory {
    private val faker = Faker()
    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    val ownerId: UUID = UUID.randomUUID()

    fun createSystematicStudyDocument(
        id: UUID = this.systematicStudyId,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = this.researcherId,
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