package com.finpeer.service;

import static org.junit.jupiter.api.Assertions.*;
import com.finpeer.dao.*;
import com.finpeer.model.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReportServiceTest {
    @Test
    void printsOutstandingLoans() {
        FakeLoanDAO loans = new FakeLoanDAO(); Loan loan = new Loan(); loan.setLoanId(12); loan.setBorrowerId(4); loan.setPrincipal(500); loan.setStatus("DISBURSED"); loans.loans.add(loan);
        String output = capture(() -> new ReportService(loans, new FakeRepaymentDAO()).printOutstandingLoansReport());
        assertTrue(output.contains("Loan #12"));
        assertTrue(output.contains("500.00"));
    }

    @Test
    void printsOverdueRepayments() {
        FakeRepaymentDAO repayments = new FakeRepaymentDAO(); Repayment repayment = new Repayment(); repayment.setLoanId(12); repayment.setRepaymentId(3); repayment.setAmountDue(75); repayment.setDueDate(java.time.LocalDate.now().minusDays(1)); repayments.items.add(repayment);
        String output = capture(() -> new ReportService(new FakeLoanDAO(), repayments).printDefaultersReport());
        assertTrue(output.contains("repayment #3"));
        assertTrue(output.contains("75.00"));
    }

    private String capture(Runnable action) { ByteArrayOutputStream output = new ByteArrayOutputStream(); PrintStream original = System.out; try { System.setOut(new PrintStream(output)); action.run(); return output.toString(); } finally { System.setOut(original); } }
    private static class FakeLoanDAO implements LoanDAO { List<Loan> loans = new ArrayList<>(); public Loan save(Loan value) { loans.add(value); return value; } public Optional<Loan> findById(int id) { return loans.stream().filter(item -> item.getLoanId() == id).findFirst(); } public List<Loan> findByBorrower(int id) { return loans; } public List<Loan> findByLender(int id) { return loans; } public List<Loan> findByStatus(String status) { return loans; } public List<Loan> findAll() { return loans; } public boolean updateStatus(int id, String status) { return true; } public boolean assignLender(int id, int lenderId) { return true; } public boolean updateInterestRate(int id, double interestRate) { return true; } }
    private static class FakeRepaymentDAO implements RepaymentDAO { List<Repayment> items = new ArrayList<>(); public Repayment save(Repayment value) { items.add(value); return value; } public Repayment findById(int id) { return null; } public List<Repayment> findByLoan(int id) { return items; } public List<Repayment> findOverdue() { return items; } public boolean markPaid(int id, double amount) { return true; } }
}
