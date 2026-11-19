package com.nabiha.expense_tracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByOwner(AppUser owner, Pageable pageable);

    Page<Expense> findByOwnerAndCategoryIgnoreCase(AppUser owner, String category, Pageable pageable);

    Optional<Expense> findByIdAndOwner(Long id, AppUser owner);

    @Query("SELECT new com.nabiha.expense_tracker.CategoryTotal(e.category, SUM(e.amount)) " +
            "FROM Expense e " +
            "WHERE e.owner = :owner AND YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month " +
            "GROUP BY e.category")
    List<CategoryTotal> getMonthlySummary(@Param("owner") AppUser owner,
                                          @Param("year") int year,
                                          @Param("month") int month);
}