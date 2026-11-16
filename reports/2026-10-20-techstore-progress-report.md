# TechStore — Relatório didático de evolução

**Data:** 20 de outubro de 2026  
**Período coberto:** trabalho acompanhado nesta conversa, de 18 a 20 de outubro.  
**Objetivo:** explicar o que mudou, por que mudou, como funciona e quais limites ainda existem.

Este é um relatório de estudo em português solicitado pelo proprietário. Os nomes de código e arquivos permanecem em inglês. Ele descreve o estado observado nesta data; não substitui o código, os testes ou o documento de segurança atualizado.

## 1. O ponto de partida e o que realmente fizemos

O TechStore já tinha um backend em Java 21, Spring Boot, PostgreSQL e uma arquitetura em camadas. Os módulos de produtos, marcas, categorias, roles e endereços já tinham implementações. O módulo de usuários também já estava parcialmente implementado no diretório de trabalho, embora parte dele ainda não estivesse em um commit.

Isso faz uma diferença importante: não criamos o projeto nem o módulo inteiro de usuários do zero nesta conversa. Minha contribuição nessa parte foi ampliar os testes, tornar as verificações mais específicas, implementar hashing de senha, ajudar a documentar o progresso e, depois, construir a base de autenticação por sessões. O commit de usuários reúne código que já estava no diretório e mudanças realizadas durante este acompanhamento.

Na correção do endereço padrão, você acrescentou a anotação `@Transactional` seguindo a explicação. Eu completei o teste de integração, preparei o banco isolado e executei as validações.

O trabalho teve quatro frentes:

1. Preservar a integridade da troca do endereço padrão.
2. Aumentar a confiança nos usuários por meio de testes e hashing.
3. Definir e iniciar a segurança com sessões no servidor.
4. Resolver o problema da extensão Java com Lombok e manter a documentação coerente.

**Situação principal:** a base de autenticação funciona, mas a segurança completa ainda não está pronta. Algumas operações foram bloqueadas até receberem as permissões corretas. Isso é uma limitação real da entrega atual, não apenas uma tarefa de documentação.

## 2. Conceitos para entender o projeto

### Backend, frontend e API

O frontend será a interface que a pessoa usa: telas, botões e formulários. O backend recebe pedidos dessa interface, aplica regras e acessa o banco. Uma API é o conjunto de operações que o backend oferece.

Uma requisição HTTP é uma mensagem enviada para uma dessas operações. Ela pode pedir uma consulta, enviar um cadastro ou solicitar uma mudança. A resposta inclui um status, como `200` para sucesso ou `401` quando falta autenticação.

Um endpoint combina um caminho com um método HTTP. Por exemplo, `POST /auth/login` recebe os dados de login. Esses caminhos não são páginas prontas do frontend.

### As camadas que já existiam

```text
Requisição HTTP
    → Controller
    → Service
    → Repository
    → PostgreSQL
```

- **Controller:** recebe dados HTTP e devolve a resposta.
- **Service:** coordena o caso de uso e as regras de negócio.
- **Repository:** oferece consultas e gravações no banco.
- **Entity:** representa dados persistidos, com relacionamentos e comportamentos do domínio.
- **DTO:** representa os dados aceitos ou devolvidos pela API. Evita expor a entidade inteira.
- **Mapper:** converte entre representações, como entidade e DTO de resposta.

JPA é a especificação utilizada para mapear objetos Java para dados relacionais. Hibernate é a implementação empregada pelo projeto. Não redesenhamos o projeto como Clean Architecture ou arquitetura hexagonal.

Agora há também uma cadeia de filtros de segurança antes dos controllers. Ela verifica aspectos como cookies, CSRF e permissão de acesso. Isso complementa a arquitetura existente.

## 3. Correção da integridade do endereço padrão

### O problema original

Um usuário tinha Casa como endereço padrão e cadastrava Trabalho como novo padrão. O serviço precisava executar duas alterações:

```java
oldDefault.setDefaultAddress(false);
addressRepository.saveAndFlush(oldDefault);

address = addressRepository.save(address);
```

Sem uma transação envolvendo o método inteiro, a retirada do padrão anterior podia ser confirmada antes da gravação do novo endereço. Se a segunda gravação falhasse, Casa deixaria de ser padrão e Trabalho nem seria cadastrado.

O problema era uma operação de negócio dividida em gravações que podiam terminar separadamente.

### A solução

O método `AddressService.create` passou a ter:

```java
@Transactional
public AddressResponse create(CreateAddressRequest request) {
    // Existing creation workflow
}
```

Uma transação reúne as alterações em uma unidade. **Commit** confirma a unidade; **rollback** reverte as alterações quando ocorre uma falha que exige reversão. As falhas de persistência usadas no teste são exceções não verificadas, que provocam rollback no comportamento padrão adotado.

O método `update` já tinha a anotação. A correção principal foi cobrir também a criação.

### Por que saveAndFlush não impede rollback

`flush` envia alterações pendentes ao banco. `commit` confirma a transação. São momentos diferentes.

Com a transação no serviço, o `UPDATE` do endereço anterior pode já ter sido executado e ainda assim ser revertido se a gravação seguinte falhar. Mantivemos o `saveAndFlush`, sem uma refatoração desnecessária do fluxo.

Isso não cria a regra de que todo usuário sempre deve ter um endereço padrão. Apenas impede que uma tentativa fracassada de substituição retire o padrão existente.

### Como comprovamos

