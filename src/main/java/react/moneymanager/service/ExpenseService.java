package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import react.moneymanager.dto.ExpenseDTO;
import react.moneymanager.entity.CategoryEntity;
import react.moneymanager.entity.ExpenseEntity;
import react.moneymanager.entity.ProfileEntity;
import react.moneymanager.repository.CategoryRepository;
import react.moneymanager.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // get latest 5 expenses for the current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<ExpenseEntity> expense =  expenseRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return expense.stream().map(this::toDto).toList();
    }

    // Notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> expenses =  expenseRepository.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDto).toList();
    }

    // get total expenses for current user
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        BigDecimal totalExpense = expenseRepository.findTotalExpenseByProfileId(profileEntity.getId());
        return totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntities = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return  expenseEntities.stream().map(this::toDto).toList();
    }

    // delete expense by id for the current user
    public void deleteExpenseById(Long id) {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        ExpenseEntity expense = expenseRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found.")
        );
        if (!expense.getProfile().getId().equals(currentProfile.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized action");
        }
        expenseRepository.delete(expense);
    }

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
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found!"));
        ExpenseEntity expenseEntity = toEntity(expenseDTO, profile, category);
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
