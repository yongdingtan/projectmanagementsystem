package com.yd.projectmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.sql.DriverManager;

@SpringBootApplication
public class ProjectmanagementsystemApplication {
    public static void main(String[] args) {
        try {
            // Manually load the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        SpringApplication.run(ProjectmanagementsystemApplication.class, args);
    }
}