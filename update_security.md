Para cada pacote de aggregate no módulo Web
- Remova a parte /researcher/{researcherId}/ do @RequestMapping
- Remova todos os parâmetros "@PathVariable researcherId: UUID" dos métodos dos controllers usando "Refactor/Change Signature" do IntelliJ (caso contrário vai quebrar os presenters)
- Adicione a seguinte injeção de dependência no construtor do Controller: private val credentialService: CredentialService
- Em cada implementação de método controller obtenha o researcher id a partir do credentialService (exemplo: val researcherId = credentialService.getAuthenticatedUserId())
- 
- Atualize as URLs na respectiva classe de teste.