`AddressServiceIntegrationTest` prepara um usuário e um endereço padrão em uma transação confirmada. Depois chama o serviço e provoca uma falha na nova gravação enviando `street = null`, que viola a restrição da entidade/banco.

O teste espera a exceção e consulta o banco em outra transação:

```java
assertTrue(original.isDefaultAddress());
assertEquals(1L, addressCount());
```

Portanto, ele verifica o estado persistido depois da falha, não apenas o objeto que estava na memória. Também existe um cenário de sucesso que confirma a substituição do padrão e a existência dos dois endereços.

Não colocamos `@Transactional` na classe desse teste. Uma transação envolvendo o teste inteiro poderia proteger o serviço mesmo sem a anotação no método e esconder o defeito que queríamos detectar.

Usamos `TransactionTemplate` para abrir blocos de transação de maneira explícita na preparação, consulta e limpeza. O teste não depende de uma transação automática ao redor da chamada ao serviço.

### Limite dessa correção

Ela comprova atomicidade do fluxo testado. Não comprova que duas requisições simultâneas nunca possam disputar a escolha do padrão. Controle de concorrência é um problema diferente, não resolvido por esses testes.

## 4. Banco isolado para testes

Criamos `docker-compose.test.yml`, com PostgreSQL 17 na porta local `55434` e banco `techstore_test`. A tentativa inicial usou a porta `5434`, mas ela já estava ocupada; ajustamos a configuração sem remover o serviço que a utilizava.

Uma porta identifica por onde um programa recebe conexões. Usar uma porta diferente permite manter bancos de desenvolvimento e teste ao mesmo tempo.

O arquivo `src/test/resources/application-test.properties` aponta os testes para esse banco. O perfil `test` seleciona essa configuração. Um perfil é um conjunto de propriedades que altera a execução conforme o ambiente.

O container usa armazenamento temporário (`tmpfs`): seu conteúdo desaparece quando ele para. O `healthcheck` verifica se o PostgreSQL está aceitando conexões antes da execução dos testes.

Os testes criam seus próprios dados e removem os registros de negócio usados como preparação. Não executamos uma limpeza geral do banco de desenvolvimento. As roles obrigatórias são preservadas. Sessões anônimas auxiliares podem continuar no banco de testes até sua expiração e limpeza pela infraestrutura.

## 5. Testes de usuários: de verificações pequenas a cenários de comportamento

### Teste unitário e mock

Um teste unitário examina uma unidade de comportamento com colaboradores simulados quando apropriado. Um **mock** é um objeto controlado pelo teste: podemos definir o que ele retorna e verificar se foi chamado.

No `UserServiceTest`, os repositórios, o mapper e o encoder são simulados. Isso evita iniciar o banco e o Spring para cada regra do serviço.

Ampliamos a classe de quatro para 17 testes. Ela cobre criação, consultas, listagem vazia, atualização, e-mail duplicado, referências inexistentes relevantes ao contrato atual, ativação, desativação e mudança de senha.

Na evolução mais recente, a criação passou a buscar CUSTOMER por nome. Seu cenário de role ausente corresponde agora à falta dessa role obrigatória no sistema, não a uma escolha de role pelo cliente. A atualização antiga ainda possui seu contrato com `roleId`, mas está bloqueada por HTTP enquanto sua autorização não é implementada.

### Por que trocar RuntimeException por uma exceção específica

Antes, o teste de e-mail duplicado aceitava qualquer `RuntimeException`. Até uma falha inesperada, como um acesso indevido a um objeto nulo, poderia satisfazer essa expectativa.

Agora exige:

```java
assertThrows(UserAlreadyExistsException.class,
    () -> userService.create(createRequest));
```

O teste passa somente se a rejeição tiver o significado esperado.

Também verificamos que uma operação rejeitada não chama `save` e, na atualização, não altera o usuário antes de terminar as verificações necessárias.

### O que é ArgumentCaptor

O captor permite inspecionar o objeto enviado para um colaborador:

```java
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(userRepository).save(captor.capture());
User saved = captor.getValue();
```

Assim, examinamos nome, e-mail, telefone, role, estado ativo e o resultado do encoder entregue à entidade. É mais informativo do que verificar apenas que alguma chamada de gravação aconteceu.

### Testes do controller

Criamos `UserControllerTest`, com 26 execuções de cenários. Ele usa MockMvc para simular requisições HTTP sem precisar de um navegador real.

Testamos validação, dados devolvidos, ausência de senha nas respostas, argumentos enviados ao serviço e status HTTP. Os testes parametrizados repetem a mesma verificação com entradas diferentes, como nome vazio, e-mail inválido e telefone ausente.

Uma entrada inválida deve ser rejeitada antes de chegar ao serviço. Por isso usamos `verifyNoInteractions(userService)` nesses cenários.

## 6. Hashing de senha: o que mudou e o que significa

### O defeito

O serviço entregava diretamente `request.password()` à entidade. O nome `changePassword` não transformava o conteúdo por conta própria. O resultado podia ser uma senha original armazenada no banco.

O comentário da entidade dizia que ela esperava um hash, mas o serviço não cumpria essa expectativa. Um comentário não aplica uma proteção automaticamente.

### Hashing não é criptografia reversível

O objetivo é guardar um valor derivado da senha e verificar futuras tentativas, sem precisar recuperar a senha original a partir desse valor.

Não fizemos uma simples codificação de texto, como Base64, nem usamos um hash genérico rápido como substituto de um mecanismo de senha. Adotamos um encoder específico de uma biblioteca mantida.

