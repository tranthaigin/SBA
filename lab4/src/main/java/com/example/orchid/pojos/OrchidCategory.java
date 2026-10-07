package com.example.orchid.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orchid_categories")
public class OrchidCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank
    @Size(max = 100)
    @Nationalized
    @Column(nullable = false, unique = true, length = 100)
    private String categoryName;

    @OneToMany(mappedBy = "orchidCategory")
    @JsonIgnore
    private List<Orchid> orchids = new ArrayList<>();

    public OrchidCategory() {}

    public OrchidCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public OrchidCategory(Long categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Orchid> getOrchids() {
        return orchids;
    }

    public void setOrchids(List<Orchid> orchids) {
        this.orchids = orchids;
    }
}
