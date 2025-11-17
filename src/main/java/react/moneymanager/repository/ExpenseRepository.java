package react.moneymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import react.moneymanager.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
}
