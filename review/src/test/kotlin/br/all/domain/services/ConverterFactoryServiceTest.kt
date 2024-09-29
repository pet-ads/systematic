package br.all.domain.services

import br.all.application.search.create.CreateSearchSessionService
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.BibtexTestData.testInputs as bibtexInput
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
        val bibtex = bibtexInput["multiple bibtex entries"]!!
        val fake = SystematicStudyId(UUID.randomUUID())
        val studyReviewList = sut.extractReferences(fake, bibtex)
        assertEquals(7, studyReviewList.size)
    }

    @Test
    fun `Should correctly identify a ris file`(){
        val ris = risInput["multiple RIS entries"]!!
        val fake = SystematicStudyId(UUID.randomUUID())
        val studyReviewList = sut.extractReferences(fake, ris)
        assertEquals(3, studyReviewList.size)
    }
}