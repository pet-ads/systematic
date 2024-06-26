# Guidelines para criar novas features 

- Como fazer os endpoints? Ver o arquivo [api-doc](./api-doc.md)
- O que preciso testar antes de pedir um PR? Ver o arquivo [testing-guidelines.md](./testing-guidelines.md)
- Em qual ordem fazer o trabalho? 
  - Fazer um feature-branch
  - Escolher UM endpoint por ordem de importância (CREATE é mais importante que DELETE, certo?)
  - Criar a interface do service e a interface do presenter
  - Criar a implementação do service (se quiser, já crie os testes de application service e de integração com banco agora)
  - Criar a implementação do presenter
  - Adicionar um @bean para o service no ControllerConfiguration
  - Criar o controller e juntar tudo
  - Criar uma classe de teste para o controller
  - Se o happy e os sad paths já estiverem testados, rode todos testes da aplicação e do domínio.
  - Se tudo estiver lindo, limpo e testado, faça o PR
  - Repita para o próximo endpoint
