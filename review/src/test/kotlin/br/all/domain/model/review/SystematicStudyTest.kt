package br.all.domain.model.review

import br.all.domain.model.researcher.ResearcherId
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
        sut = SystematicStudy(systematicStudyId, "Some title", "Some description", owner)
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
                val collaborators = mutableSetOf(ResearcherId(UUID.randomUUID()))

                assertDoesNotThrow { SystematicStudy(id, "Title", "Description", owner, collaborators) }
            }

            @Test
            fun `should owner be a collaborator`() {
                val ownerId = sut.owner
                assertTrue(ownerId in sut.collaborators)
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

                assertThrows<IllegalArgumentException> { SystematicStudy(id, title, description, owner) }
            }
        }
    }

    @Nested
    @DisplayName("When adding new collaborators")
    inner class WhenAddingNewCollaborators {
        @Test
        @Tag("ValidClasses")
        fun `should add new collaborator`() {
            sut.addCollaborator(ResearcherId(UUID.randomUUID()))
            assertEquals(2, sut.collaborators.size)
        }

        @Test
        @Tag("InvalidClasses")
        fun `should not add duplicated researchers`() {
            val duplicatedResearcher = ResearcherId(UUID.randomUUID())
            val localSut = SystematicStudy(
                SystematicStudyId(UUID.randomUUID()),
                "Title",
                "Description",
                ResearcherId(UUID.randomUUID()),
                mutableSetOf(duplicatedResearcher),
            )
            localSut.addCollaborator(duplicatedResearcher)
            assertEquals(1, localSut.collaborators.count { it == duplicatedResearcher })
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
            fun `should remove valid collaborator`(){
                val researcherId = ResearcherId(UUID.randomUUID())
                val id = sut.id.value()
                val localSut = SystematicStudy(
                    SystematicStudyId(id),
                    sut.title,
                    sut.description,
                    sut.owner,
                    mutableSetOf(researcherId)
                )

                localSut.removeCollaborator(researcherId)

                assertFalse(researcherId in localSut.collaborators)
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to remove")
        inner class AndBeingUnableToRemove {
            @Test
            fun `should not allow removing owner from collaborators`(){
                val ownerId = sut.owner
                assertThrows<IllegalStateException> {  sut.removeCollaborator(ownerId) }
            }

            @Test
            fun `should throw if try to remove absent collaborator`(){
                val absentResearcher = ResearcherId(UUID.randomUUID())
                assertThrows<NoSuchElementException> { sut.removeCollaborator(absentResearcher) }
            }
        }
    }

    @Nested
    @DisplayName("When changing the owner")
    inner class WhenChangingTheOwner {
        @Test
        @Tag("ValidClasses")
        fun `should add new owner to collaborators if not present yet`(){
            val newOwner = ResearcherId(UUID.randomUUID())
            sut.changeOwner(newOwner)

            Assertions.assertAll("change owner",
                { assertTrue(newOwner in sut.collaborators) },
                { assertEquals(sut.owner, newOwner) }
            )
        }
    }

    @Nested
    @DisplayName("When changing the title")
    inner class WhenChangingTheTitle {
        @ParameterizedTest
        @Tag("ValidClasses")
        @ValueSource(strings = ["New title", "T"])
        fun `should successfully change the title`(title: String) {
            assertDoesNotThrow { sut.title = "New title" }
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
            assertDoesNotThrow { sut.description = description }
        }

        @ParameterizedTest(name = "[{index}]: description = \"{0}\"")
        @Tag("InvalidClasses")
        @ValueSource(strings = ["", " "])
        fun `should throw IllegalArgumentException when trying to assign invalid descriptions`(description: String) {
            assertThrows<IllegalArgumentException> { sut.description = description }
        }
    }
}