package com.example.orchid.services;

import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.repositories.IOrchidCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrchidCategoryService {
    private final IOrchidCategoryRepository repository;

    public OrchidCategoryService(IOrchidCategoryRepository repository) {
        this.repository = repository;
    }

    public List<OrchidCategory> getAll() {
        return repository.findAll();
    }

    public Optional<OrchidCategory> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public OrchidCategory create(OrchidCategory input) {
        String name = input.getCategoryName().trim();
        if (repository.findByCategoryNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException("Category name already exists: " + name);
        }
        // The client supplies a name; generated IDs and inverse relationships are server-owned.
        return repository.save(new OrchidCategory(name));
    }
}
