package br.all.domain.services

import br.all.domain.model.review.SystematicStudyId
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.RisTestData.testInput as risInput
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class ConverterFactoryServiceTest {
    private lateinit var sut: ConverterFactoryService
    private lateinit var bibtexConverterService: BibtexConverterService
    private lateinit var risConverterService: RisConverterService
    private lateinit var idGeneratorService: IdGeneratorService

    @BeforeEach
    fun setup() {
        idGeneratorService = FakeIdGeneratorService

        bibtexConverterService = BibtexConverterService(idGeneratorService)
        risConverterService = RisConverterService(idGeneratorService)
        sut = ConverterFactoryService(
            bibtexConverterService,
            risConverterService
        )
    }

    @AfterEach fun teardown() {
        val fake = idGeneratorService as FakeIdGeneratorService
        fake.reset()
    }

    @Test fun `Should return study review successfully`() {
        val bibtex = BibtexTestData.testInputs["multiple bibtex entries"]!!
        val studyReviewList = sut.extractReferences(SystematicStudyId(UUID.randomUUID()), SearchSessionID(UUID.randomUUID()), bibtex, mutableSetOf("example source"))
        assertEquals(7, studyReviewList.first.size)
    }

    @Test
    fun `Should correctly identify a ris file`(){
        val ris = risInput["multiple RIS entries"]!!
        val studyReviewList = sut.extractReferences(SystematicStudyId(UUID.randomUUID()), SearchSessionID(UUID.randomUUID()),ris, mutableSetOf("example source"))
        assertEquals(3, studyReviewList.first.size)
    }
}