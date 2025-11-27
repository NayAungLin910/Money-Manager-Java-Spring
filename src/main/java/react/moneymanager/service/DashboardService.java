package react.moneymanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import react.moneymanager.entity.ProfileEntity;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

}
