package com.nabiha.expense_tracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    private ExpenseController controller;
    private AppUser currentUser;

    @BeforeEach
    void setUp() {
        controller = new ExpenseController(expenseRepository, userRepository);
        currentUser = new AppUser();
        currentUser.setUsername("nabiha");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nabiha", null, List.of()));
        when(userRepository.findByUsername("nabiha")).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void clearLogin() {
        SecurityContextHolder.clearContext();
    }
    @Test
    void addExpenseSetsOwnerToLoggedInUser() {
        Expense expense = new Expense();
        expense.setTitle("Coffee");

        controller.addExpense(expense);

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(captor.capture());
        assertSame(currentUser, captor.getValue().getOwner());
    }
    @Test
    void getExpenseOfAnotherUserIsNotFound() {
        when(expenseRepository.findByIdAndOwner(7L, currentUser)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> controller.getExpenseById(7L));
    }
}