package example.invalidmapping;

import jakarta.persistence.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MappingFailureTest {
    @Test
    void t08_wrongMappedByFailsDuringMetadataBootstrap() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:invalidmapping;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.username", "sa")
                .build();
        try {
            assertThatThrownBy(() -> new MetadataSources(registry)
                    .addAnnotatedClass(BrokenStudent.class)
                    .addAnnotatedClass(BrokenBook.class)
                    .buildMetadata())
                    .hasMessageContaining("mappedBy");
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Entity(name = "BrokenStudent")
    static class BrokenStudent {
        @Id Long id;
        @ManyToMany @JoinTable(name = "BROKEN_LINK")
        Set<BrokenBook> books = new HashSet<>();
    }

    @Entity(name = "BrokenBook")
    static class BrokenBook {
        @Id Long id;
        @ManyToMany(mappedBy = "book") // intentionally wrong: owner property is "books"
        Set<BrokenStudent> students = new HashSet<>();
    }
}
