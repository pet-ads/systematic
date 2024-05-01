# Back-end TO-DO list 

1. Criar um branch refactor a partir de feat-security para cada pacote representando um aggregate no pacote web 
(exemplo: feat-security-question). Seguir os passos definidos no arquivo [update-security.md](./update-security.md). 
2. Em cada branch criada, antes de integrar novamente ao feat-security, verificar as pré-condições e pós-condições de 
   todos os application services de acordo com o definido no arquivo [services-pre-pos.md](./services-pre-pos.md). Complementar os casos de teste.
3. Atualizar a documentação dos endpoints com autenticação e autorização

4. Após integrar todos os branches, descontinuar as classes FakeResearcherCredentialsService e ResearcherCredentialsService 
existentes no pacote review/application/researcher/credentials
5. Renomear os pacotes researcher em review/application e review/domain/Model para User, pois agora as ações podem ser
feitas por um User que tem Roles de ADMIN ou COLLABORATOR. 
6. Criar testes e2e com Spring [WebClient](https://www.baeldung.com/spring-5-webclient) com estrutura reutilizável para
as requisições de criar usuário, autenticar usuário criado, criar revisão sistemática e as ações de desfazer os mesmos
recursos. Testar e2e a coisas como criar um systematic-study, atualizar um protocol com um search source, fazer uma 
search session e mudar o status de seleção e extração de um study-review ... 
7. Criar endpoints e services faltantes seguindo o proposto nos arquivos [new-features-guidelines.md](./new-features-guidelines.md),
[api-doc](./api-doc.md) e [testing-guidelines.md](testing-guidelines.md)  



