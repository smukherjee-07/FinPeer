package com.finpeer.service;

import static org.junit.jupiter.api.Assertions.*;
import com.finpeer.dao.*;
import com.finpeer.exception.InsufficientFundsException;
import com.finpeer.model.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;

class RepaymentServiceTest {
    @Test
    void generatesAmortizedSchedule() {
        FakeLoanDAO loans = new FakeLoanDAO(); loans.loan = loan();
        FakeRepaymentDAO repayments = new FakeRepaymentDAO();
        new RepaymentService(repayments, new FakeTransactionDAO(), loans).generateSchedule(1);
        assertEquals(12, repayments.items.size());
        assertTrue(repayments.items.stream().allMatch(item -> item.getAmountDue() > 0 && "PENDING".equals(item.getStatus())));
    }

    @Test
    void rejectsOverpayment() {
        FakeRepaymentDAO repayments = new FakeRepaymentDAO(); Repayment repayment = repayment(); repayments.items.add(repayment);
        RepaymentService service = new RepaymentService(repayments, new FakeTransactionDAO(), new FakeLoanDAO());
        assertThrows(InsufficientFundsException.class, () -> service.makeRepayment(1, 101));
    }

    @Test
    void recordsPaymentAndLedgerEntry() throws InsufficientFundsException {
        FakeRepaymentDAO repayments = new FakeRepaymentDAO(); repayments.items.add(repayment());
        FakeTransactionDAO transactions = new FakeTransactionDAO();
        new RepaymentService(repayments, transactions, new FakeLoanDAO()).makeRepayment(1, 40);
        assertEquals(40, repayments.items.get(0).getAmountPaid());
        assertEquals("REPAYMENT", transactions.transaction.getType());
        assertEquals(40, transactions.transaction.getAmount());
    }

    @Test
    void appliesPaymentByLoanIdAndReturnsRemainingBalance() throws InsufficientFundsException {
        FakeLoanDAO loans = new FakeLoanDAO(); loans.loan = loan();
        FakeRepaymentDAO repayments = new FakeRepaymentDAO(); repayments.items.add(repayment());
        FakeTransactionDAO transactions = new FakeTransactionDAO();
        double remaining = new RepaymentService(repayments, transactions, loans).makeLoanRepayment(1, 40);
        assertEquals(60, remaining);
        assertEquals(40, repayments.items.get(0).getAmountPaid());
    }

    @Test
    void rejectsPaymentBeforeLoanDisbursement() {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan pending = loan(); pending.setStatus("PENDING"); loans.loan = pending;
        RepaymentService service = new RepaymentService(new FakeRepaymentDAO(), new FakeTransactionDAO(), loans);
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.makeLoanRepayment(1, 100));
        assertTrue(error.getMessage().contains("not disbursed"));
    }

    private Loan loan() { Loan loan = new Loan(); loan.setLoanId(1); loan.setPrincipal(1200); loan.setInterestRate(12); loan.setTermMonths(12); loan.setStatus("DISBURSED"); return loan; }
    private Repayment repayment() { Repayment repayment = new Repayment(); repayment.setRepaymentId(1); repayment.setLoanId(1); repayment.setAmountDue(100); repayment.setAmountPaid(0); repayment.setDueDate(LocalDate.now().plusMonths(1)); repayment.setStatus("PENDING"); return repayment; }
    private static class FakeLoanDAO implements LoanDAO { Loan loan; public Loan save(Loan value) { loan = value; return value; } public Optional<Loan> findById(int id) { return Optional.ofNullable(loan); } public List<Loan> findByBorrower(int id) { return List.of(); } public List<Loan> findByLender(int id) { return List.of(); } public List<Loan> findByStatus(String status) { return List.of(); } public List<Loan> findAll() { return List.of(); } public boolean updateStatus(int id, String status) { return true; } public boolean assignLender(int id, int lenderId) { return true; } public boolean updateInterestRate(int id, double interestRate) { return true; } }
    private static class FakeRepaymentDAO implements RepaymentDAO { List<Repayment> items = new ArrayList<>(); public Repayment save(Repayment value) { items.add(value); return value; } public Repayment findById(int id) { return items.stream().filter(item -> item.getRepaymentId() == id).findFirst().orElse(null); } public List<Repayment> findByLoan(int id) { return items; } public List<Repayment> findOverdue() { return List.of(); } public boolean markPaid(int id, double amount) { findById(id).setAmountPaid(amount); return true; } }
    private static class FakeTransactionDAO implements TransactionDAO { Transaction transaction; public Transaction save(Transaction value) { transaction = value; return value; } public List<Transaction> findByLoan(int id) { return List.of(); } }
}
