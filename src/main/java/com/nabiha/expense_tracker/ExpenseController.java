package com.nabiha.expense_tracker;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repository;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private AppUser currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@Valid @RequestBody Expense expense) {
        expense.setOwner(currentUser());
        return repository.save(expense);
    }

    @GetMapping
    public Page<Expense> getAllExpenses(@RequestParam(required = false) String category, Pageable pageable) {
        AppUser user = currentUser();
        if (category != null) {
            return repository.findByOwnerAndCategoryIgnoreCase(user, category, pageable);
        }
        return repository.findByOwner(user, pageable);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return repository.findByIdAndOwner(id, currentUser())
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long id) {
        Expense expense = repository.findByIdAndOwner(id, currentUser())
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        repository.delete(expense);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody Expense updated) {
        Expense existing = repository.findByIdAndOwner(id, currentUser())
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setExpenseDate(updated.getExpenseDate());
        return repository.save(existing);
    }

    @GetMapping("/summary")
    public List<CategoryTotal> getMonthlySummary(@RequestParam int year, @RequestParam int month) {
        return repository.getMonthlySummary(currentUser(), year, month);
    }
}