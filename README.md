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
- POST	/api/agendamentos	Cria um novo agendamento PIX recorrente e dispara a análise antifraude
- GET	/api/agendamentos/{id}	Consulta um agendamento específico pelo seu identificador

### URLS 
- http://localhost:8080/actuator/health

- http://localhost:8080/actuator/health/liveness

- http://localhost:8080/actuator/health/readiness

- http://localhost:8080/actuator/prometheus

- RabbitMQ UI: http://localhost:15672 (usuario/senha: guest/guest)

- AMQP: amqp://guest:guest@localhost:5672/



