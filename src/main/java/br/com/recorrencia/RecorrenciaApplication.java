package br.com.recorrencia;



import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class RecorrenciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecorrenciaApplication.class, args);
    }
}