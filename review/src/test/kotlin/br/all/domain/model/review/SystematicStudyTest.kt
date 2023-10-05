package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class SystematicStudyTest {
    private lateinit var sut : SystematicStudy
    
    @BeforeEach
    fun setUp() {
        val reviewId = ReviewId(UUID.randomUUID())
        val owner = ResearcherId(UUID.randomUUID())
        sut = SystematicStudy(reviewId, "Some title", "Some description", owner)
    }

    @ParameterizedTest
    @CsvSource("'',Some description", "Some title,''")
    fun `Should not create systematic study without title or description`(title: String, description: String){
        assertThrows<IllegalArgumentException> {
            SystematicStudy(ReviewId(UUID.randomUUID()), title, description, ResearcherId(UUID.randomUUID()))
        }
    }

    @Test
    fun `Should owner be a collaborator`(){
        val ownerId = sut.owner
        assertTrue(sut.containsCollaborator(ownerId))
    }

    @Test
    fun `Should not allow removing owner from collaborators`(){
        val ownerId = sut.owner
        assertThrows<IllegalStateException> {  sut.removeCollaborator(ownerId) }
    }

    @Test
    fun `Should remove valid collaborator`(){
        val researcherId = ResearcherId(UUID.randomUUID())
        val localSut = SystematicStudy(sut.reviewId, sut.title, sut.description, sut.owner, mutableSetOf(researcherId))

        localSut.removeCollaborator(researcherId)

        assertFalse(localSut.containsCollaborator(researcherId))
    }

    @Test
    fun `Should throw if try to remove absent collaborator`(){
        val absentResearcher = ResearcherId(UUID.randomUUID())
        assertThrows<NoSuchElementException> { sut.removeCollaborator(absentResearcher) }
    }

    @Test
    fun `Should add new collaborator`(){
        sut.addCollaborator(ResearcherId(UUID.randomUUID()))
        assertEquals(2, sut.collaborators.size)
    }

    @Test
    fun `Should add new owner to collaborators if not present yet`(){
        val newOwner = ResearcherId(UUID.randomUUID())

        sut.changeOwner(newOwner)

        assertAll("change owner",
            { assertTrue(sut.containsCollaborator(newOwner)) },
            { assertEquals(sut.owner, newOwner)}
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["New title", "T"])
    fun `Should successfully change the title`(title: String) {
        assertAll("changing the title",
            { assertDoesNotThrow { sut.rename(title) } },
            { assertEquals(title, sut.title) }
        )
    }

    @ParameterizedTest(name = "[{index}]: title = \"{0}\"")
    @ValueSource(strings = ["", " "])
    fun `Should throw IllegalArgumentException when trying to assign any kind of empty title`(title: String) {
        assertThrows<IllegalArgumentException> { sut.rename(title) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["New description", "D"])
    fun `Should successfully change the description`(description: String) {
        assertAll("changing the description",
            { assertDoesNotThrow { (sut.changeDescription(description)) } },
            { assertEquals(description, sut.description) }
        )
    }
}