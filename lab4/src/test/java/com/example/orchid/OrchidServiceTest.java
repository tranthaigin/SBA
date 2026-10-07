package com.example.orchid;

import com.example.orchid.pojos.Orchid;
import com.example.orchid.pojos.OrchidCategory;
import com.example.orchid.repositories.IOrchidCategoryRepository;
import com.example.orchid.repositories.IOrchidRepository;
import com.example.orchid.services.OrchidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchidServiceTest {

    @Mock
    private IOrchidRepository orchidRepository;

    @Mock
    private IOrchidCategoryRepository categoryRepository;

    @InjectMocks
    private OrchidService orchidService;

    private OrchidCategory category;
    private Orchid orchid;

    @BeforeEach
    void setUp() {
        category = new OrchidCategory(1L, "Cattleya");
        orchid = new Orchid(1L, "Cattleya Queen", true, "Description", category, true, "http://url");
    }

    @Test
    void getAll_shouldReturnAllOrchids() {
        when(orchidRepository.findAll()).thenReturn(List.of(orchid));

        List<Orchid> result = orchidService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrchidName()).isEqualTo("Cattleya Queen");
        verify(orchidRepository).findAll();
    }

    @Test
    void searchByName_shouldReturnMatchingOrchids() {
        when(orchidRepository.findByOrchidNameContainingIgnoreCase("Queen")).thenReturn(List.of(orchid));

        List<Orchid> result = orchidService.searchByName("Queen");

        assertThat(result).hasSize(1);
        verify(orchidRepository).findByOrchidNameContainingIgnoreCase("Queen");
    }

    @Test
    void getById_whenExists_shouldReturnOrchid() {
        when(orchidRepository.findById(1L)).thenReturn(Optional.of(orchid));

        Optional<Orchid> result = orchidService.getById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getOrchidName()).isEqualTo("Cattleya Queen");
    }

    @Test
    void create_withValidCategory_shouldSaveAndReturnOrchid() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(orchidRepository.save(any(Orchid.class))).thenReturn(orchid);

        Orchid input = new Orchid(null, "New Orchid", true, "Desc", category, true, "http://url");
        Orchid created = orchidService.create(input);

        assertThat(created).isNotNull();
        assertThat(created.getOrchidID()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
        verify(orchidRepository).save(any(Orchid.class));
    }

    @Test
    void create_withMissingCategoryId_shouldThrowException() {
        Orchid input = new Orchid(null, "New Orchid", true, "Desc", new OrchidCategory(), true, "http://url");

        assertThatThrownBy(() -> orchidService.create(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("categoryId is required");

        verify(orchidRepository, never()).save(any());
    }

    @Test
    void create_withNonExistentCategory_shouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        OrchidCategory nonExistent = new OrchidCategory(999L, "Unknown");
        Orchid input = new Orchid(null, "New Orchid", true, "Desc", nonExistent, true, "http://url");

        assertThatThrownBy(() -> orchidService.create(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found: 999");

        verify(orchidRepository, never()).save(any());
    }

    @Test
    void update_whenExistsAndValid_shouldUpdateAndReturn() {
        when(orchidRepository.findById(1L)).thenReturn(Optional.of(orchid));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(orchidRepository.save(any(Orchid.class))).thenReturn(orchid);

        Orchid updatedInput = new Orchid(null, "Updated Name", false, "New Desc", category, false, "http://newurl");
        Optional<Orchid> result = orchidService.update(1L, updatedInput);

        assertThat(result).isPresent();
        verify(orchidRepository).save(orchid);
    }

    @Test
    void update_whenNotExists_shouldReturnEmpty() {
        when(orchidRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Orchid> result = orchidService.update(999L, orchid);

        assertThat(result).isEmpty();
        verify(orchidRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldReturnTrue() {
        when(orchidRepository.existsById(1L)).thenReturn(true);

        boolean deleted = orchidService.delete(1L);

        assertThat(deleted).isTrue();
        verify(orchidRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotExists_shouldReturnFalse() {
        when(orchidRepository.existsById(999L)).thenReturn(false);

        boolean deleted = orchidService.delete(999L);

        assertThat(deleted).isFalse();
        verify(orchidRepository, never()).deleteById(anyLong());
    }
}

