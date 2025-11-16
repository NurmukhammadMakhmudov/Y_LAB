package ru.ylab.config;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import java.sql.Connection;

public class LiquibaseConfig {

    public static void runMigrations() {
        try {
            DatabaseConfig.closeDataSource();
            try (Connection connection = DatabaseConfig.getConnection()) {
                Liquibase liquibase = new Liquibase(
                        getPropertyFromFile("liquibase.changeLogFile"),
                        new ClassLoaderResourceAccessor(),
                        DatabaseFactory.getInstance()
                                .findCorrectDatabaseImplementation(
                                        new JdbcConnection(connection)
                                )
                );

                liquibase.update();
                System.out.println("Миграции успешно выполнены");

            } catch (Exception e) {
                throw new RuntimeException("Ошибка при выполнении миграций", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка в runMigrations", e);
        }
    }

    private static String getPropertyFromFile(String key) {
        try (var input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            java.util.Properties props = new java.util.Properties();
            if (input != null) {
                props.load(input);
            }
            return props.getProperty(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load property: " + key, e);
        }
    }
}
