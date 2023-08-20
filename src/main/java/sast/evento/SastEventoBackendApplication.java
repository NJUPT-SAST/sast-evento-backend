package sast.evento;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("sast.evento.mapper")
public class SastEventoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SastEventoBackendApplication.class, args);
    }

}
