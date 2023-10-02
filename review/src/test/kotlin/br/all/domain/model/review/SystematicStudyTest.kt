package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class SystematicStudyTest {

    @Test
    fun `should not create systematic study without title`(){
        assertThrows<IllegalArgumentException> {
            SystematicStudy(
                ReviewId(UUID.randomUUID()),
                "",
                "description",
                ResearcherId(UUID.randomUUID()))
        }
    }

    @Test
    fun `should not create systematic study without description`(){
        assertThrows<IllegalArgumentException> {
            SystematicStudy(
                ReviewId(UUID.randomUUID()),
                "Title",
                "",
                ResearcherId(UUID.randomUUID()))
        }
    }

    @Test
    fun `should owner be a collaborator`(){
        val ownerId = ResearcherId(UUID.randomUUID())
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ownerId)
        assertTrue(sut.containsCollaborator(ownerId))
    }

    @Test
    fun `should not allow removing owner from collaborators`(){
        val ownerId = ResearcherId(UUID.randomUUID())
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ownerId)
        assertThrows<IllegalStateException> {  sut.removeCollaborator(ownerId) }
    }

    @Test
    fun `should remove valid collaborator`(){
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ResearcherId(UUID.randomUUID()))
        val researcherId = ResearcherId(UUID.randomUUID())
        sut.addCollaborator(researcherId)
        assertTrue(sut.containsCollaborator(researcherId))
        sut.removeCollaborator(researcherId)
        assertFalse(sut.containsCollaborator(researcherId))
    }

    @Test
    fun `should throw if try to remove absent collaborator`(){
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ResearcherId(UUID.randomUUID()))
        assertThrows<NoSuchElementException> { sut.removeCollaborator(ResearcherId(UUID.randomUUID()))}
    }

    @Test
    fun `should add new collaborator`(){
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ResearcherId(UUID.randomUUID()))
        sut.addCollaborator(ResearcherId(UUID.randomUUID()))
        assertEquals(2, sut.collaborators.size)
    }

    @Test
    fun `should add new owner to collaborators if not present yet`(){
        val sut = SystematicStudy(ReviewId(UUID.randomUUID()), "title", "description", ResearcherId(UUID.randomUUID()))
        val newOwner = ResearcherId(UUID.randomUUID())
        sut.changeOwner(newOwner)

        assertAll("change owner",
            { assertTrue(sut.containsCollaborator(newOwner)) },
            { assertEquals(sut.owner, newOwner)}
        )

    }
}