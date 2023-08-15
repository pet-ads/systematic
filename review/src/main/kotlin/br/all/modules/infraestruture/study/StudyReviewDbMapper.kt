package br.all.modules.infraestruture.study

import br.all.modules.application.study.create.StudyReviewDto
import org.mapstruct.Mapper

@Mapper
interface StudyReviewDbMapper {
    fun toDto(document: StudyReviewDocument): StudyReviewDto
    fun toDocument(dto: StudyReviewDto): StudyReviewDocument
}

