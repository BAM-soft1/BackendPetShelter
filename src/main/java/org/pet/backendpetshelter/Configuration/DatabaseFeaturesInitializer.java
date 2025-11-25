package org.pet.backendpetshelter.Configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

@Component
@Order(2) // Run after InitData
public class DatabaseFeaturesInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseFeaturesInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        System.out.println("Loading database features (procedures, functions, triggers, views)...");
        
        try {
            // Read the SQL file
            ClassPathResource resource = new ClassPathResource("features.sql");
            String sqlScript;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                sqlScript = reader.lines().collect(Collectors.joining("\n"));
            }

            // Split by DELIMITER changes and execute
            executeSqlWithDelimiters(sqlScript);
            
            System.out.println("Database features loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error loading database features: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - allow application to start even if features fail
        }
    }

    private void executeSqlWithDelimiters(String sqlScript) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Split by DELIMITER keyword to handle different sections
            String[] parts = sqlScript.split("DELIMITER");
            String currentDelimiter = ";"; // Start with default delimiter

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.isEmpty()) continue;

                // First part before any DELIMITER command
                if (i == 0) {
                    executeStatementsWithDelimiter(statement, part, ";");
                    continue;
                }

                // Determine new delimiter from the start of this section
                String[] lines = part.split("\n", 2);
                String firstLine = lines[0].trim();
                
                if (firstLine.equals("//")) {
                    currentDelimiter = "//";
                    part = lines.length > 1 ? lines[1] : "";
                } else if (firstLine.equals(";")) {
                    currentDelimiter = ";";
                    part = lines.length > 1 ? lines[1] : "";
                }

                executeStatementsWithDelimiter(statement, part, currentDelimiter);
            }
        }
    }
    
    private void executeStatementsWithDelimiter(Statement statement, String sql, String delimiter) throws Exception {
        // Split statements by the delimiter
        String[] statements = sql.split(java.util.regex.Pattern.quote(delimiter));

        for (String stmt : statements) {
            // Remove comments from the statement
            StringBuilder cleanStmt = new StringBuilder();
            for (String line : stmt.split("\n")) {
                String trimmedLine = line.trim();
                // Skip comment-only lines
                if (trimmedLine.startsWith("--") || trimmedLine.isEmpty()) {
                    continue;
                }
                // Remove inline comments
                int commentPos = trimmedLine.indexOf("--");
                if (commentPos > 0) {
                    trimmedLine = trimmedLine.substring(0, commentPos).trim();
                }
                if (!trimmedLine.isEmpty()) {
                    cleanStmt.append(trimmedLine).append("\n");
                }
            }
            
            String trimmedStmt = cleanStmt.toString().trim();

            // Skip empty statements and special commands
            if (trimmedStmt.isEmpty() ||
                    trimmedStmt.equals("//") ||
                    trimmedStmt.equals(";") ||
                    trimmedStmt.startsWith("USE ")) {
                continue;
            }

            try {
                statement.execute(trimmedStmt);
            } catch (Exception e) {
                // Log but continue - some statements might fail if they already exist
                System.out.println("Warning executing SQL: " + e.getMessage());
            }
        }
    }
}
