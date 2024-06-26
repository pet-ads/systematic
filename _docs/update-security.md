Para cada pacote de aggregate no módulo Web (ver Create Systematic Study como exemplo)
- Remova a parte /researcher/{researcherId}/ do @RequestMapping
- Remova todos os parâmetros "@PathVariable researcherId: UUID" dos métodos dos controllers usando "Refactor/Change Signature" do IntelliJ (caso contrário vai quebrar os presenters)
- Atualizar o nome das variáveis de responseModel dos endpoints e dtos associados de Researcher/Reviewer para user/userId
- Adicione a seguinte injeção de dependência no construtor do Controller: authenticationInfoService: AuthenticationInfoService
- Em cada implementação de método controller obtenha o researcher id a partir do AuthenticationInfoService (exemplo: val researcherId = authenticationInfoService.getAuthenticatedUserId())
- Nas classes service utilizada pelo controller, inclua a dependência "private val credentialsService: CredentialsService" no construtor.
- Obtenha os dados do usuário autenticado usando o método loadCredentials do credentialService
- Obtenha o systematicStudy usando o repositório pertinente
- Substituia a chamada para o PreconditionChecker pela extension function "prepareIfFailsPreconditions(user, systematicStudy)" e verifique se presenter.isDone()
- Atualize os testes
  - Atualize as URLs
  - Adicione uma injeção de dependência para testHelperService: TestHelperService,
  - Crie um atributo private lateinit var user: ApplicationUser
  - No @BeforeEach obtenha um ApplicationUser com o método testHelperService.createApplicationUser()
  - No @AfterEach remova o ApplicationUser do banco com testHelperService.deleteApplicationUser(user.id)
  - Adicione o ApplicationUser na chamada do MockMVC [1]
- Inclua testes para verificar problemas de autenticação e autorização (user não cadastrado, user que não pode ter acesso por não ter o Role de RESEARCHER)


[1] @Test
    @Tag("ValidClasses")
    fun `should create a valid systematic study`() {
      val json = factory.createValidPostRequest()
      mockMvc.perform(
        post(postUrl())
          .with(SecurityMockMvcRequestPostProcessors.user(user)) <====
          .contentType(MediaType.APPLICATION_JSON).content(json)
        )
      .andExpect(status().isCreated)
      .andExpect(jsonPath("$.researcherId").value(user.id.toString()))
      .andExpect(jsonPath("$.systematicStudyId").isString)
      .andExpect(jsonPath("$._links").exists())
    }