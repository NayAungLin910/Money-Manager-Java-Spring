package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import react.moneymanager.dto.CategoryDTO;
import react.moneymanager.entity.CategoryEntity;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), currentProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with the name already exists!");
        }
        CategoryEntity categoryEntity = toEntity(categoryDTO, currentProfile);
        CategoryEntity newCategory = categoryRepository.save(categoryEntity);
        return toDto(newCategory);
    }

    // get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    // get categories by type for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type, profileEntity.getId());
        return categories.stream().map(this::toDto).toList();
    }

    // update category
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findByIdAndProfileId(categoryId, profileEntity.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found or not accessible"));
        category.setName(categoryDTO.getName());
        category.setIcon(categoryDTO.getIcon());
        category.setType(categoryDTO.getType());
        category = categoryRepository.save(category);
        return toDto(category);
    }

    // helper methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDto(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
