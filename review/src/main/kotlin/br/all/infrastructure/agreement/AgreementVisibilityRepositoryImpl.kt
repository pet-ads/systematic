package br.all.infrastructure.agreement

import br.all.application.agreement.repository.AgreementVisibilityDto
import br.all.application.agreement.repository.AgreementVisibilityRepository
import br.all.domain.model.agreement.AgreementVisibility
import br.all.infrastructure.shared.toNullable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class AgreementVisibilityRepositoryImpl(
    private val innerRepository: MongoAgreementVisibilityRepository,
) : AgreementVisibilityRepository {

    override fun findById(systematicStudyId: UUID): AgreementVisibilityDto? =
        innerRepository.findById(systematicStudyId).toNullable()?.let {
            AgreementVisibilityDto(it.id, it.selection, it.extraction)
        }

    override fun saveOrUpdate(dto: AgreementVisibilityDto) {
        innerRepository.save(AgreementVisibilityDocument(dto.systematicStudyId, dto.selection, dto.extraction))
    }
}