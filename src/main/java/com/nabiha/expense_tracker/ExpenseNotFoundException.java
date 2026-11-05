package com.nabiha.expense_tracker;

public class ExpenseNotFoundException extends RuntimeException{
    public ExpenseNotFoundException(Long id){
        super("Expense not found with id: "+id);
    }
}
