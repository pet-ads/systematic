package br.all.infrastructure.study

import br.all.application.study.create.StudyReviewDto
import br.all.application.study.create.StudyReviewRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Repository

@Repository
class StudyReviewRepositoryImpl (private val repository: MongoStudyReviewRepository): StudyReviewRepository {
    override fun create(studyReviewDto: StudyReviewDto) {
        val mapper = Mappers.getMapper(StudyReviewDbMapper::class.java)
        repository.save(mapper.toDocument(studyReviewDto))
    }
}

