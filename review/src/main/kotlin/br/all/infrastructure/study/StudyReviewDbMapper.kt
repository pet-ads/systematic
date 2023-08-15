package br.all.infrastructure.study

import br.all.application.study.create.StudyReviewDto
import org.mapstruct.Mapper

@Mapper
interface StudyReviewDbMapper {
    fun toDto(document: StudyReviewDocument): StudyReviewDto
    fun toDocument(dto: StudyReviewDto): StudyReviewDocument
}

