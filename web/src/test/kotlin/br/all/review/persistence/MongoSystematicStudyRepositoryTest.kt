package br.all.review.persistence

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.infrastructure.review.SystematicStudyDocument
import br.all.infrastructure.shared.toNullable
import io.github.serpro69.kfaker.Faker
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
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
    @DisplayName("When saving a new Systematic Study")
    inner class WhenSavingANewSystematicStudy {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `Should save a new systematic study`() {
                val systematicStudyId = UUID.randomUUID()
                val document = generateDocument(systematicStudyId)

                sut.save(document)
                assertEquals(document, sut.findById(systematicStudyId).toNullable())
            }
        }
    }

    private fun generateDocument(
        id: UUID = UUID.randomUUID(),
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        owner: UUID = UUID.randomUUID(),
        collaborators: Set<UUID> = emptySet(),
    ) = SystematicStudyDocument(id, title, description, owner, collaborators)
}