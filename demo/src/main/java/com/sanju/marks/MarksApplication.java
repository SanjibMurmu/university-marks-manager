package com.sanju.marks;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The main entry point for the Jadavpur University Portal backend application.
 * Bootstraps the Spring context, auto-configures the embedded Tomcat server, 
 * and initializes all underlying architectural components and database connections.
 *
 * @author Sanjib Murmu
 * @version 1.0
 */

@SpringBootApplication
public class MarksApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarksApplication.class, args);
    }

    @Bean
    public CommandLineRunner runSeeder(StudentService studentService) {
        return args -> {
            System.out.println("\n=========================================");
            System.out.println("🚀 SPRING BOOT FULLY AWAKE!");
            System.out.println("=========================================\n");
            
            studentService.initializeDatabase();
        };
    }
}