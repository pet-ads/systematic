package br.all.search.persistence

import br.all.infrastructure.search.MongoSearchSessionRepository
import br.all.infrastructure.shared.toNullable
import br.all.search.shared.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
@SpringBootTest
class MongoSearchSessionRepositoryTest(
    @Autowired private val sut: MongoSearchSessionRepository,
) {
    private lateinit var testDataFactory: TestDataFactory

    @BeforeEach
    fun setUp() {
        testDataFactory = TestDataFactory()
        sut.deleteAll()
    }
    @AfterEach
    fun tearDown() = sut.deleteAll()

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When the CRUD is succeed")
    inner class WhenTheCrudIsSucceed {
        @Test
        fun `should save a new search session`() {
            val sessionId = UUID.randomUUID()
            val document = testDataFactory.searchSessionDocument(id = sessionId)

            sut.save(document)
            Assertions.assertEquals(document, sut.findById(sessionId).toNullable())
        }

        @Test
        fun `should update an existent search session`() {
            val systematicStudyId = UUID.randomUUID()
            val sessionId = UUID.randomUUID()
            val oldDocument = testDataFactory.searchSessionDocument(
                id = sessionId, systematicStudyId = systematicStudyId
            )

            sut.save(oldDocument)

            val newDocument = testDataFactory.searchSessionDocument(
                id = sessionId,
                systematicStudyId = systematicStudyId,
                searchString = "NewSearchString"
            )

            sut.save(newDocument)
            val updatedSearchSession = sut.findById(sessionId).toNullable()
            Assertions.assertEquals("NewSearchString", updatedSearchSession?.searchString)
        }

        @Test
        fun `should find a existent search session`() {
            val sessionId = UUID.randomUUID()
            val document = testDataFactory.searchSessionDocument(sessionId)

            sut.save(document)
            Assertions.assertNotEquals(null, sut.findById(sessionId).toNullable())
        }

        @Test
        fun `should find all existent search sessions`() {
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID()))
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID()))
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID()))

            Assertions.assertEquals(3, sut.findAll().size)
        }

        @Test
        fun `should find all existent search sessions of given systematic study`() {
            val systematicStudyId = UUID.randomUUID()

            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID(), systematicStudyId = systematicStudyId))
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID(), systematicStudyId = systematicStudyId))
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID(), systematicStudyId = systematicStudyId))
            sut.save(testDataFactory.searchSessionDocument(id = UUID.randomUUID(), systematicStudyId = UUID.randomUUID()))

            Assertions.assertEquals(3, sut.findAllBySystematicStudyId(systematicStudyId).size)
        }

        @Disabled
        @Test
        fun `should find all search sessions of given systematic study and source`(){
            val systematicStudyId = UUID.randomUUID()
            val source = "SearchedSource"

            sut.save(testDataFactory.searchSessionDocument(
                id = UUID.randomUUID(), systematicStudyId = systematicStudyId, source = source))
            sut.save(testDataFactory.searchSessionDocument(
                id = UUID.randomUUID(), systematicStudyId = systematicStudyId, source = source))
            sut.save(testDataFactory.searchSessionDocument(
                id = UUID.randomUUID(), systematicStudyId = systematicStudyId, source = source))
            sut.save(testDataFactory.searchSessionDocument(
                id = UUID.randomUUID(), systematicStudyId = systematicStudyId, source = "WrongSource"))

            Assertions.assertEquals(3, sut.findAllBySource(systematicStudyId, source).size)
        }
    }


}