`PasswordConfig` fornece um `PasswordEncoder` com PBKDF2, utilizando o conjunto de parâmetros identificado pelo Spring como `pbkdf2@SpringSecurity_v5_8`.

O nome desse conjunto de parâmetros não significa que o projeto foi rebaixado para Spring Security 5.8. Ele identifica uma configuração do algoritmo; a versão da biblioteca continua sendo gerenciada pelo Spring Boot.

### Salt e identificação do formato

O encoder acrescenta um salt aleatório ao cálculo. Duas codificações da mesma senha podem produzir valores diferentes. Isso dificulta reutilizar resultados calculados previamente para várias contas.

O `DelegatingPasswordEncoder` inclui um prefixo que identifica o formato. Assim, uma futura verificação sabe qual configuração utilizar. Não habilitamos uma alternativa que aceite senha original como formato de armazenamento.

### Onde aplicamos

No cadastro:

```java
user.changePassword(passwordEncoder.encode(request.password()));
```

Na mudança de senha, `UserService.changePassword` passou a receber a senha original e aplicar o encoder antes de chamar a entidade. A entidade continua recebendo o hash e não passa a depender da biblioteca de segurança.

Uma atualização comum de perfil não altera a senha. Os testes verificam essa preservação.

### Como verificamos a senha

No login, usamos:

```java
encoder.matches(passwordReceived, passwordStored);
```

Não comparamos dois hashes recém-gerados com `equals`, porque seus salts podem ser diferentes.

`PasswordConfigTest` exercita o algoritmo real: senha correta confere, senha incorreta não confere e codificações repetidas variam. `UserServiceIntegrationTest` verifica os hashes efetivamente persistidos pelo cadastro e pela mudança de senha.

Não reescrevemos senhas antigas. Você confirmou que os dados atuais são descartáveis; sua recriação poderá ser feita explicitamente, sem adicionar compatibilidade com senhas em texto puro. A rotina de descarte geral não foi executada.

## 7. Regras de segurança que você aprovou

**Autenticação** responde quem está fazendo a requisição. **Autorização** responde o que essa pessoa pode fazer. Estar conectado não dá permissão para acessar qualquer cadastro.

As decisões registradas incluem:

- Cadastro público atribui CUSTOMER no servidor.
- Somente ADMIN autenticado pode atribuir ou alterar roles.
- O titular gerencia seu próprio perfil e seus endereços.
- Alterar a própria senha exigirá a senha atual.
- ADMIN poderá listar usuários, consultar outros perfis e ativar/desativar contas.
- SELLER não terá poderes administrativos sobre usuários.
- O primeiro ADMIN será criado por inicialização explicitamente habilitada, com credenciais externas, sem senha padrão e sem promoção silenciosa de uma conta existente.
- Visitantes podem navegar pelo catálogo; ações de carrinho e compra exigirão identificação.

A maioria das permissões administrativas e de propriedade ainda não foi implementada. A aprovação define o destino; não faz o comportamento surgir automaticamente.

### Prazos aprovados, mas ainda pendentes

| Regra | CUSTOMER | ADMIN e SELLER |
| --- | --- | --- |
| Persistência após fechar navegador | Desejada | Não desejada |
| Limite de inatividade | 30 dias | 15 minutos |
| Limite absoluto desde login | 90 dias | 8 horas |
| Renovação silenciosa | Dentro dos dois limites | Dentro dos dois limites |

Atividade não poderá reiniciar o limite absoluto. Definir o que conta como atividade ainda é necessário: consultas automáticas em segundo plano não devem manter um acesso indefinidamente.

Você também aprovou encerrar todos os acessos após mudança de senha ou role e bloquear acessos existentes na próxima requisição após desativação. Essas invalidações globais permanecem pendentes.

## 8. Por que escolhemos sessões no servidor

Uma conta pode estar conectada em vários lugares. Cada acesso autenticado pode ter uma sessão diferente. Por isso, logout do computador não precisa encerrar o celular, enquanto uma mudança de senha deverá encerrar todos.

Na abordagem escolhida, o navegador apresenta um identificador e o servidor mantém o registro correspondente. Esse identificador não é a senha do usuário.

Escolhemos Spring Session JDBC para armazenar sessões no PostgreSQL já utilizado pelo projeto. Não introduzimos Redis ou um segundo banco. O armazenamento no servidor e a duração do cookie no navegador são controles diferentes.

JWT ficou adiado. Ter frontend em outro projeto não obriga seu uso. No estágio atual, um registro central de sessões combina com as exigências de inatividade e invalidação que você definiu.

Isso não significa que sessões sejam sempre melhores. Foi uma escolha adequada ao escopo atual e aos requisitos aprovados, evitando adicionar o controle de revogação de JWT sem uma necessidade concreta.

## 9. A base de autenticação implementada, arquivo por arquivo

### SecurityConfig.java

É a configuração da segurança web. `@Configuration` identifica uma classe de configuração; `@Bean` registra objetos para o Spring conectar às outras partes. A injeção por construtor recebe esses objetos sem o controller precisar instanciá-los manualmente.

A `SecurityFilterChain` configura verificações antes do controller. Nesta etapa, libera somente os caminhos explicitamente listados, exige autenticação para o usuário atual e nega os demais.

Também configura a persistência da autenticação na sessão, CORS, CSRF, cookies e as respostas de erro de segurança. O cache de requisições para redirecionamento foi desativado, pois a API trabalha com respostas JSON, não com uma tela de login gerada pelo backend.

