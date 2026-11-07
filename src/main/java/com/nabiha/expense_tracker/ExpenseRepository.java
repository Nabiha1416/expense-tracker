package com.nabiha.expense_tracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByCategoryIgnoreCase(String category, Pageable pageable);
}
