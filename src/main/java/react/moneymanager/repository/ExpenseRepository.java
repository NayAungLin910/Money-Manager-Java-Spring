package react.moneymanager.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import react.moneymanager.entity.ExpenseEntity;
import react.moneymanager.entity.ExpenseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    //  select * from tbl_expenses where profile_id = ?1 order by date desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // select * from tbl_expenses where profile_id = ?1 order by date desc limit 5
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM ExpenseEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like order by %?4%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3
    List<ExpenseEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );

    // select * from tbl_expenses where profile_id = ?1 and date = ?2
    @Query("""
    SELECT e
    FROM ExpenseEntity e
    JOIN FETCH e.category
    WHERE e.profile.id = :profileId
    AND e.date = :date
""")
    List<ExpenseEntity> findByProfileIdAndDateWithCategory(
            @Param("profileId") Long profileId,
            @Param("date") LocalDate date
    );
}
