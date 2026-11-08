package com.nabiha.expense_tracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByCategoryIgnoreCase(String category, Pageable pageable);
    @Query("SELECT new com.nabiha.expense_tracker.CategoryTotal(e.category, SUM(e.amount)) " +
            "FROM Expense e " +
            "WHERE YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month " +
            "GROUP BY e.category")
    List<CategoryTotal> getMonthlySummary(@Param("year") int year, @Param("month") int month);
}
