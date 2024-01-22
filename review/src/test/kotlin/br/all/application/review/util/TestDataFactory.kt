package br.all.application.review.util

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.domain.model.review.SystematicStudy
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel as CreateResponseModel
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel as FindAllResponseModel
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel as FindOneResponseModel
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel as UpdateRequestModel

class TestDataFactory {
    private val faker = Faker()
    val researcher: UUID = UUID.randomUUID()
    val systematicStudy: UUID = UUID.randomUUID()
    val owner: UUID by lazy { UUID.randomUUID() }

    fun generateDto(
        id: UUID = systematicStudy,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        ownerId: UUID = researcher,
        collaborators: Set<UUID> = emptySet(),
    ) = SystematicStudyDto(
        id,
        title,
        description,
        ownerId,
        mutableSetOf(ownerId).also { it.addAll(collaborators) },
    )

    fun createRequestModel(
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        collaborators: Set<UUID> = emptySet()
    ) = CreateRequestModel(title, description, collaborators)

    fun createResponseModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
    ) = CreateResponseModel(researcherId, systematicStudyId)

    fun dtoFromCreateRequest(
        request: CreateRequestModel,
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
    ) = SystematicStudy.fromRequestModel(systematicStudyId, researcherId, request).toDto()

    fun findOneResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
        dto: SystematicStudyDto = generateDto(),
    ) = FindOneResponseModel(researcherId, systematicStudyId, dto)

    fun findAllResponseModel(
        amountOfStudies: Int,
        researcherId: UUID = this.researcher,
    ) = FindAllResponseModel(
        researcherId,
        List(amountOfStudies) { generateDto(id = UUID.randomUUID(), ownerId = researcherId) }
    )

    fun emptyFindAllResponseModel(
        researcherId: UUID = this.researcher,
        owner: UUID? = null,
    ) = FindAllResponseModel(
        researcherId = researcherId,
        ownerId = owner,
        systematicStudies = emptyList(),
    )

    fun findAllByOwnerResponseModel(
        amountOfStudies: Int,
        owner: UUID = this.owner,
        researcherId: UUID = this.researcher,
    ) = FindAllResponseModel(
        researcherId = researcherId,
        ownerId = owner,
        systematicStudies = List(amountOfStudies) {
            generateDto(id = UUID.randomUUID(), ownerId = owner, collaborators = setOf(researcherId))
        },
    )

    fun updateRequestModel(
        title: String? = null,
        description: String? = null,
    ) = UpdateRequestModel(title, description)

    fun updateResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
    ) = ResponseModel(researcherId, systematicStudyId)

    operator fun component1() = researcher

    operator fun component2() = systematicStudy

    operator fun component3() = owner
}