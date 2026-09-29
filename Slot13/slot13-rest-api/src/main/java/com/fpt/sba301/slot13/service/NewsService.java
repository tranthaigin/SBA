package com.fpt.sba301.slot13.service;

import com.fpt.sba301.slot13.exception.NewsNotFoundException;
import com.fpt.sba301.slot13.model.News;
import com.fpt.sba301.slot13.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    public News findById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));
    }

    public List<News> search(String keyword) {
        return newsRepository.search(keyword);
    }

    public News create(News request) {
        request.setId(null);
        return newsRepository.save(request);
    }

    public News update(Long id, News request) {
        findById(id);
        return newsRepository.update(id, request);
    }

    public void delete(Long id) {
        findById(id);
        newsRepository.deleteById(id);
    }
}
