package br.all.modules.infraestruture.study

import br.all.modules.application.study.create.StudyReviewDto
import br.all.modules.application.study.create.StudyReviewRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Repository

@Repository
class StudyReviewRepositoryImpl (val repository: MongoStudyReviewRepository): StudyReviewRepository {
    override fun create(studyReviewDto: StudyReviewDto) {
        val mapper = Mappers.getMapper(StudyReviewDbMapper::class.java)
        repository.save(mapper.toDocument(studyReviewDto))
    }
}

