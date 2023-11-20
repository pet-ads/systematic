package br.all.study.persistence

import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewId
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import br.all.study.utils.TestDataFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class MongoStudyReviewRepositoryTest (
    @Autowired private val sut: MongoStudyReviewRepository,
    @Autowired private val idService: StudyReviewIdGeneratorService
) {

    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory(idService)
        sut.deleteAll()
    }

    @AfterEach
    fun teardown() = sut.deleteAll()

    @Test
    fun `Should insert a study review`(){
        val study = factory.reviewDocumentOfId(UUID.randomUUID())
        sut.insert(study)
        assertTrue(sut.findById(study.id) != null)
    }

    @Test
    fun `Should find all study reviews of a review`(){
        val reviewId = UUID.randomUUID()
        sut.insert(factory.reviewDocumentOfId(reviewId))
        sut.insert(factory.reviewDocumentOfId(reviewId))
        sut.insert(factory.reviewDocumentOfId(UUID.randomUUID()))
        val result = sut.findAllById_ReviewId(reviewId)
        assertTrue(result.size == 2)
    }

    @Test
    fun `Should find a study review in a review`(){
        val reviewId = UUID.randomUUID()
        val studyReview = factory.reviewDocumentOfId(reviewId)
        sut.insert(studyReview)
        val result = sut.findById(studyReview.id)
        assertEquals(studyReview.id.studyId, result?.id?.studyId)
        assertEquals(reviewId, result?.id?.reviewId)
    }


    @Test
    fun `Should generate id sequence`(){
        val id = idService.next()
        val nextId = idService.next()
        assertEquals(id + 1, nextId)
    }
}