O logout usa a infraestrutura do Spring Security. Não foi necessário escrever um controller para apagar sessões manualmente.

### UserAuthenticationProvider.java

É o componente que verifica as credenciais. Busca a conta por e-mail, usa o encoder para conferir a senha e rejeita contas inativas.

Se a conta não existe, ainda realiza uma comparação de hash com um valor auxiliar. Isso reduz uma diferença grosseira entre pular o cálculo e executá-lo. Não afirmamos que isso torne o tempo total constante ou elimine todas as formas de enumeração de contas.

Um formato antigo ou inválido de hash é tratado como credencial inválida. Não há autenticação alternativa com texto puro.

Senha errada, conta inexistente e conta inativa recebem a mesma mensagem genérica. Isso evita informar diretamente qual parte da credencial foi reconhecida. Não foi implementado um sistema completo de limitação de tentativas ou recuperação de conta.

### SessionUser.java

É um record com ID, e-mail e role. Um record é uma forma concisa de representar dados imutáveis.

Ele implementa `Principal`, que representa uma identidade, e `Serializable`, necessário para sua representação ser armazenada pela infraestrutura de sessão escolhida. O ID estável identifica a conta para o índice de sessões. Não guardamos a entidade JPA, a senha ou seu hash nesse objeto.

### LoginRequest.java

É o DTO com e-mail e senha. As anotações de validação rejeitam campos vazios e e-mail malformado. Seu `toString` foi sobrescrito para não reproduzir credenciais ao converter o objeto em texto.

Essa medida não equivale a uma auditoria completa de todos os logs ou DTOs do projeto.

### AuthController.java

Recebe o pedido de login, solicita autenticação ao `AuthenticationManager`, aplica a estratégia de sessão e salva o contexto autenticado explicitamente.

```java
sessionStrategy.onAuthentication(authentication, request, response);
contextRepository.saveContext(context, request, response);
```

A primeira chamada aplica o tratamento do novo login, incluindo a troca do identificador existente. A segunda permite recuperar a identidade em requisições posteriores. Apenas definir um objeto na memória da requisição atual não garantiria persistência do login.

O nome `UsernamePasswordAuthenticationToken` aparece no código do Spring. É um objeto interno para representar a tentativa ou resultado de autenticação. Não significa que passamos a emitir JWT para o frontend.

O endpoint do usuário atual usa o ID da identidade autenticada, não um ID escolhido livremente pelo cliente.

### Os endpoints reais desta entrega

| Método e caminho | Função |
| --- | --- |
| GET /auth/csrf | Obter o nome do cabeçalho e o token CSRF |
| POST /auth/login | Autenticar com e-mail e senha |
| GET /auth/me | Consultar a conta autenticada |
| POST /auth/logout | Encerrar a sessão atual |
| POST /users | Cadastrar CUSTOMER, com validação e CSRF |

O cadastro não faz login automaticamente. O caminho implementado para o usuário conectado é `/auth/me`, não o exemplo antigo `/users/me`.

## 10. Cookies, CORS e CSRF sem pressupor conhecimento prévio

### Cookie

Um cookie é um pequeno dado que o navegador guarda e pode enviar em requisições ao servidor correspondente. Nosso cookie se chama `TECHSTORE_SESSION` e contém um identificador opaco, não as credenciais.

- `HttpOnly`: impede a leitura direta desse cookie pelo JavaScript da página.
- `Secure`: limita seu envio a conexões seguras, conforme o comportamento do navegador. É o padrão planejado para execução com HTTPS.
- `SameSite=Lax`: restringe seu envio em determinados contextos entre sites. Não substitui a proteção CSRF.
- `Path=/`: aplica o cookie aos caminhos do backend sob essa raiz.

O perfil `local` e o perfil `test` permitem HTTP explicitamente. Não mudamos o padrão geral para cookies inseguros.

Alguns navegadores restauram cookies de sessão ao reabrir. Portanto, fechar o navegador não é um evento de revogação que possamos garantir somente com esse tipo de cookie. Os limites no servidor e o logout explícito continuam necessários.

### CORS

Origem combina protocolo, endereço e porta. Um frontend e um backend em portas diferentes têm origens diferentes, mesmo na mesma máquina.

CORS determina quais origens o navegador pode permitir que leiam respostas e façam a interação configurada. Não é uma prova da identidade de quem chama a API e não substitui autenticação: outros clientes HTTP não estão sujeitos às mesmas restrições do navegador.

Permitimos origens configuradas e o envio de credenciais. O padrão de desenvolvimento aponta para a porta 5173, mas pode ser alterado por configuração externa. Não usamos origem curinga com credenciais.

### CSRF

Como o navegador pode enviar cookies automaticamente, outro site poderia tentar provocar uma operação usando um acesso já existente. A proteção CSRF exige uma informação adicional na requisição.

O frontend obtém essa informação em `GET /auth/csrf` e envia o valor no cabeçalho informado. O cookie da sessão continua protegido por HttpOnly; o token CSRF é uma informação diferente.

Uma sessão anônima pode existir para guardar CSRF antes do login. Existir sessão não significa necessariamente que alguém esteja autenticado.

O login troca o identificador da sessão e renova o estado CSRF. Por isso, depois do login o frontend precisa buscar um token novo antes de outra escrita. Após logout, deve obter outro token antes de um novo login ou cadastro.

### Fluxo completo

