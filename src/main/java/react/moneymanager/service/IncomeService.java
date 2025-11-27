package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import react.moneymanager.dto.IncomeDTO;
import react.moneymanager.dto.IncomeDTO;
import react.moneymanager.entity.*;
import react.moneymanager.entity.IncomeEntity;
import react.moneymanager.repository.CategoryRepository;
import react.moneymanager.repository.IncomeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // get latest 5 incomes for the current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<IncomeEntity> income =  incomeRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return income.stream().map(this::toDto).toList();
    }

    // get total incomes for current user
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeRepository.findTotalIncomeByProfileId(profileEntity.getId());
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }
    
    // delete income by id for the current user
    public void deleteIncomeById(Long id) {
        ProfileEntity currentProfile = profileService.getCurrentProfile();

        IncomeEntity income = incomeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income not found.")
        );
        if (!income.getProfile().getId().equals(currentProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized action");
        }
        incomeRepository.delete(income);
    }

    // Retrieves all the incomes for current month/based on the start date and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetween(currentProfile.getId(), startDate, endDate);
        return incomes.stream().map(this::toDto).toList();
    }

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
