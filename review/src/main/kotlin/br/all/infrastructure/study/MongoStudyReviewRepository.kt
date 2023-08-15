package br.all.infrastructure.study

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, Long>