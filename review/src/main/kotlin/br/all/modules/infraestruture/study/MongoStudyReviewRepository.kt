package br.all.modules.infraestruture.study

import org.springframework.data.mongodb.repository.MongoRepository

interface MongoStudyReviewRepository : MongoRepository<StudyReviewDocument, Long>