```text
1. Frontend obtém CSRF e recebe um cookie de sessão anônima.
2. Envia e-mail, senha, cookie e cabeçalho CSRF para login.
3. Backend verifica as credenciais e troca o identificador da sessão.
4. A identidade autenticada é armazenada no PostgreSQL.
5. Navegador envia o cookie nas próximas chamadas relevantes.
6. GET /auth/me devolve o usuário identificado pela sessão.
7. Frontend obtém CSRF atualizado antes de uma nova escrita.
8. Logout invalida a sessão atual; reapresentar seu cookie antigo não restaura o acesso.
```

O frontend separado deverá incluir credenciais nas chamadas pertinentes. Não escrevemos suas telas ou a navegação visual para login/cadastro nesta etapa.

## 11. Cadastro público e catálogo ativo

O `CreateUserRequest` perdeu o componente `roleId`. O serviço resolve CUSTOMER por nome usando o repositório. Assim, um visitante não escolhe seu próprio nível de permissão.

Com a configuração atual de JSON, um campo extra `roleId` é ignorado, não devolve necessariamente erro. O teste envia um valor arbitrário e comprova que a conta criada é CUSTOMER. Ignorar esse campo não permite atribuir a role solicitada.

O `UpdateUserRequest` ainda tem `roleId`; sua remoção e a separação das operações administrativas continuam pendentes. A rota de atualização está bloqueada na cadeia atual.

No catálogo, adicionamos `findAllByActiveTrue` e `findByIdAndActiveTrue` ao `ProductRepository`, com métodos públicos correspondentes no serviço. Esses nomes são consultas derivadas do Spring Data: a biblioteca monta a consulta a partir das propriedades indicadas.

O controller público passou a chamar esses métodos. Os métodos internos anteriores foram preservados para não mudar silenciosamente o significado de todas as consultas existentes.

## 12. O que está temporariamente bloqueado e o que ainda falta

A configuração termina com:

```java
.anyRequest().denyAll()
```

Isso significa que o acesso não é liberado automaticamente só porque alguém fez login. Rotas que ainda não receberam sua política permanecem fechadas.

Hoje, gestão de usuários, consultas de roles, gestão de endereços e alterações do catálogo estão temporariamente indisponíveis por HTTP, mesmo para usuários autenticados. Os serviços continuam existindo e sendo testados.

| Recurso | Estado real |
| --- | --- |
| Hashing no cadastro e mudança de senha | Implementado |
| Cadastro público como CUSTOMER | Implementado |
| Login, usuário atual e logout local | Implementado |
| Sessão em PostgreSQL, CSRF e CORS | Base implementada |
| Consulta pública de catálogo ativo | Implementada |
| Tempo de inatividade atual | 15 minutos para todas as roles |
| Cookie persistente de CUSTOMER e prazo de 30/90 dias | Pendente |
| Limite absoluto de 8 horas de ADMIN/SELLER | Pendente |
| Invalidação global por mudança de senha ou role | Pendente |
| Bloquear sessões já abertas após desativação | Pendente; novos logins inativos já são rejeitados |
| Confirmação da senha atual em endpoint de troca de senha | Pendente; o endpoint ainda não foi criado |
| Primeiro ADMIN por configuração externa | Planejado, não implementado |
| Propriedade e autorização de perfil/endereços | Planejadas, não liberadas por HTTP |
| Permissões administrativas e de SELLER para catálogo | Ainda precisam de definição/implementação |
| Carrinho, pedidos e frontend | Fora desta entrega |

O armazenamento de sessões foi testado no banco e entre requisições. Não fizemos uma validação manual de reinício real do backend nem de restauração de sessão na interface de diferentes navegadores.

## 13. Problemas encontrados e como foram tratados

### Porta ocupada

O banco de testes não conseguiu usar a porta 5434 porque outro processo já a utilizava. Mudamos a porta do novo banco para 55434 e atualizamos a configuração correspondente. Não removemos o processo ou container que já existia.

### Restrições de execução

Algumas tentativas não conseguiram acessar o Docker, a conexão local do PostgreSQL ou os recursos necessários à execução do editor/testes dentro da sessão restrita. Solicitamos a permissão necessária e repetimos as ações autorizadas. Isso era uma limitação do ambiente de execução, não prova de defeito na regra de negócio.

Uma solicitação foi negada por você e a execução correspondente não foi realizada. Retomamos quando você autorizou reenviar a permissão.

### Ambiguidade de CORS

O Spring encontrou mais de um objeto compatível com `CorsConfigurationSource`: o que criamos e um componente MVC. Receber apenas o tipo não indicava qual escolher.

Usamos `@Qualifier("corsConfigurationSource")` para identificar explicitamente a configuração desejada. Esse mecanismo não adiciona uma regra de negócio; resolve a montagem dos componentes.

### Infraestrutura de segurança nos testes

Os testes isolados inicialmente não tinham a configuração necessária para montar `HttpSecurity`. Habilitamos a infraestrutura explicitamente na configuração de teste.

Depois, identidades criadas com `@WithMockUser` não chegavam corretamente às requisições do MockMvc. O módulo direto de testes de segurança não fornecia sozinho toda a integração modular do Boot 4 utilizada ali. Trocamos para `spring-boot-starter-security-test`.

Não alteramos os status esperados para aceitar uma falha. Mantivemos os filtros, passamos CSRF nas escritas e usamos uma cadeia de teste que exige autenticação, separada das permissões ainda incompletas de produção.

