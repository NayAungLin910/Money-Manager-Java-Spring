package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;
import react.moneymanager.dto.ExpenseDTO;
import react.moneymanager.entity.CategoryEntity;
import react.moneymanager.entity.ExpenseEntity;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.CategoryRepository;
import react.moneymanager.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // Retrieves all the expenses for current month/based on the start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(currentProfile.getId(), startDate, endDate);
        return expenses.stream().map(this::toDto).toList();
    }

    // Adds a new expense to the database
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
       CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
       ExpenseEntity expenseEntity = toEntity(expenseDTO , profile, category);
       ExpenseEntity newExpense = expenseRepository.save(expenseEntity);
       return toDto(newExpense);
    }

    // helper method
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity) {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profileEntity)
                .category(categoryEntity)
                .build();
    }

    private ExpenseDTO toDto(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryId(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getId() : null)
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}
