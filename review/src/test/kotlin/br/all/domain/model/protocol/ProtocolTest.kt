package br.all.domain.model.protocol

import br.all.domain.model.question.QuestionId
import br.all.domain.shared.valueobject.Language
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("UnitTest")
class ProtocolTest {
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() = run { factory = TestDataFactory() }

    @Nested
    @DisplayName("When writing protocols")
    inner class WhenWritingProtocols {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And filling out the field correctly")
        inner class AndFillingOutTheFieldCorrectly {
            @Test
            fun `should successfully create a protocol`() {
                assertDoesNotThrow { factory.createProtocol() }
            }
        }
        
        @Nested
        @Tag("InvalidClasses")
        @DisplayName("But providing invalid input")
        inner class ButProvidingInvalidInput {
            @ParameterizedTest(name = "[{index}] goal=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should throw when the goal is blank`(goal: String) {
                assertThrows<IllegalArgumentException> { factory.createProtocol(goal = goal) }
            }

            @ParameterizedTest(name = "[{index}] justification=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should throw blank justification`(justification: String) {
                val e = assertThrows<IllegalArgumentException> { factory.createProtocol(justification = justification) }
                assertEquals("The justification cannot be an empty string", e.message)
            }

            @ParameterizedTest(name = "[{index}] searchString=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should throw if the search string is blank`(searchString: String) {
                val e = assertThrows<IllegalArgumentException> { factory.createProtocol(searchString = searchString) }
                assertEquals("The search string must not be blank!", e.message)
            }

            @ParameterizedTest(name = "[{index}] sourcesSelectionCriteria=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should not create protocols with blank sources selection criteria`(sourcesSelectionCriteria: String) {
                val e = assertThrows<IllegalArgumentException> {
                    factory.createProtocol(sourcesSelectionCriteria = sourcesSelectionCriteria)
                }
                assertEquals("The sources selection criteria description must not be blank", e.message)
            }

            @ParameterizedTest(name = "[{index}] searchMethod=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should not the search method be blank`(searchMethod: String) {
                val e = assertThrows<IllegalArgumentException> { factory.createProtocol(searchMethod = searchMethod) }
                assertEquals("The search method description must not be blank", e.message)
            }

            @ParameterizedTest(name = "[{index}] studyTypeDefinition=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should throw when the provided study type definition is blank`(studyTypeDefinition: String) {
                val e = assertThrows<IllegalArgumentException> {
                    factory.createProtocol(studyTypeDefinition = studyTypeDefinition)
                }
                assertEquals("The study type definition must not be blank", e.message)
            }

            @ParameterizedTest(name = "[{index}] selectionProcess=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should throw for blank selection process descriptions`(selectionProcess: String) {
                val e = assertThrows<IllegalArgumentException> {
                    factory.createProtocol(selectionProcess = selectionProcess)
                }
                assertEquals("The selection process description must not be blank", e.message)
            }

            @ParameterizedTest(name = "[{index}] dataCollectionProcess=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should not the data collection process description be blank`(dataCollectionProcess: String) {
                val e = assertThrows<IllegalArgumentException> {
                    factory.createProtocol(dataCollectionProcess = dataCollectionProcess)
                }
                assertEquals("The data collection process description must not be blank", e.message)
            }

            @ParameterizedTest(name = "[{index}] analysisAndSynthesis=\"{0}\"")
            @ValueSource(strings = ["", " ", "   "])
            fun `should not the analysis and synthesis description be blank`(analysisAndSynthesis: String) {
                val e = assertThrows<IllegalArgumentException> {
                    factory.createProtocol(analysisAndSynthesis = analysisAndSynthesis)
                }
                assertEquals("The analysis and synthesis process description must not be blank", e.message)
            }
        }
    }

