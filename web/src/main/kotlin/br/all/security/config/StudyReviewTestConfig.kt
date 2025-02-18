package br.all.security.config
//
//import br.all.application.review.create.CreateSystematicStudyService
//import br.all.application.review.create.CreateSystematicStudyService.RequestModel as ReviewRequestModel
//import br.all.application.study.create.CreateStudyReviewService
//import br.all.application.study.create.CreateStudyReviewService.RequestModel as StudyRequestModel
//import br.all.application.protocol.update.UpdateProtocolService
//import br.all.protocol.requests.PutRequest as ProtocolPutRequest
//import br.all.application.user.find.LoadAccountCredentialsService
//import br.all.domain.services.ConverterFactoryService
//import br.all.domain.model.review.SystematicStudyId
//import br.all.domain.model.search.SearchSessionID
//import br.all.study.presenter.RestfulCreateStudyReviewPresenter
//import br.all.review.presenter.RestfulCreateSystematicStudyPresenter
//import br.all.protocol.presenter.RestfulUpdateProtocolPresenter
//import br.all.utils.LinksFactory
//import org.springframework.boot.CommandLineRunner
//import org.springframework.stereotype.Component
//import java.util.*
//
//@Component
//class SystematicStudyTestConfig(
//    private val createSystematicStudyService: CreateSystematicStudyService,
//    private val createStudyReviewService: CreateStudyReviewService,
//    private val updateProtocolService: UpdateProtocolService,
//    private val credentialsService: LoadAccountCredentialsService,
//    private val converterFactoryService: ConverterFactoryService,
//    private val linksFactory: LinksFactory
//) : CommandLineRunner {
//
//    override fun run(vararg args: String?) {
//        // Buscar o usuário "admin" pelo serviço de credenciais
//        val adminCredentials = try {
//            credentialsService.loadAuthenticationCredentialsByUsername("admin")
//        } catch (ex: NoSuchElementException) {
//            println("🔹 Usuário admin não encontrado. Certifique-se de que o AuthTestConfig criou o usuário.")
//            return
//        }
//
//        // IDs da revisão sistemática e da sessão de busca
//        val userId = adminCredentials.id
//        val systematicStudyId = SystematicStudyId(UUID.randomUUID()) // Substitua por um ID fixo, se necessário
//        val searchSessionId = SearchSessionID(UUID.randomUUID())
//
//        // Criar uma revisão sistemática
//        val reviewPresenter = RestfulCreateSystematicStudyPresenter(linksFactory)
//        val reviewRequest = ReviewRequestModel(
//            userId = userId,
//            title = "Admin Systematic Review with Protocol",
//            description = "This is a test systematic review created for the admin user, including studies from BibTeX and protocol information.",
//            collaborators = setOf() // Adicione colaboradores, se necessário
//        )
//
//        try {
//            createSystematicStudyService.create(reviewPresenter, reviewRequest)
//            println("✅ Revisão sistemática criada com sucesso: ${reviewRequest.title}")
//        } catch (ex: Exception) {
//            println("🔹 Falha ao criar revisão sistemática: ${ex.message}")
//            return
//        }
//
//        // Obter o ID da revisão criada
//        val createdReview = reviewPresenter.responseEntity?.body as? RestfulCreateSystematicStudyPresenter.ViewModel
//        if (createdReview == null) {
//            println("🔹 Falha ao obter dados da revisão criada.")
//            return
//        }
//
//        // Atualizar o protocolo da revisão sistemática
//        val protocolPresenter = RestfulUpdateProtocolPresenter(linksFactory)
//        val protocolRequest = ProtocolPutRequest(
//            title = "Protocol for Admin Systematic Review",
//            objective = "Define clear objectives for this systematic review.",
//            researchQuestions = listOf("What is the effect of X on Y?"),
//            inclusionCriteria = listOf("Criteria 1", "Criteria 2"),
//            exclusionCriteria = listOf("Criteria A", "Criteria B"),
//            searchStrategy = "Use of BibTeX and manual search.",
//            dataExtractionStrategy = "Manual data extraction using forms.",
//            qualityAssessmentStrategy = "Using predefined quality metrics."
//        )
//
//        try {
//            updateProtocolService.update(protocolPresenter, protocolRequest.toUpdateRequestModel(userId, createdReview.systematicStudyId))
//            println("✅ Protocolo atualizado com sucesso para a revisão sistemática: ${createdReview.systematicStudyId}")
//        } catch (ex: Exception) {
//            println("🔹 Falha ao atualizar protocolo: ${ex.message}")
//        }
//
//        // Simular um arquivo BibTeX para converter em estudos
//        val bibtexFileContent = """
//            @article{example2023,
//                author = {John Doe and Jane Smith},
//                title = {A Study on Systematic Reviews},
//                year = {2023},
//                journal = {Journal of Systematic Testing}
//            }
//        """.trimIndent()
//
//        // Extrair estudos usando o ConverterFactoryService
//        val (studies, errors) = converterFactoryService.extractReferences(
//            systematicStudyId,
//            searchSessionId,
//            bibtexFileContent
//        )
//
//        if (studies.isEmpty()) {
//            println("🔹 Nenhum estudo foi extraído: ${errors.joinToString()}")
//            return
//        }
//
//        // Criar estudos associados à revisão sistemática
//        studies.forEach { study ->
//            val studyPresenter = RestfulCreateStudyReviewPresenter(linksFactory)
//            val studyRequest = StudyRequestModel(
//                userId = userId,
//                systematicStudyId = createdReview.systematicStudyId,
//                searchSessionId = searchSessionId.id,
//                type = study.type.name,
//                title = study.title,
//                year = study.year,
//                authors = study.authors,
//                venue = study.venue,
//                abstract = study.abstract ?: "",
//                keywords = study.keywords ?: emptySet(),
//                source = "BibTeX"
//            )
//
//            try {
//                createStudyReviewService.createFromStudy(studyPresenter, studyRequest)
//                println("✅ Estudo criado com sucesso: ${study.title}")
//            } catch (ex: Exception) {
//                println("🔹 Falha ao criar estudo: ${ex.message}")
//            }
//        }
//    }
//}
