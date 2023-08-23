package br.all.application.study.find

import br.all.application.study.repository.StudyReviewDto
import java.util.UUID

data class StudyReviewsResponseModel(val reviewId: UUID, val studyReviews: List<StudyReviewDto> )
