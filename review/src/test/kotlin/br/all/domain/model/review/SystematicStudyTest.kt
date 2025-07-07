package br.all.domain.model.review

import br.all.domain.model.collaboration.Collaboration
import br.all.domain.model.collaboration.CollaborationId
import br.all.domain.model.collaboration.CollaborationPermission
import br.all.domain.model.user.ResearcherId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

@Tag("UnitTest")
class SystematicStudyTest {
    private lateinit var sut : SystematicStudy

    @BeforeEach
    fun setUp() {
        val systematicStudyId = SystematicStudyId(UUID.randomUUID())
        val owner = ResearcherId(UUID.randomUUID())
        sut = SystematicStudy(systematicStudyId, "Some title", "Some description", owner, setOf())
    }

    @Nested
    @DisplayName("When creating a new Systematic Study")
    inner class WhenCreatingANewSystematicStudy {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should successfully create an systematic study`() {
                val id = SystematicStudyId(UUID.randomUUID())
                val owner = ResearcherId(UUID.randomUUID())
                val collaborators = mutableSetOf(CollaborationId(UUID.randomUUID()))

                assertDoesNotThrow { SystematicStudy(id, "Title", "Description", owner, collaborators) }
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And providing invalid states")
        inner class AndProvidingInvalidStates {
            @ParameterizedTest(name = "[{index}]: title=\"{0}\", description=\"{1}\"")
            @CsvSource("'',Some description", "Some title,''", "'',''")
            fun `should not create systematic study without title or description`(title: String, description: String){
                val id = SystematicStudyId(UUID.randomUUID())
                val owner = ResearcherId(UUID.randomUUID())

                assertThrows<IllegalArgumentException> { SystematicStudy(id, title, description, owner, setOf()) }
            }
        }
    }

    @Nested
    @DisplayName("When adding new collaborators")
    inner class WhenAddingNewCollaborators {
        @Test
        @Tag("InvalidClasses")
        fun `should not add duplicated researchers`() {
            val duplicatedResearcher = CollaborationId(UUID.randomUUID())
            
            sut.addCollaborator(duplicatedResearcher)
            sut.addCollaborator(duplicatedResearcher)

            assertEquals(1, sut.collaborators.count { it == duplicatedResearcher })
        }
    }

    @Nested
    @DisplayName("When removing collaborators")
    inner class WhenRemovingCollaborators {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should remove valid collaborator`() {
                val researcherId = ResearcherId(UUID.randomUUID())
                val collabId = CollaborationId(UUID.randomUUID())
                val collaboration = Collaboration(collabId, sut.id as SystematicStudyId, researcherId, permissions = setOf(CollaborationPermission.VIEW))

                sut.addCollaborator(collabId)
                assertTrue(collabId in sut.collaborators, "researcher was not even added" )

                sut.removeCollaborator(collaboration)
                assertFalse(collabId in sut.collaborators, "researcher was not removed")
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to remove")
        inner class AndBeingUnableToRemove {
            @Test
            fun `should not allow removing owner from collaborators`(){
                val ownerId = sut.owner
                val collaboration = Collaboration(CollaborationId(UUID.randomUUID()), sut.id as SystematicStudyId, ownerId, permissions = setOf(CollaborationPermission.VIEW))
                assertThrows<NoSuchElementException> {  sut.removeCollaborator(collaboration) }
            }

            @Test
            fun `should throw if try to remove absent collaborator`(){
                val absentResearcher = ResearcherId(UUID.randomUUID())
                val collaboration = Collaboration(CollaborationId(UUID.randomUUID()), sut.id as SystematicStudyId, absentResearcher, permissions = setOf(CollaborationPermission.VIEW))
                assertThrows<NoSuchElementException> { sut.removeCollaborator(collaboration) }
            }
        }
    }

    @Nested
    @DisplayName("When changing the title")
    inner class WhenChangingTheTitle {
        @ParameterizedTest
        @Tag("ValidClasses")
        @ValueSource(strings = ["New title", "T"])
        fun `should successfully change the title`(title: String) {
            assertAll(
                { assertDoesNotThrow { sut.title = title } },
                { assertEquals(title, sut.title) }
            )
        }

        @ParameterizedTest(name = "[{index}]: title = \"{0}\"")
        @Tag("InvalidClasses")
        @ValueSource(strings = ["", " "])
        fun `should throw IllegalArgumentException when trying to assign any kind of empty title`(title: String) {
            assertThrows<IllegalArgumentException> { sut.title = title }
        }
    }

    @Nested
    @DisplayName("When changing the description")
    inner class WhenChangingTheDescription {
        @ParameterizedTest
        @Tag("InvalidClasses")
        @ValueSource(strings = ["New description", "D"])
        fun `should successfully change the description`(description: String) {
            assertAll(
                { assertDoesNotThrow { sut.description = description } },
                { assertEquals(description, sut.description) },
            )
        }

        @ParameterizedTest(name = "[{index}]: description = \"{0}\"")
        @Tag("InvalidClasses")
        @ValueSource(strings = ["", " "])
        fun `should throw IllegalArgumentException when trying to assign invalid descriptions`(description: String) {
            assertThrows<IllegalArgumentException> { sut.description = description }
        }
    }
}