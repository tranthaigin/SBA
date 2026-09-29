package com.fpt.sba301.slot13.repository;

import com.fpt.sba301.slot13.model.News;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NewsRepository {
    private final Map<Long, News> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public NewsRepository() {
        save(new News(null, "Welcome to SBA301", "REST API demo", 1L, true));
        save(new News(null, "Spring Boot Part B", "Annotations and 3-layer CRUD", 1L, true));
        save(new News(null, "FPT University Technology Showcase", "Highlights from student capstone projects", 2L, true));
    }

    public List<News> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<News> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public News save(News news) {
        if (news.getId() == null) {
            news.setId(sequence.incrementAndGet());
        }
        store.put(news.getId(), news);
        return news;
    }

    public News update(Long id, News news) {
        news.setId(id);
        store.put(id, news);
        return news;
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public List<News> search(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase();
        return store.values().stream()
                .filter(n -> n.getTitle() != null && n.getTitle().toLowerCase().contains(normalized))
                .toList();
    }
}
