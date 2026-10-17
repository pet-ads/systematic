package br.all.domain.model.reviewmode

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.study.StudyReview
import br.all.domain.shared.user.ResearcherId
import br.all.infrastructure.study.StudyReviewId

class ReviewerStudy(
    val studyId: StudyReviewId,
    val systematicStudyId: SystematicStudyId,
    val researcherId: ResearcherId,
    val studyReview: StudyReview,
)