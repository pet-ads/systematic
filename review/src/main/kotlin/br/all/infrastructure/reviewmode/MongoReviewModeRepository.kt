package br.all.infrastructure.reviewmode

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.UUID

interface MongoReviewModeRepository : MongoRepository<ReviewModeDocument, UUID>
