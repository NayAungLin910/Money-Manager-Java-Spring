package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import react.moneymanager.dto.IncomeDTO;
import react.moneymanager.dto.IncomeDTO;
import react.moneymanager.entity.CategoryEntity;
import react.moneymanager.entity.IncomeEntity;
import react.moneymanager.entity.IncomeEntity;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.CategoryRepository;
import react.moneymanager.repository.IncomeRepository;
import react.moneymanager.repository.IncomeRepository;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // Adds a new income to the database
    public IncomeDTO addIncome(IncomeDTO incomeDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
        IncomeEntity incomeEntity = toEntity(incomeDTO , profile, category);
        IncomeEntity newIncome = incomeRepository.save(incomeEntity);
        return toDto(newIncome);
    }

    // helper method
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profileEntity)
                .category(categoryEntity)
                .build();
    }

    private IncomeDTO toDto(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
