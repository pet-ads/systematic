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
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val owner = ResearcherId(UUID.randomUUID())
        sut = SystematicStudy(systematicStudyId, "Some title", "Some description", owner)
    }

    @ParameterizedTest
    @CsvSource("'',Some description", "Some title,''")
    fun `Should not create systematic study without title or description`(title: String, description: String){
        assertThrows<IllegalArgumentException> {
            SystematicStudy(SystematicStudyId(UUID.randomUUID()), title, description, ResearcherId(UUID.randomUUID()))
        }
    }

    @Test
    fun `Should owner be a collaborator`(){
        val ownerId = sut.owner
        assertTrue(ownerId in sut.collaborators)
    }

    @Test
    fun `Should not allow removing owner from collaborators`(){
        val ownerId = sut.owner
        assertThrows<IllegalStateException> {  sut.removeCollaborator(ownerId) }
    }

    @Test
    fun `Should remove valid collaborator`(){
        val researcherId = ResearcherId(UUID.randomUUID())
        val localSut = SystematicStudy(sut.systematicStudyId, sut.title, sut.description, sut.owner, mutableSetOf(researcherId))

        localSut.removeCollaborator(researcherId)

        assertFalse(researcherId in localSut.collaborators)
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
            { assertTrue(newOwner in sut.collaborators) },
            { assertEquals(sut.owner, newOwner)}
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["New title", "T"])
    fun `Should successfully change the title`(title: String) {
        assertDoesNotThrow { sut.title = "New title" }
    }

    @ParameterizedTest(name = "[{index}]: title = \"{0}\"")
    @ValueSource(strings = ["", " "])
    fun `Should throw IllegalArgumentException when trying to assign any kind of empty title`(title: String) {
        assertThrows<IllegalArgumentException> { sut.title = title }
    }

    @ParameterizedTest
    @ValueSource(strings = ["New description", "D"])
    fun `Should successfully change the description`(description: String) {
        assertDoesNotThrow { sut.description = description }
    }

    @ParameterizedTest(name = "[{index}]: description = \"{0}\"")
    @ValueSource(strings = ["", " "])
    fun `Should throw IllegalArgumentException when trying to assign invalid descriptions`(description: String) {
        assertThrows<IllegalArgumentException> { sut.description = description }
    }
}