    @Nested
    @DisplayName("When updating the protocol")
    inner class WhenUpdatingTheProtocol {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And providing valid changes")
        inner class AndProvidingValidChanges {
            // TODO: Valid classes for changing the protocol string fields
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And doing wrong changes")
        inner class AndDoingWrongChanges {
            // TODO: Invalid classes for changing the protocol string fields
        }
    }

    @Nested
    @DisplayName("When managing research questions")
    inner class WhenManagingResearchQuestions {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And performing the updates successfully")
        inner class AndPerformingTheUpdatesSuccessfully {
            // TODO: Valid classes for adding/removing research questions to the protocol
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to change them")
        inner class AndBeingUnableToChangeThem {
            // TODO: Invalid classes for adding/removing research questions to the protocol
        }
    }

    @Nested
    @DisplayName("When managing keywords")
    inner class WhenManagingKeywords {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being able to change them")
        inner class AndBeingAbleToChangeThem {
            @Test
            fun `should add a new keyword if it is not in the protocol yet`() {
                val sut = factory.createProtocol()
                val newKeyword = "New keyword"

                sut.addKeyword(newKeyword)

                assertAll(
                    { assertEquals(2, sut.keywords.size) },
                    { assertContains(sut.keywords, newKeyword) }
                )
            }

            @Test
            fun `should do nothing when trying to add a keyword that already is in the protocol`() {
                val sut = factory.createProtocol()
                val keyword = "Keyword"

                assertAll(
                    { assertDoesNotThrow { sut.addKeyword(keyword) } },
                    { assertEquals(1, sut.keywords.size) }
                )
            }

            @Test
            fun `should successfully remove a keyword if it is not the last one`() {
                val removingKeyword = "Keyword"
                val sut = factory.createProtocol(keywords = setOf(removingKeyword, "Other keyword"))

                assertAll(
                    { assertDoesNotThrow { sut.removeKeyword(removingKeyword) } },
                    { assertEquals(1, sut.keywords.size) },
                    { assertTrue { removingKeyword !in sut.keywords } },
                )
            }
        }

        @Nested
        @DisplayName("And being unable to change any keyword")
        inner class AndBeingUnableToChangeAnyKeyword {
            @Test
            fun `should throw when trying to remove the last keyword`() {
                val removingKeyword = "Keyword"
                val sut = factory.createProtocol()

                assertThrows<IllegalStateException> { sut.removeKeyword(removingKeyword) }
            }

            @Test
            fun `should throw when trying to remove an nonexistent keyword`() {
                val sut = factory.createProtocol()
                assertThrows<NoSuchElementException> { sut.removeKeyword("Nonexistent keyword") }
            }
        }
    }

    @Nested
    @DisplayName("When managing the protocol information sources")
    inner class WhenManagingTheProtocolInformationSources {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being able to update them")
        inner class AndBeingAbleToUpdateThem {
            @Test
            fun `should add a information source if it is not in the protocol yet`() {
                val sut = factory.createProtocol()
                val newSearchSource = SearchSource("New SearchSource")

                sut.addInformationSource(newSearchSource)

                assertAll(
                    { assertEquals(2, sut.informationSources.size) },
                    { assertContains(sut.informationSources, newSearchSource) }
                )
            }

            @Test
            fun `should do nothing when trying to add a information source that already is in the protocol`() {
                val sut = factory.createProtocol()
                val existentInformationSource = SearchSource("SomeSourceWithManyPhilosophicalArticles")

                assertAll(
                    { assertDoesNotThrow { sut.addInformationSource(existentInformationSource) } },
                    { assertEquals(1, sut.informationSources.size) }
                )
            }

            @Test
            fun `should successfully remove a information source if it is not the last one`() {
                val removingSource = SearchSource("PhilosophicalSource")
                val sut = factory.createProtocol(informationSources = setOf(
                    removingSource,
                    SearchSource("Other Source"),
                ))

                sut.removeInformationSource(removingSource)

                assertAll(
                    { assertEquals(1, sut.informationSources.size) },
                    { assertTrue { removingSource !in sut.informationSources } },
                )
            }
        }

        @Nested
        @DisplayName("And failing to perform changes")
        inner class AndFailingToPerformChanges {
            @Test
            fun `should throw when trying to remove the last information source`() {
                val sut = factory.createProtocol()
                val removingSource = SearchSource("SomeSourceWithManyPhilosophicalArticles")

                assertThrows<IllegalStateException> { sut.removeInformationSource(removingSource) }
            }

            @Test
            fun `should throw when trying to remove a information source that does not exist`() {
                val sut = factory.createProtocol()
                val nonexistentSource = SearchSource("Nonexistent Source")

                assertThrows<NoSuchElementException> { sut.removeInformationSource(nonexistentSource) }
            }
        }
    }

    @Nested
    @DisplayName("When managing the studies languages")
    inner class WhenManagingTheStudiesLanguages {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And performing changes successfully")
        inner class AndPerformingChangesSuccessfully {
            @Test
            fun `should add a new language if it is not in the protocol`() {
                val sut = factory.createProtocol()
                val newLanguage = Language(Language.LangType.PORTUGUESE)

                sut.addLanguage(newLanguage)

                assertAll(
                    { assertEquals(2, sut.studiesLanguages.size) },
                    { assertContains(sut.studiesLanguages, newLanguage) },
                )
            }

            @Test
            fun `should do nothing when trying to add a language that already is in the protocol`() {
                val sut = factory.createProtocol()
                val existentLanguage = Language(Language.LangType.ENGLISH)

                assertAll(
                    { assertDoesNotThrow { sut.addLanguage(existentLanguage) } },
                    { assertEquals(1, sut.studiesLanguages.size) },
                )
            }

            @Test
            fun `should successfully remove a language if it is not the last one`() {
                val removingLanguage = Language(Language.LangType.PORTUGUESE)
                val sut = factory.createProtocol(languages = setOf(
                    removingLanguage,
                    Language(Language.LangType.ENGLISH),
                ))

                sut.removeLanguage(removingLanguage)

                assertAll(
                    { assertEquals(1, sut.studiesLanguages.size) },
                    { assertTrue { removingLanguage !in sut.studiesLanguages } },
                )
            }

        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to update them")
        inner class AndFailingToUpdateThem {
            @Test
            fun `should throw when trying to remove the last language`() {
                val sut = factory.createProtocol()
                val removingLanguage = Language(Language.LangType.ENGLISH)

                assertThrows<IllegalStateException> { sut.removeLanguage(removingLanguage) }
            }

            @Test
            fun `should throw when trying to remove a nonexistent language from protocol`() {
                val sut = factory.createProtocol()
                val nonexistentLanguage = Language(Language.LangType.PORTUGUESE)

                assertThrows<NoSuchElementException> { sut.removeLanguage(nonexistentLanguage) }
            }
        }
    }

    @Nested
    @DisplayName("When managing eligibility criteria")
    inner class WhenManagingEligibilityCriteria {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And performing changes")
        inner class AndPerformingChanges {
            @Test
            fun `should add a new criteria if it is not in the protocol`() {
                val sut = factory.createProtocol()
                val newCriterion = Criterion.toInclude("Nice thoughts")

                sut.addSelectionCriteria(newCriterion)

                assertAll(
                    { assertEquals(3, sut.selectionCriteria.size) },
                    { assertContains(sut.selectionCriteria, newCriterion) },
                )
            }

            @Test
            fun `should do nothing when trying to add a repeated criteria`() {
                val sut = factory.createProtocol()
                val repeatedCriterion = Criterion.toInclude("It has deep reflection about life")

                assertAll(
                    { assertDoesNotThrow { sut.addSelectionCriteria(repeatedCriterion) } },
                    { assertEquals(2, sut.selectionCriteria.size) },
                )
            }

            @ParameterizedTest
            @CsvSource("Nice thoughts,INCLUSION", "Bad thoughts,EXCLUSION")
            fun `should successfully remove a criteria if it is not the last of such type`(
                description: String,
                type: Criterion.CriterionType,
            ) {
                val sut = factory.createProtocol(eligibilityCriteria = setOf(
                    Criterion.toInclude("It has deep reflection about life!"),
                    Criterion.toInclude("Nice thoughts"),
                    Criterion.toExclude("It does not talk about life"),
                    Criterion.toExclude("Bad thoughts"),
                ))
                val removingCriterion = Criterion(description, type)

                assertAll(
                    { assertDoesNotThrow { sut.removeSelectionCriteria(removingCriterion) } },
                    { assertEquals(3, sut.selectionCriteria.size) },
                )
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to update them")
        inner class AndFailingToUpdateThem {
            @ParameterizedTest
            @CsvSource("It has deep reflection about life,INCLUSION", "It does not talk about life!,EXCLUSION")
            fun `should throw when trying to remove the last criteria o a type`(
                description: String, type: Criterion.CriterionType) {

                val sut = factory.createProtocol()
                val removingCriterion = Criterion(description, type)

                assertThrows<IllegalStateException> { sut.removeSelectionCriteria(removingCriterion) }
            }

            @Test
            fun `should throw when trying to remove a criteria that is not defined in the protocol`() {
                val sut = factory.createProtocol()
                val nonexistentCriterion = Criterion.toInclude("Nice thoughts")

                assertThrows<NoSuchElementException> { sut.removeSelectionCriteria(nonexistentCriterion) }
            }
        }
    }

    @Nested
    @DisplayName("When managing extraction questions")
    inner class WhenManagingExtractionQuestions {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being able to update them")
        inner class AndBeingAbleToUpdateThem {
            @Test
            fun `should add a new extraction question if it is not defined yet`() {
                val sut = factory.createProtocol()
                val newExtractionQuestion = QuestionId(UUID.randomUUID())

                sut.addExtractionQuestion(newExtractionQuestion)

                assertAll(
                    { assertEquals(1, sut.extractionQuestions.size) },
                    { assertContains(sut.extractionQuestions, newExtractionQuestion) },
                )
            }

            @Test
            fun `should do nothing when trying to add a extraction question that has already been defined`() {
                val question = QuestionId(UUID.randomUUID())
                val sut = factory.createProtocol(extractionQuestions = setOf(question))

                assertAll(
                    { assertDoesNotThrow { sut.addExtractionQuestion(question) } },
                    { assertEquals(1, sut.extractionQuestions.size) },
                )
            }

            @Test
            fun `should remove a extraction question if its present`() {
                val removingQuestion = QuestionId(UUID.randomUUID())
                val sut = factory.createProtocol(extractionQuestions = setOf(removingQuestion))

                sut.removeExtractionQuestion(removingQuestion)

                assertEquals(0, sut.extractionQuestions.size)
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to perform changes")
        inner class AndBeingUnableToPerformChanges {
            @Test
            fun `should throw when trying to remove a question that does not belongs to the protocol`() {
                val sut = factory.createProtocol()
                val removingQuestion = QuestionId(UUID.randomUUID())

                assertThrows<NoSuchElementException> { sut.removeExtractionQuestion(removingQuestion) }
            }
        }
    }

    @Nested
    @DisplayName("When managing risk of bias questions")
    inner class WhenManagingRiskOfBiasQuestions {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And providing valid changes")
        inner class AndProvidingValidChanges {
            @Test
            fun `should a new rob question successfully if it is not defined`() {
                val sut = factory.createProtocol()
                val newRobQuestion = QuestionId(UUID.randomUUID())

                sut.addRobQuestion(newRobQuestion)

                assertAll(
                    { assertEquals(1, sut.robQuestions.size) },
                    { assertContains(sut.robQuestions, newRobQuestion) },
                )
            }

            @Test
            fun `should do nothing when trying to add repeated rob questions`() {
                val repeatedQuestion = QuestionId(UUID.randomUUID())
                val sut = factory.createProtocol(robQuestions = setOf(repeatedQuestion))

                assertAll(
                    { assertDoesNotThrow { sut.addRobQuestion(repeatedQuestion) } },
                    { assertEquals(1, sut.robQuestions.size) },
                )
            }

            @Test
            fun `should existent rob questions be successfully removed`() {
                val robQuestion = QuestionId(UUID.randomUUID())
                val sut = factory.createProtocol(robQuestions = setOf(robQuestion))

                sut.removeRobQuestion(robQuestion)

                assertEquals(0, sut.robQuestions.size)
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And being unable to perform changes")
        inner class AndBeingUnableToPerformChanges {

            @Test
            fun `should throw when trying to remove nonexistent rob questions`() {
                val sut = factory.createProtocol()
                val removingQuestion = QuestionId(UUID.randomUUID())

                assertThrows<NoSuchElementException> { sut.removeRobQuestion(removingQuestion) }
            }
        }
    }
}
