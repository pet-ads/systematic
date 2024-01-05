package br.all.application.review.util

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel as CreateResponseModel
import br.all.application.review.find.services.FindAllSystematicStudyService.ResponseModel as FindAllResponseModel
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel as FindOneResponseModel

class TestDataFactory {
    private val faker = Faker()
    val researcherId = UUID.randomUUID()
    val systematicStudyId = UUID.randomUUID()

    fun generateDto(
        systematicStudyId: UUID = this.systematicStudyId,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        ownerId: UUID = this.researcherId,
        collaborators: Set<UUID> = emptySet(),
    ) = SystematicStudyDto(
        systematicStudyId,
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
        researcherId: ResearcherId,
        systematicStudyId: UUID,
    ) = CreateResponseModel(researcherId.value, systematicStudyId)

    fun createDtoFromCreateRequestModel(
        systematicStudyId: UUID,
        researcherId: ResearcherId,
        request: CreateRequestModel,
    ) = SystematicStudy.fromRequestModel(systematicStudyId, researcherId.value, request).toDto()

    fun findOneResponseModel(
        researcherId: UUID = this.researcherId,
        systematicStudyId: UUID = this.systematicStudyId,
        dto: SystematicStudyDto = generateDto(),
    ) = FindOneResponseModel(researcherId, systematicStudyId, dto)

    fun findAllResponseModel(
        vararg systematicStudies: SystematicStudyDto,
        researcherId: UUID = this.researcherId,
    ) = FindAllResponseModel(researcherId, systematicStudies.toList())
}