### Um erro meu na adaptação automática

Ao acrescentar CSRF às chamadas de teste, uma substituição automática também atingiu chamadas de `delete` dos mocks, onde o retorno é `void`. Isso gerou erros de compilação. Corrigi a adaptação para que o complemento fosse aplicado às requisições HTTP, não às chamadas de serviço. Não era necessário mudar o serviço ou enfraquecer o teste.

### Tipos da biblioteca e construtor do produto

O repositório JDBC expõe um tipo concreto de sessão cujo acesso não era permitido naquele ponto do teste. Passei a trabalhar pela interface pública `Session` para ler seus atributos.

Também tentei montar um produto de teste com `setSku`, mas a entidade não oferece esse setter. Ajustei a preparação para usar seu construtor existente. Não acrescentei um setter ao domínio apenas para facilitar um teste.

### O ERROR intencional no teste de rollback

O PostgreSQL escreve uma mensagem de erro quando o teste tenta persistir uma rua nula. Nesse cenário, é a falha provocada que queremos observar. `assertThrows` espera a exceção e as consultas posteriores verificam o rollback. Uma linha ERROR no log não significa que a execução de testes terminou com falha; o resumo do Maven precisa ser interpretado junto com o cenário.

### A prévia de um endereço local

Uma URL usada como exemplo acabou aparecendo na prévia da aplicação. Não havia uma página entregue para visualização naquele momento. A conexão recusada indicava ausência de servidor aceitando aquela conexão; o exemplo não equivalia à implementação de uma tela. O endpoint real posterior ficou em `/auth/me`.

## 14. O problema Lombok, Java 21 e VS Code

Você observou, em `CreateAddressRequest` e `UpdateAddressRequest`, a mensagem sobre Flexible Constructor Bodies exigir Java 25.

As classes não usavam esse recurso. Elas utilizavam `@AllArgsConstructor`, do Lombok, para gerar um construtor com todos os campos.

Lombok participa da compilação para gerar código que não está escrito manualmente no arquivo. O Maven usa a compilação configurada no projeto. O editor também possui seu próprio mecanismo de análise e compilação incremental, por meio da extensão Java.

Confirmamos que estava instalada a extensão `redhat.java` 1.57.0. Encontramos um problema conhecido dessa versão na integração com Lombok e o compilador Eclipse/JDT, com a mesma mensagem em projetos Java 21. O relato também descreve geração de arquivos `.class` inválidos.

Isso é compatível com os erros `ClassFormatError: Invalid method Code length 0` observados durante nossas execuções. Não fizemos rastreamento de cada escrita em arquivo para atribuir individualmente todas as falhas ao editor; a versão, o diagnóstico e os sintomas coincidiram com o problema documentado.

A solução aplicada foi instalar a extensão 1.56.0 pela linha de comando do VS Code, com sua autorização. Você recarregou o editor e relatou que o erro desapareceu. Eu confirmei a versão registrada e executei uma recompilação do zero com os testes de endereços.

Não alteramos o projeto para Java 25. Não removemos Lombok, não desabilitamos seu processamento e não reescrevemos os DTOs para esconder o diagnóstico.

`clean` remove os arquivos gerados em `target`, não o código-fonte. Isso importa porque uma compilação incremental pode dizer que não há nada a compilar e deixar arquivos ruins na saída. Após limpar, o Maven recompila o que precisa.

O diagnóstico atual dentro do editor não foi lido diretamente por uma ferramenta: a confirmação combina seu relato visual com a extensão registrada e a recompilação bem-sucedida.

Referências do problema:

