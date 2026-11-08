package com.nabiha.expense_tracker;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository repository;

    public ExpenseController(ExpenseRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@Valid @RequestBody Expense expense) {
        return repository.save(expense);
    }

    @GetMapping
    public Page<Expense> getAllExpenses(@RequestParam(required = false) String category, Pageable pageable) {
        if (category != null) {
            return repository.findByCategoryIgnoreCase(category, pageable);
        }
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        repository.deleteById(id);

    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id,@Valid @RequestBody Expense updated){
        Expense existing=repository.findById(id)
                .orElseThrow(()-> new ExpenseNotFoundException(id));
        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setExpenseDate(updated.getExpenseDate());

        return repository.save(existing);
    }
    @GetMapping("/summary")
    public List<CategoryTotal> getMonthlySummary(@RequestParam int year, @RequestParam int month) {
        return repository.getMonthlySummary(year, month);
    }
}
