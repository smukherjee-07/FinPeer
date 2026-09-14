package com.finpeer.model;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ModelTest {
    @Test
    void userStoresIdentityAndRole() {
        User user = new User(7, "Ada", "ada@example.com", "hash", "LENDER");
        assertEquals(7, user.getUserId());
        assertEquals("Ada", user.getName());
        assertEquals("ada@example.com", user.getEmail());
        assertEquals("LENDER", user.getRole());
    }

    @Test
    void loanStoresDatesAndFinancialFields() {
        Loan loan = new Loan();
        loan.setLoanId(3);
        loan.setPrincipal(1200);
        loan.setInterestRate(12);
        loan.setTermMonths(12);
        loan.setRequestDate(LocalDate.of(2026, 1, 1));
        loan.setStatus("PENDING");
        assertEquals(3, loan.getLoanId());
        assertEquals(1200, loan.getPrincipal());
        assertEquals(LocalDate.of(2026, 1, 1), loan.getRequestDate());
        assertEquals("PENDING", loan.getStatus());
    }

    @Test
    void repaymentAndTransactionStoreAmounts() {
        Repayment repayment = new Repayment();
        repayment.setAmountDue(100);
        repayment.setAmountPaid(25);
        Transaction transaction = new Transaction();
        transaction.setAmount(25);
        transaction.setType("REPAYMENT");
        assertEquals(100, repayment.getAmountDue());
        assertEquals(25, repayment.getAmountPaid());
        assertEquals("REPAYMENT", transaction.getType());
    }
}
