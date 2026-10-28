package hantonik.messy.server.core;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerCore implements CommandLineRunner {


    static void main(String @NonNull ... args) {
        SpringApplication.run(ServerCore.class, args);
    }

    @Override
    public void run(String @NonNull ... args) {

    }
}
