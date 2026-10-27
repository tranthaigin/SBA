package fu.edu.sba301.slot28.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Optional;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            overrideFromEnvironment(configuration, "DB_URL", "hibernate.connection.url");
            overrideFromEnvironment(configuration, "DB_USERNAME", "hibernate.connection.username");
            overrideFromEnvironment(configuration, "DB_PASSWORD", "hibernate.connection.password");
            overrideFromEnvironment(configuration, "HIBERNATE_DDL_AUTO", "hibernate.hbm2ddl.auto");
            return configuration.buildSessionFactory();
        } catch (Exception exception) {
            System.err.println("Cannot build SessionFactory. Check DB_URL, DB_USERNAME, DB_PASSWORD and mappings.");
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void overrideFromEnvironment(Configuration configuration,
                                                String environmentName,
                                                String hibernateProperty) {
        Optional.ofNullable(System.getenv(environmentName))
                .filter(value -> !value.isBlank())
                .ifPresent(value -> configuration.setProperty(hibernateProperty, value));
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (!SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
