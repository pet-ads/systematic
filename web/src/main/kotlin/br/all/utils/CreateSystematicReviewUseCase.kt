package br.all.utils

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.protocol.repository.ProtocolDto
import br.all.application.protocol.repository.ProtocolRepository
import br.all.domain.model.question.QuestionContextEnum
import br.all.domain.services.UuidGeneratorService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateSystematicReviewUseCase(
    private val uuidGeneratorService: UuidGeneratorService,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val protocolRepository: ProtocolRepository,
    private val createQuestion: CreateQuestionsUseCase
) {
    fun createReview(ownerId: UUID, collaboratorIds: Set<UUID>): UUID {
        val systematicStudyId = uuidGeneratorService.next()

        systematicStudyRepository.saveOrUpdate(
            SystematicStudyDto(
                systematicStudyId,
                " SOA has arisen as a successful architectural style to develop software systems. It has been recently the focus of considerable attention not only in the industry but also in academia. In SOA, software functionalities are packaged in independent, self-contained, and well-defined modules, called services, which are the basis for composing more complex service-oriented systems (Papazoglou and Heuvel, 2007). SOA intends to contribute with low coupling systems, promoting reuse and productivity in software development (Papazoglou et al., 2008).\n",
                "Recently, the SOA characteristics have been attracting researchers and developers from different domains, even beyond traditionally web-based business applications. One of these domains is Robotics, which is claimed to be one of the most prominent research areas in the coming years. As reported by Tomatis (2011), many experts have predicted that the 21st century will be the century of robotics.\n" +
                        "In fact, Robotics has drawn increasing attention in recent years. Robots are already in use worldwide as either simple domestic devices (iRobots, 2011) or complex autonomous agents (NASA, 2011). Despite its relevance, there are still great challenges for robotic systems to be developed productively and in a mature way. To mitigate these issues, SOA has been adopted as a solution for providing more flexible, reconfigurable, and scalable robotic systems so that they can be developed as a set of distributed and independent software modules (Berná-Martínez et al., 2006b). It may also facilitate the integration of heterogeneous devices and different robotic systems (Chen et al., 2008).\n" +
                        "Therefore, companies and research groups have been investigating and supporting the development of service-oriented robotic systems, i.e., robotic systems based on SOA (Straszheim et al., 2011; Jackson, 2007). In this context, studies reporting the initiatives to develop such systems can be found (Jansen et al., 2006; Remy and Blake, 2011; Alnounou et al., 2010). Nevertheless, as far as we are concerned, there is no complete and detailed view of the systems, implementation technologies, and software engineering guidelines developed for robots based on SOA. A study involving a broad and fair analysis of this research topic seems to be relevant, considering the impact it would have on the development of service-oriented robotic systems.\n" +
                        "The main objective of this paper is to present a detailed panorama of how robotic systems based on SOA have been recently designed, implemented, and used. We have adopted and applied the systematic review technique (Kitchenham et al., 2004a), which allows for a complete and fair evaluation of a topic of interest. As the main results of our systematic review, we have observed that in recent years there has been an increase in the number of studies reporting on the development of robotic systems based on SOA, as well as a growing interest in technologies that support their development. These facts evidence a real interest by both academia and industry. Furthermore, this panorama makes it possible to identify interesting and important research topics for investigation.",
                ownerId,
                collaboratorIds
            )
        )

        val extractionQuestion1 = createQuestion.createQuestion(
            systematicStudyId, "EX1", "Extraction Question 1", QuestionContextEnum.EXTRACTION
        )
        val extractionQuestion2 = createQuestion.createQuestion(
            systematicStudyId, "EX2", "Extraction Question 2", QuestionContextEnum.EXTRACTION
        )
        val extractionQuestion3 = createQuestion.createQuestion(
            systematicStudyId, "EX3", "Extraction Question 3", QuestionContextEnum.EXTRACTION
        )

        val robQuestion1 = createQuestion.createQuestion(
            systematicStudyId, "ROB1", "RoB Question 1", QuestionContextEnum.ROB
        )
        val robQuestion2 = createQuestion.createQuestion(
            systematicStudyId, "ROB2", "RoB Question 2", QuestionContextEnum.ROB
        )
        val robQuestion3 = createQuestion.createQuestion(
            systematicStudyId, "ROB3", "RoB Question 3", QuestionContextEnum.ROB
        )

        protocolRepository.saveOrUpdate(
            ProtocolDto(
                id = systematicStudyId,
                systematicStudy = systematicStudyId,
                goal = "The main objective of this systematic review is to identify primary studies that report the development of service-oriented robotic systems, i.e., robotic systems that are designed based on SOA. Furthermore, this review aims at obtaining a comprehensive overview of technologies and methodologies used to develop such systems.",
                justification = "TODO",
                researchQuestions = setOf(
                    "RQ1: How has service-orientation been applied to the development of robotic systems? This question aims to identify the abstraction level, such as sensors/actuators, actions or the whole robot, on which services have been developed;",
                    "RQ2: What is the most common way of interaction among service-oriented robotic systems? The main objective of this question is to identify how services interact in the robotics domain. This interaction could be made, for example, between sensor and robot, robot and robot, robot and back-end, or robot and external services;",
                    "RQ3: What implementation technology has been mostly used to develop service-oriented robotic systems? The aim is to verify which implementation technology, such as SOAP and REST, is mostly used to develop service-oriented robotic systems;",
                    "RQ4: What are the development environments and tools that support the development of service-oriented robotic systems? The objective is to identify development environments and tools used to develop robotic systems based on SOA;",
                    "RQ5: Is SOA applicable to all types of robots? This question aims at identifying if SOA is a viable solution for all robotic systems and their operational situation. Otherwise, in what area or context has SOA been mostly applied to robotic systems;",
                    "RQ6: How has Software Engineering been applied to the development of service-oriented robotic systems? This question aims at identifying the software engineering knowledgement, such as processes, activities, methods, and techniques used during the development of service-oriented robotic systems."
                ),
                keywords = setOf("Service Oriented", "Robot"),
                searchString = "(\"Service Oriented\" OR \"Service-oriented\" OR \"Service Based\" OR \"Service-based\" OR \"Service Orientation\" OR SOA) AND (Robot OR Robotic OR humanoid)",
                informationSources = setOf("Scopus"),
                sourcesSelectionCriteria = null,
                searchMethod = "In order to extract data, we plan to build data extraction tables related to each research question. These tables should synthesize results to facilitate the drawing of conclusions. To summarize and describe the set of data, statistical synthesis method and meta-analysis might be used",
                studiesLanguages = setOf("ENGLISH"),
                studyTypeDefinition = null,
                selectionProcess = null,
                eligibilityCriteria = setOf(),
                dataCollectionProcess = null,
                analysisAndSynthesisProcess = null,
                extractionQuestions = setOf(extractionQuestion1.questionId, extractionQuestion2.questionId, extractionQuestion3.questionId),
                robQuestions = setOf(robQuestion1.questionId, robQuestion2.questionId, robQuestion3.questionId),
                picoc = null
            )
        )
        return systematicStudyId
    }
}