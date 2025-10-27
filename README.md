 (Recorrência PIX)

##  Como rodar o projeto
###  Pré-requisitos
- Docker e Docker Compose instalados
- Porta 8080 disponível
- Java 21 (caso queira rodar localmente)
- Postgres e RabbitMQ sob containers ou instâncias locais

###  Rodando via Docker Compose
- abrir o terminal na pasta onde está o arquivo do docker-compose
- docker compose up -d --build
- com isso teremos um menssageria ativa (rabbit )
- um banco de dados postgre e nosso container vai se chamar recorrencia-pix
- a aplicação vai ficar disponivel em http://localhost:8080
  
  ### Endpoints disponíveis
- POST	/v1/agendamentos	Cria um novo agendamento PIX recorrente e dispara a análise antifraude
- Exemplo de body 

{

  "nomePagador":"teste",
  
  "nomeRecebedor":"test2e",
  
  "documentoPagador": "11111222232",
  
  "documentoRecebedor": "22222222222",
  
  "valor": "10.00",
  
  "periodicidade": "MENSAL",
  
  "primeiraExecucao": "2025-11-01T10:00:00Z",
  
  "descricao": "Assinatura básica"
  
}

- GET	/v1/agendamentos/{id}	Consulta um agendamento específico pelo seu identificador

### URLS 
- http://localhost:8080/actuator/health

- http://localhost:8080/actuator/health/liveness

- http://localhost:8080/actuator/health/readiness

- http://localhost:8080/actuator/prometheus

- RabbitMQ UI: http://localhost:15672 (usuario/senha: guest/guest)

- AMQP: amqp://guest:guest@localhost:5672/

### Regras aplicadas
- Regra de valor de pix: Onde o usuario coloca um valor de limite que ele mesmo conhece para não ter problemas de tentativas de pix fraudulentos. ( foi colocado um valor como corte de 10000 )
- Regra de quantidade de agendamentos: é feito uma consulta pelo documento do pagador para ver se existem outros agendamentos no status aguardando revisão ou agendado ) Se já existir não fazemos o agendamento deixamos em analise.
- Regra de transferencia para o mesmo documento: Se o documento informado for o mesmo para o agendamento é rejeitado pois não faz sentido esse agendamento.



