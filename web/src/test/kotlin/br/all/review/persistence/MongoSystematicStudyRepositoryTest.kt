package br.all.review.persistence

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.infrastructure.shared.toNullable
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@Tag("IntegrationTest")
class MongoSystematicStudyRepositoryTest(
    @Autowired private val sut: MongoSystematicStudyRepository,
) {
    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        sut.deleteAll()
    }
    @Nested
    @Tag("ValidClasses")
    @DisplayName("When the CRUD is succeed")
    inner class WhenTheCrudIsSucceed {
        @Test
        fun `Should save a new systematic study`() {
            val systematicStudyId = UUID.randomUUID()
            val document = generateDocument(systematicStudyId)

            sut.save(document)
            assertEquals(document, sut.findById(systematicStudyId).toNullable())
        }

        @Test
        fun `Should update an existent systematic study`() {
            val systematicStudyId = UUID.randomUUID()
            val ownerId = UUID.randomUUID()
            val oldDocument = generateDocument(id = systematicStudyId, owner = ownerId)

            sut.save(oldDocument)

            val newDocument = generateDocument(
                id = systematicStudyId,
                owner = ownerId,
                collaborators = mutableSetOf(UUID.randomUUID())
            )

            sut.save(newDocument)
            val updatedSystematicStudy = sut.findById(systematicStudyId).toNullable()
            assertEquals(2, updatedSystematicStudy?.collaborators?.size)
        }

        @Test
        fun `Should find a existent systematic study`() {
            val systematicStudyId = UUID.randomUUID()
            val document = generateDocument(systematicStudyId)

            sut.save(document)
            assertNotEquals(null, sut.findById(systematicStudyId).toNullable())
        }

        @Test
        fun `Should find all existent systematic study`() {
            sut.save(generateDocument())
            sut.save(generateDocument())
            sut.save(generateDocument())

            assertEquals(3, sut.findAll().size)
        }

        @Test
        fun `Should delete a systematic study`() {
            val systematicStudyId = UUID.randomUUID()
            val document = generateDocument(systematicStudyId)

            sut.save(document)

            sut.deleteById(systematicStudyId)
            assertEquals(null, sut.findById(systematicStudyId).toNullable())
        }
    }

    @Nested
    @Tag("InvalidClasses")
    @DisplayName("When being unable to retrieve data from the repository")
    inner class WhenBeingUnableToRetrieveDataFromTheRepository {
        @Test
        fun `Should not find a systematic study that does not exist`() {
            assertNull(sut.findById(UUID.randomUUID()).toNullable())
        }
    }

    private fun generateDocument(
        id: UUID = UUID.randomUUID(),
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = UUID.randomUUID(),
        collaborators: MutableSet<UUID> = mutableSetOf(),
    ) = SystematicStudyDocument(id, title, description, owner, collaborators.also { it.add(owner) }.toSet())
}