package br.all.application.review.util

import br.all.application.protocol.repository.toDto
import br.all.application.review.find.services.FindAllSystematicStudiesService.FindByOwnerRequest
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.domain.model.protocol.Protocol
import br.all.domain.model.review.SystematicStudy
import br.all.domain.model.review.toSystematicStudyId
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel as CreateResponseModel
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel as FindAllResponseModel
import br.all.application.review.find.services.FindSystematicStudyService.RequestModel as FindOneRequestModel
import br.all.application.review.find.services.FindSystematicStudyService.ResponseModel as FindOneResponseModel
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

    fun protocolDto(systematicStudyId: UUID = systematicStudy) = Protocol
        .write(systematicStudyId.toSystematicStudyId(), emptySet())
        .build()
        .toDto()

    fun createRequestModel(
        researcherId: UUID = researcher,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        collaborators: Set<UUID> = emptySet()
    ) = CreateRequestModel(researcherId, title, description, collaborators)

    fun createResponseModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
    ) = CreateResponseModel(researcherId, systematicStudyId)

    fun dtoFromCreateRequest(
        request: CreateRequestModel,
        systematicStudyId: UUID = systematicStudy,
    ) = SystematicStudy.fromRequestModel(systematicStudyId, request).toDto()

    fun findOneRequestModel(
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
    ) = FindOneRequestModel(researcherId, systematicStudyId)

    fun findOneResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
        dto: SystematicStudyDto = generateDto(),
    ) = FindOneResponseModel(researcherId, systematicStudyId, dto)

    fun findByOwnerRequest(
        researcherId: UUID = researcher,
        ownerId: UUID = owner,
    ) = FindByOwnerRequest(researcherId, ownerId)

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
        researcherId: UUID = researcher,
        systematicStudyId: UUID = systematicStudy,
        title: String? = null,
        description: String? = null,
    ) = UpdateRequestModel(researcherId, systematicStudyId, title, description)

    fun updateResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
    ) = ResponseModel(researcherId, systematicStudyId)

    operator fun component1() = researcher

    operator fun component2() = systematicStudy

    operator fun component3() = owner
}