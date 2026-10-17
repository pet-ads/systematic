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
            AgreementVisibilityDto(
                systematicStudyId = it.id,
                selection = AgreementVisibility.valueOf(it.selection),
                extraction = AgreementVisibility.valueOf(it.extraction),
            )
        }

    override fun saveOrUpdate(dto: AgreementVisibilityDto) {
        innerRepository.save(
            AgreementVisibilityDocument(
                id = dto.systematicStudyId,
                selection = dto.selection.name,
                extraction = dto.extraction.name,
            )
        )
    }
}