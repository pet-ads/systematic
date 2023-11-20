package br.all.study.persistence

import br.all.infrastructure.shared.toNullable
import br.all.infrastructure.study.MongoStudyReviewRepository
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
        factory = TestDataFactory()
        idService.reset()
        sut.deleteAll()
    }

    @AfterEach
    fun teardown() = sut.deleteAll()

    @Test
    fun `Should insert a study review`(){
        val study = factory.reviewDocument(UUID.randomUUID(), idService.next())
        sut.insert(study)
        assertTrue(sut.findById(study.id).toNullable() != null)
    }

    @Test
    fun `Should update a study review`(){
        val reviewId = UUID.randomUUID()
        val studyId = idService.next()
        val study = factory.reviewDocument(reviewId, studyId,"study")
        sut.insert(study)
        val updatedTitle = "study review"
        val updatedStudy = factory.reviewDocument(reviewId, studyId, updatedTitle)
        sut.save(updatedStudy)
        assertEquals(updatedTitle, sut.findById(study.id).toNullable()?.title)
    }

    @Test
    fun `Should find all study reviews of a review`(){
        val reviewId = UUID.randomUUID()
        sut.insert(factory.reviewDocument(reviewId, idService.next()))
        sut.insert(factory.reviewDocument(reviewId, idService.next()))
        sut.insert(factory.reviewDocument(UUID.randomUUID(), idService.next()))
        val result = sut.findAllByIdReviewId(reviewId)
        assertTrue(result.size == 2)
    }

    @Test fun `Should update study reviews selection status`(){
        val reviewId = UUID.randomUUID()
        val studyId = idService.next()
        val studyReview = factory.reviewDocument(reviewId, studyId)
        sut.insert(studyReview)
        val newStatus = "INCLUDED"
        sut.findAndUpdateAttributeById(studyReview.id, "selectionStatus", newStatus)
        val result = sut.findById(studyReview.id)
        assertEquals(newStatus, result.toNullable()?.selectionStatus)
    }

    @Test
    fun `Should find a study review in a review`(){
        val reviewId = UUID.randomUUID()
        val studyReview = factory.reviewDocument(reviewId, idService.next(),"study")
        sut.insert(studyReview)
        val result = sut.findById(studyReview.id)
        assertEquals(studyReview.id.studyId, result.toNullable()?.id?.studyId)
        assertEquals(reviewId, result.toNullable()?.id?.reviewId)
    }

    @Test
    fun `Should generate id sequence`(){
        val id = idService.next()
        val nextId = idService.next()
        assertEquals(id + 1, nextId)
    }
}