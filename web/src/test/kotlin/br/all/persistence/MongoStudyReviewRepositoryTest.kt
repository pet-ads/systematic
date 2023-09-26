package br.all.persistence

import br.all.infrastructure.study.MongoStudyReviewRepository
import br.all.infrastructure.study.StudyReviewDocument
import br.all.infrastructure.study.StudyReviewIdGeneratorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class MongoStudyReviewRepositoryTest (
    @Autowired private val sut: MongoStudyReviewRepository,
    @Autowired private val idService: StudyReviewIdGeneratorService
) {

    @BeforeEach
    fun setUp() = sut.deleteAll()

    @Test
    fun `Should insert a study review`(){
        val study = studyReviewOf(UUID.randomUUID())
        sut.insert(study)
        assertTrue(sut.findById(study.id).isPresent)
    }

    @Test
    fun `Should find all study reviews of a review`(){
        val reviewId = UUID.randomUUID()
        sut.insert(studyReviewOf(reviewId))
        sut.insert(studyReviewOf(reviewId))
        sut.insert(studyReviewOf(UUID.randomUUID()))
        val result = sut.findAllByReviewId(reviewId)
        assertTrue(result.size == 2)
    }

    @Test
    fun `Should find a study review in a review`(){
        val reviewId = UUID.randomUUID()
        val studyReview = studyReviewOf(reviewId)
        sut.insert(studyReview)
        val result = sut.findByReviewIdAndId(reviewId, studyReview.id)
        assertEquals(studyReview.id, result.id)
    }


    @Test
    fun `Should generate id sequence`(){
        val id = idService.next()
        val nextId = idService.next()
        assertEquals(id + 1, nextId)
    }

    fun studyReviewOf(reviewId: UUID) : StudyReviewDocument{
        val id = idService.next()
        return StudyReviewDocument(
            id,
            reviewId,
            "Test title",
            2023,
            "Lucas",
            "JSS",
            "Mussum Ipsum, cacilds vidis litro abertis. Admodum accumsan disputationi eu sit.",
            setOf("PET", "IFSP"),
            emptyList(),
            "",
            setOf("Scopus", "Springer"),
            setOf("Criteria A", "Criteria B"),
            mapOf(Pair(UUID.randomUUID(), "Form")),
            mapOf(Pair(UUID.randomUUID(), "Quality")),
            "",
            "LOW",
            "UNCLASSIFIED",
            "UNCLASSIFIED"
        )
    }
}