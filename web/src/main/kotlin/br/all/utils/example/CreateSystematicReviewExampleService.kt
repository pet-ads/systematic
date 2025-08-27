package br.all.utils.example

import br.all.application.protocol.repository.CriterionDto
import br.all.application.protocol.repository.PicocDto
import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateSystematicReviewExampleService(
    private val uuidGeneratorService: UuidGeneratorService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val question: CreateQuestionExampleService
) {
    fun createReview(ownerId: UUID, collaboratorIds: Set<UUID>): UUID {

        val systematicId = uuidGeneratorService.next()

        systematicStudyRepository.saveOrUpdate(
            SystematicStudyDto(
                id = systematicId,
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

        val eq1 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ1",
            title = "How has service-orientation been applied to the development of robotic systems?",
            type = "TEXTUAL",
            context = QuestionContextEnum.EXTRACTION
        )

        val eq2 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ2",
            title = "What is the most common way of interaction among service-oriented robotic systems?",
            type = "NUMBERED_SCALE",
            context = QuestionContextEnum.EXTRACTION,
            higher = 5,
            lower = 1
        )

        val eq3 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ3",
            title = "What implementation technology has been mostly used to develop service-oriented robotic systems?",
            type = "LABELED_SCALE",
            context = QuestionContextEnum.EXTRACTION,
            scales = mapOf(
                "How clearly are the review’s research objectives stated?" to 1,
                "How comprehensive is the search strategy (databases, keywords, and search strings)?" to 2,
                "How transparent is the data extraction and synthesis process?" to 3,
                "How well are the findings (tables, graphs, mapping) organized and reported?" to 4,
                "How adequately are the threats to validity discussed?" to 5
            )
        )

        val eq4 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ4",
            title = "Which research question provides the most valuable insight for future research?",
            type = "PICK_LIST",
            context = QuestionContextEnum.EXTRACTION,
            options = listOf(
                "RQ1: How has service-orientation been applied to the development of robotic systems?",
                "RQ2: What is the most common way of interaction among service-oriented robotic systems?",
                "RQ3: What implementation technology has been mostly used to develop service-oriented robotic systems?",
                "RQ4: What are the development environments and tools that support the development of service-oriented robotic systems?",
                "RQ5: Is SOA applicable to all types of robots?",
                "RQ6: How has Software Engineering been applied to the development of service-oriented robotic systems?"
            )
        )

        val eq5 = question.createQuestion(
            reviewId = systematicId,
            code = "EQ5",
            title = "Which of the following service-oriented architectures (SOAs) have been applied to robotic systems in the reviewed studies?",
            type = "PICK_MANY",
            context = QuestionContextEnum.EXTRACTION,
            options = listOf(
                "ROS (Robot Operating System)",
                "DDS (Data Distribution Service)",
                "REST (Representational State Transfer)",
                "SOAP (Simple Object Access Protocol)",
                "Corba (Common Object Request Broker Architecture)",
                "MQTT (Message Queuing Telemetry Transport)"
            )
        )

        val rbq1 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ1",
            title = "In your own words, describe potential sources of selection bias in this review.",
            type = "TEXTUAL",
            context = QuestionContextEnum.ROB
        )

        val rbq2 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ2",
            title = "Which option best describes the overall risk of bias in the review?",
            type = "PICK_LIST",
            context = QuestionContextEnum.ROB,
            options = listOf(
                "Very High",
                "High",
                "Moderate",
                "Low",
                "Very Low"
            )
        )

        val rbq3 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ3",
            title = "How effectively is publication bias minimized (e.g., through multiple databases and specialist suggestions)?",
            type = "LABELED_SCALE",
            context = QuestionContextEnum.ROB,
            scales = mapOf(
                "Not effective at all" to 1,
                "Slightly effective" to 2,
                "Moderately effective" to 3,
                "Very effective" to 4,
                "Extremely effective" to 5
            )
        )

        val rbq4 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ4",
            title = "Rate the overall reliability of the review process from 1 (very unreliable) to 5 (very reliable).",
            type = "NUMBERED_SCALE",
            context = QuestionContextEnum.ROB,
            higher = 5,
            lower = 1
        )

        val rbq5 = question.createQuestion(
            reviewId = systematicId,
            code = "RBQ5",
            title = "Select all potential sources of bias that are present in the primary study.",
            type = "PICK_MANY",
            context = QuestionContextEnum.ROB,
            options = listOf(
                "Selection bias (e.g., non-randomized sampling)",
                "Performance bias (e.g., lack of blinding of participants)",
                "Detection bias (e.g., lack of blinding of outcome assessors)",
                "Attrition bias (e.g., incomplete outcome data)",
                "Reporting bias (e.g., selective reporting of outcomes)"
            )
        )

        protocolRepository.saveOrUpdate(
            ProtocolDto(
                id = systematicId,
                systematicStudy = systematicId,
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
                    "Web Of Science",
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
                extractionQuestions = setOf(eq1.questionId, eq2.questionId, eq3.questionId, eq4.questionId, eq5.questionId),
                robQuestions = setOf(rbq1.questionId, rbq2.questionId, rbq3.questionId, rbq4.questionId, rbq5.questionId),
                picoc = PicocDto(
                    population = "Researchers and developers of robotic systems interested in employing SOA.",
                    intervention = "The development and use of service-oriented robotic systems.",
                    control = "Not applicable.",
                    outcome = "A comprehensive overview of technologies, methodologies, and guidelines for developing service-oriented robotic systems.",
                    context = "Robotics and Software Engineering"
                )
            )
        )
        return systematicId
    }
}