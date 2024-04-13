Como fazer os endpoints? Ver o arquivo api-doc.md
O que preciso fazer de testes antes de pedir um PR? Ver o arquivo testing-guidelines.md
Em qual ordem fazer o trabalho?
Fazer um feature-branch
Escolher UM endpoint por ordem de importância (CREATE é mais importante que DELETE, certo?)
Criar a interface do service e a interface do presenter
Criar a implementação do service (se quiser, já crie os testes de application service e de integração com banco agora)
Criar a implementação do presenter
Adicionar um @bean para o service no ControllerConfiguration
Criar o controller e juntar tudo
Criar uma classe de teste para o controller
Se o happy e os sad paths já estiverem testados, rode todos testes da aplicação e do domínio.
Se tudo estiver lindo, limpo e testado, faça o PR
Repita para o próximo endpoint
Quem perdeu a reunião ou precisou sair um pouco antes, se precisar de help, marque com os próprios colegas. Ensinando a gente aprende mais.
Deadline para o último PR do ciclo: 21 de dezembro
Is that right?
Todos os quatro, deixem um joinha marcando que leram e estão cientes.
Glória e pau na máquina. O fim está próximo.