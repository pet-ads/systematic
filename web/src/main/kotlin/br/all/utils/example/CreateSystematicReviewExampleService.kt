package br.all.utils.example

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateSystematicReviewExampleService(
    private val uuidGeneratorService: UuidGeneratorService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
) {
    fun createReview(ownerId: UUID, collaboratorIds: Set<UUID>): UUID {

        val systematicStudyId = uuidGeneratorService.next()

        systematicStudyRepository.saveOrUpdate(
            SystematicStudyDto(
                id = systematicStudyId,
                title = "A Systematic Review on Service-Oriented Robotic Systems Development",
                description = """
                This systematic review, conducted in January 2012 at ICMC/USP, presents a detailed panorama on 
                the design, implementation, and usage of service-oriented robotic systems. It identifies key 
                technologies, methodologies, and software engineering guidelines in the field.
            """.trimIndent(),
                owner = ownerId,
                collaborators = collaboratorIds
            )
        )

        protocolRepository.saveOrUpdate(
            ProtocolDto(
                id = systematicStudyId,
                systematicStudy = systematicStudyId,
                goal = "The main objective of this systematic review is to identify primary studies that report the development of service-oriented robotic systems, i.e., robotic systems that are designed based on SOA. Furthermore, the review aims to obtain a comprehensive overview of the technologies and methodologies used to develop such systems.",
                justification = "Despite a growing number of studies in service-oriented robotics, there is no complete and detailed overview of the systems, implementation technologies, and software engineering guidelines in this domain.",
                researchQuestions = setOf(
                    "RQ1: How has service-orientation been applied to the development of robotic systems?",
                    "RQ2: What is the most common way of interaction among service-oriented robotic systems?",
                    "RQ3: What implementation technology has been mostly used to develop service-oriented robotic systems?",
                    "RQ4: What are the development environments and tools that support the development of service-oriented robotic systems?",
                    "RQ5: Is SOA applicable to all types of robots?",
                    "RQ6: How has Software Engineering been applied to the development of service-oriented robotic systems?"
                ),
                keywords = setOf("Service-Oriented", "Robotic Systems", "SOA", "Systematic Review"),
                searchString = "(\"Service Oriented\" OR \"Service-oriented\" OR \"Service Based\" OR \"Service-based\" OR \"Service Orientation\" OR SOA) AND (Robot OR Robotic OR humanoid)",
                informationSources = setOf(
                    "ACM Digital Library",
                    "Compendex",
                    "IEEE Xplore",
                    "ScienceDirect",
                    "Scopus",
                    "Springer",
                    "Web of Science"
                ),
                sourcesSelectionCriteria = "Sources were selected based on content update, availability, result quality, and export versatility.",
                searchMethod = "A customized search string was applied to multiple publication databases. The search process included an initial screening by title, abstract, and keywords, followed by a full-text evaluation using predefined inclusion and exclusion criteria.",
                studiesLanguages = setOf("ENGLISH"),
                studyTypeDefinition = null,
                selectionProcess = "A two-step process was used: initial screening (title/abstract) followed by full-text evaluation with defined inclusion/exclusion criteria.",
                eligibilityCriteria = setOf(
                    CriterionDto("The primary study proposes or reports on the design and development of a service-oriented robotic systems.", "INCLUSION"),
                    CriterionDto("The primary study proposes or reports on a new technology for developing service-oriented robotic systems.", "INCLUSION"),
                    CriterionDto("The primary study proposes or reports on a process, method, technique, reference architecture or any software engineering guideline that supports either the design or the development of service-oriented robotic systems.", "INCLUSION"),
                    CriterionDto("The primary study reports on the development of a robotic systems without using SOA.", "EXCLUSION"),
                    CriterionDto("The primary study presents contributions in areas other than Robotics.", "EXCLUSION"),
                    CriterionDto("The primary study is a previous version of a more complete study about the same research", "EXCLUSION"),
                    CriterionDto("The primary study does not report on the design or development of service-oriented robotic system.", "EXCLUSION"),
                    CriterionDto("The primary study is a table of contents, short course description, tutorial, copyright form or summary of an event (e.g., a conference or a workshop).", "EXCLUSION"),
                ),
                dataCollectionProcess = "Data extraction will be performed using pre-defined extraction tables corresponding to each research question.",
                analysisAndSynthesisProcess = "Data were synthesized using statistical methods and meta-analysis to draw conclusions about the research area.",
                extractionQuestions = setOf(),
                robQuestions = setOf(),
                picoc = PicocDto(
                    population = "Researchers and developers of robotic systems interested in employing SOA.",
                    intervention = "The development and use of service-oriented robotic systems.",
                    control = "Not applicable.",
                    outcome = "A comprehensive overview of technologies, methodologies, and guidelines for developing service-oriented robotic systems.",
                    context = "Robotics and Software Engineering"
                )
            )
        )
        return systematicStudyId
    }
}