- [Relato principal no repositório da extensão Java](https://github.com/redhat-developer/vscode-java/issues/4521)
- [Relato com AllArgsConstructor e Java 21](https://github.com/redhat-developer/vscode-java/issues/4544)

## 15. Dependências e configuração adicionadas

| Item | Motivo |
| --- | --- |
| spring-security-crypto | Encoder de senha mantido por biblioteca, sem criptografia caseira |
| spring-boot-starter-security | Infraestrutura de autenticação, filtros e autorização web |
| spring-boot-starter-session-jdbc | Sessões HTTP armazenadas no PostgreSQL |
| spring-boot-starter-security-test | Integração dos testes de segurança com Spring Boot 4 e MockMvc |

As versões permanecem gerenciadas pelo Spring Boot. Não houve atualização geral de framework, introdução de JWT, Redis, Flyway ou Testcontainers.

`application.properties` ganhou armazenamento de sessões, timeout provisório de 15 minutos, origens permitidas e configuração de cookies seguros. `application-local.properties` permite HTTP local explicitamente; o perfil de testes faz o mesmo no ambiente isolado.

Spring Session usa seu script de esquema para preparar as tabelas de infraestrutura. Elas não são novas entidades do domínio comercial. A inicialização atual é adequada à fase local de estudo; antes de produção, ainda será necessário planejar provisionamento controlado do esquema.

## 16. Como interpretar os testes e seus resultados

Os tipos de teste respondem perguntas diferentes:

| Tipo | Pergunta respondida |
| --- | --- |
| Serviço com Mockito | A lógica chama os colaboradores certos e preserva as regras? |
| Controller com MockMvc | A API valida, serializa e devolve os status previstos no contrato? |
| Encoder real | O hash confere com a senha correta e rejeita outra? |
| Integração com PostgreSQL | As gravações e transações têm o efeito esperado no banco? |
| Servidor real com cookies | Filtros, CSRF, CORS e sessões funcionam entre requisições reais? |

`SessionAuthenticationIntegrationTest` possui dez métodos de teste. Um cliente HTTP Java com gerenciador de cookies faz as chamadas contra uma porta aleatória do servidor real. Ele verifica catálogo público, ocultação de produto inativo, login, troca do identificador de sessão, dados persistidos, falhas de credenciais, CSRF, CORS, cadastro CUSTOMER e bloqueio padrão.

O teste de logout reapresenta o cookie antigo depois da invalidação e verifica que ele não recupera a autenticação. Não confundimos isso com um teste completo de todas as futuras invalidações globais.

`PublicCatalogServiceTest` acrescenta quatro testes para as consultas públicas de produtos ativos.

Os testes antigos de controller usam `ControllerTestSecurityConfig`, uma configuração exclusiva de teste que exige autenticação. Ela permite continuar examinando o contrato HTTP de operações cuja autorização de produção ainda está fechada. Esses testes não comprovam que a rota está liberada em produção. A cadeia real é exercitada pelo teste de integração de sessões.

### Histórico das execuções nesta conversa

| Momento | Resultado confirmado |
| --- | --- |
| Endereço padrão | 2 testes de integração; suíte completa então com 115 testes aprovados |
| Usuários e encoder | 45 execuções específicas aprovadas; suíte completa então com 156 testes |
| Base de sessões e catálogo | 71 execuções específicas aprovadas; suíte completa com 170 testes |
| Após instalar extensão Java 1.56.0 e recarregar | Recompilação limpa com Java 21 e 23 testes de endereços aprovados |

Esses números não devem ser somados. As verificações se sobrepõem e a suíte foi crescendo. A última suíte completa aprovada tinha 170 testes; depois da correção da extensão executamos o subconjunto de endereços, não novamente os 170.

O último grupo de endereços contém 11 testes de serviço, 10 de controller e 2 de integração. Seu log está em `target/address-verification.log` e termina com `BUILD SUCCESS` e zero falhas/erros.

Arquivos em `target` são gerados e podem desaparecer no próximo `clean`. O histórico de execução e a documentação registram os resultados anteriores. Durante a elaboração deste relatório não executei outra suíte, pois alterei somente este documento.

Testes aprovados oferecem evidência do comportamento coberto. Não são prova de ausência de qualquer defeito nem autorização para tratar a segurança como pronta para produção.

## 17. Inventário de arquivos e finalidade

Os caminhos abaixo são relativos à raiz do repositório para facilitar localização e revisão no Git.

### Trabalho anterior já registrado no histórico

- `src/main/java/com/fragala/techstore/service/AddressService.java`: transação no fluxo de criação do endereço.
- `src/test/java/com/fragala/techstore/service/AddressServiceIntegrationTest.java`: sucesso e rollback com persistência real.
- `docker-compose.test.yml` e `src/test/resources/application-test.properties`: ambiente de PostgreSQL para testes.
- `src/main/java/com/fragala/techstore/config/PasswordConfig.java`: encoder PBKDF2 e identificação do formato.
- `src/main/java/com/fragala/techstore/service/UserService.java`: hashing no cadastro e mudança de senha; posteriormente ajustado novamente para CUSTOMER fixo.
- `src/test/java/com/fragala/techstore/service/UserServiceTest.java`: ampliação dos cenários de serviço.
- `src/test/java/com/fragala/techstore/controller/UserControllerTest.java`: novo conjunto de testes HTTP de usuários.
- `src/test/java/com/fragala/techstore/config/PasswordConfigTest.java`: encoder real.
- `src/test/java/com/fragala/techstore/service/UserServiceIntegrationTest.java`: hashes persistidos no banco.
- `docs/05-roadmap.md`: situação verificada dos módulos, validação e testes; depois atualizado para sessões.
- `docs/06-decisions.md`: decisões e justificativas; depois ampliado pelas políticas aprovadas.
- `docs/07-integration-tests.md`: preparação e execução dos testes com PostgreSQL.
- `docs/08-security.md`: armazenamento de senhas, modelo aprovado e estado real da implementação.
- `docs/PROJECT_CONTEXT.md`: aviso de que o conteúdo preservado é um retrato histórico anterior.
- `pom.xml`: dependência de crypto; depois ampliado para a base web de segurança.

### Arquivos novos da base atual, ainda não commitados na conferência

- `src/main/java/com/fragala/techstore/config/SecurityConfig.java`.
- `src/main/java/com/fragala/techstore/controller/AuthController.java`.
- `src/main/java/com/fragala/techstore/dto/request/LoginRequest.java`.
- `src/main/java/com/fragala/techstore/security/SessionUser.java`.
- `src/main/java/com/fragala/techstore/security/UserAuthenticationProvider.java`.
- `src/main/resources/application-local.properties`.
- `src/test/java/com/fragala/techstore/security/SessionAuthenticationIntegrationTest.java`.
- `src/test/java/com/fragala/techstore/service/PublicCatalogServiceTest.java`.
- `src/test/java/com/fragala/techstore/support/ControllerTestSecurityConfig.java`.

### Arquivos existentes modificados na base atual

- `pom.xml`: dependências web, sessão JDBC e testes de segurança.
- `src/main/java/com/fragala/techstore/controller/ProductController.java`: consulta pública ativa.
- `src/main/java/com/fragala/techstore/service/ProductService.java`: novos métodos públicos de catálogo ativo.
- `src/main/java/com/fragala/techstore/repository/ProductRepository.java`: consultas derivadas por estado ativo.
- `src/main/java/com/fragala/techstore/repository/UserRepository.java`: busca por e-mail para login.
- `src/main/java/com/fragala/techstore/repository/RoleRepository.java`: busca por nome para CUSTOMER.
- `src/main/java/com/fragala/techstore/service/UserService.java`: atribuição de CUSTOMER na criação.
- `src/main/java/com/fragala/techstore/dto/request/CreateUserRequest.java`: retirada de roleId.
- `src/main/resources/application.properties`: propriedades da infraestrutura de sessão.
- `src/test/resources/application-test.properties`: cookies HTTP no perfil isolado.
- Os seis testes de controller em `src/test/java/com/fragala/techstore/controller`: Address, Brand, Category, Product, Role e User; identidade simulada, CSRF e isolamento do contrato HTTP.
- `src/test/java/com/fragala/techstore/service/UserServiceTest.java` e `UserServiceIntegrationTest.java`: adaptação ao novo contrato de criação.
- `docs/05-roadmap.md`, `docs/06-decisions.md` e `docs/08-security.md`: planejamento aprovado, implementação atual e limites.
- Este relatório: `reports/2026-10-20-techstore-progress-report.md`.

### Alteração fora do repositório

A extensão Java do VS Code foi substituída pela versão 1.56.0. Isso é uma alteração do ambiente local: não faz parte dos arquivos enviados em um commit do projeto. Os dois DTOs de endereço não precisaram ser reescritos.

## 18. Histórico Git, próximo commit e LinkedIn

A conferência local encontrou estes commits relacionados ao trabalho:

| Commit | Tema |
| --- | --- |
| 6d9f9e1 | Integridade da substituição do endereço padrão |
| e702c13 | Módulo de usuários, hashing e testes |
| 96b8df0 | Roadmap, armazenamento de senha e política de roles |
| efbaca4 | Instruções de agentes e configuração/documentação do projeto |

O último item foi organizado por você. Não atribuo a mim a criação de todos os arquivos que esse commit reuniu.

O histórico local não comprova sozinho o estado remoto. Antes do próximo envio, devemos conferir a branch, o que está preparado e a relação com o remoto. Nenhum commit, push ou postagem foi executado para elaborar este relatório.

Uma divisão possível para as alterações pendentes é:

1. Código da base de autenticação, cadastro CUSTOMER, catálogo ativo e os testes correspondentes, mantendo suas dependências juntas.
2. Documentação das decisões, estado real, roteiro de implementação e relatório, se você decidir versioná-lo.

Essa divisão será revisada antes de preparar os arquivos. Não devemos separar testes de mudanças indispensáveis à sua compilação apenas para criar commits com nomes menores.

Para o LinkedIn, fatos que já podemos comunicar são: estudo de transações e rollback real, ampliação dos testes, hashing PBKDF2, início da autenticação por sessões persistidas, CSRF/CORS e catálogo público ativo. Podemos informar que a última suíte completa tinha 170 testes aprovados.

Não seria correto anunciar segurança concluída, autorização completa por roles, carrinho/pedidos prontos, frontend entregue, deploy realizado ou 170 testes novos escritos do zero. O texto será preparado e revisado depois da leitura deste relatório.

## 19. Próximas entregas e o que cada uma resolverá

1. **Permissões de usuário e endereços:** liberar as operações hoje bloqueadas, conferindo titularidade pelo usuário autenticado e separando administração de edição do próprio perfil.
2. **Primeiro ADMIN:** implementar a inicialização explicitamente habilitada, lidar com conta já existente sem promoção silenciosa e testar repetição da execução.
3. **Mudança de role:** criar operação administrativa protegida, retirar roleId da atualização pública e exigir novo login dos acessos invalidados.
4. **Ciclo de vida das sessões:** implementar os prazos diferentes, limite absoluto, persistência de CUSTOMER, bloqueio após desativação e invalidação global por senha/role.
5. **Detalhes ainda abertos:** significado de atividade, operações adicionais que exigirão confirmação de senha, permissões de catálogo de ADMIN/SELLER e proteção contra perda indevida do acesso administrativo.
6. **Integração do frontend:** usar cookies e CSRF corretamente, tratar 401/403 e construir o direcionamento para login/cadastro.
7. **Carrinho e pedidos:** definir as regras de estoque, preço e histórico antes dos respectivos serviços e APIs.

Cada entrega deve vir com testes do comportamento alterado e documentação apenas onde houver mudança real. O objetivo é continuar avançando em partes compreensíveis, sem apresentar planejamento como funcionalidade já existente.

## 20. Fontes técnicas para consultar durante o estudo

- [Spring Security: armazenamento de senhas](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html).
- [Spring Security: módulo de crypto](https://docs.spring.io/spring-security/reference/features/integrations/cryptography.html).
- [Spring Security: persistência da autenticação](https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html).
- [Spring Boot: Spring Session](https://docs.spring.io/spring-boot/reference/web/spring-session.html).
- [Spring Security: CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).
- [Spring Security: CORS](https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html).
- [MDN: cookies e Set-Cookie](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie).
- [MDN: envio de credenciais pelo Fetch](https://developer.mozilla.org/en-US/docs/Web/API/Request/credentials).

Os exemplos e nomes de arquivos deste relatório foram conferidos com o código local. As referências externas servem para aprofundamento; o escopo e os resultados do TechStore são os que foram implementados e verificados nesta conversa.
