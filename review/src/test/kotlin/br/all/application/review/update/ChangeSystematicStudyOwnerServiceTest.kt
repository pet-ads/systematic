package br.all.application.review.update

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.ReviewId
import br.all.domain.model.review.SystematicStudy
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ChangeSystematicStudyOwnerServiceTest {
    @MockK
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    @MockK
    private lateinit var researcherRepository : ResearcherRepository
    private lateinit var sut : ChangeSystematicStudyOwnerService

    @BeforeEach
    fun setUp() {
        sut = ChangeSystematicStudyOwnerService(systematicStudyRepository, researcherRepository)
    }

    @Test
    fun `Should the owner be successfully changed`() {
        val reviewId = UUID.randomUUID()
        val newOwnerId = UUID.randomUUID()
        val systematicStudy = SystematicStudy(ReviewId(reviewId), "Some title", "Some description",
            ResearcherId(UUID.randomUUID()))

        every { researcherRepository.existsById(newOwnerId) } returns true
        every { systematicStudyRepository.findById(reviewId) } returns systematicStudy.toDto()
        systematicStudy.changeOwner(ResearcherId(newOwnerId))
        every { systematicStudyRepository.create(systematicStudy.toDto()) } returns Unit

        assertDoesNotThrow { sut.changeOwner(reviewId, newOwnerId) }
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent systematic study`() {
        val review = UUID.randomUUID()
        val newOwner = UUID.randomUUID()

        every { researcherRepository.existsById(newOwner) } returns true
        every { systematicStudyRepository.findById(review) } returns null

        assertAll("Throw for nonexistent study", {
            val exception = assertThrows<NoSuchElementException> { sut.changeOwner(review, newOwner) }
            assertEquals("Cannot find a systematic study with id: $review", exception.message)
        })
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent new owner`() {
        val reviewId = UUID.randomUUID()
        val newOwner = UUID.randomUUID()
        val study = SystematicStudy(ReviewId(reviewId), "Some title", "Some description",
            ResearcherId(UUID.randomUUID())).toDto()

        every { researcherRepository.existsById(newOwner) } returns false
        every { systematicStudyRepository.findById(reviewId) } returns study

        assertAll("Throw for nonexistent new owner", {
            val exception = assertThrows<NoSuchElementException> { sut.changeOwner(reviewId, newOwner) }
            assertEquals("The id $newOwner does not belong to any existent researcher!", exception.message)
        })
    }
}