package br.all.application.review.update

import br.all.application.researcher.repository.ReviewerRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.toDto
import br.all.application.review.util.FakeSystematicStudyRepository
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.review.SystematicStudy
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ChangeSystematicStudyOwnerServiceTest {
    @MockK
    private lateinit var researcherRepository : ReviewerRepository
    private lateinit var systematicStudyRepository : SystematicStudyRepository
    private lateinit var sut : ChangeSystematicStudyOwnerService

    @BeforeEach
    fun setUp() {
        systematicStudyRepository = FakeSystematicStudyRepository()
        sut = ChangeSystematicStudyOwnerService(systematicStudyRepository, researcherRepository)
    }

    @Test
    fun `Should the owner be successfully changed`() {
        val reviewId = UUID.randomUUID()
        val newOwnerId = UUID.randomUUID()
        val systematicStudy = SystematicStudy(SystematicStudyId(reviewId), "Some title", "Some description",
            ResearcherId(UUID.randomUUID()))

        every { researcherRepository.existsById(newOwnerId) } returns true
        systematicStudyRepository.create(systematicStudy.toDto())
        systematicStudy.changeOwner(ResearcherId(newOwnerId))
        sut.changeOwner(reviewId, newOwnerId)

        val updatedDto = systematicStudyRepository.findById(reviewId)
        assertEquals(newOwnerId, updatedDto?.owner)
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent systematic study`() {
        val review = UUID.randomUUID()
        val newOwner = UUID.randomUUID()

        every { researcherRepository.existsById(newOwner) } returns true

        assertAll("Throw for nonexistent study", {
            val exception = assertThrows<NoSuchElementException> { sut.changeOwner(review, newOwner) }
            assertEquals("Cannot find a systematic study with id: $review", exception.message)
        })
    }

    @Test
    fun `Should throw NoSuchElementException for nonexistent new owner`() {
        val reviewId = UUID.randomUUID()
        val newOwner = UUID.randomUUID()
        val study = SystematicStudy(SystematicStudyId(reviewId), "Some title", "Some description",
            ResearcherId(UUID.randomUUID())).toDto()

        every { researcherRepository.existsById(newOwner) } returns false
        systematicStudyRepository.create(study)

        val exception = assertThrows<NoSuchElementException> { sut.changeOwner(reviewId, newOwner) }
        assertEquals("The id $newOwner does not belong to any existent researcher!", exception.message)
    }
}