package com.finpeer.service;

import static org.junit.jupiter.api.Assertions.*;
import com.finpeer.dao.*;
import com.finpeer.exception.InvalidLoanStateException;
import com.finpeer.model.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class LoanServiceTest {
    @Test
    void requestsPendingLoanWithCurrentDate() {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan loan = new LoanService(loans, new FakeTransactionDAO(), new FakeRepaymentDAO(), lenderDAO()).requestLoan(4, 1000, 12, 12);
        assertEquals("PENDING", loan.getStatus());
        assertEquals(4, loan.getBorrowerId());
        assertNotNull(loan.getRequestDate());
    }

    @Test
    void borrowerUsesSuggestedInterestRate() {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan loan = new LoanService(loans, new FakeTransactionDAO(), new FakeRepaymentDAO(), lenderDAO()).requestLoan(4, 1000, 18, 12);
        assertEquals(18, loan.getInterestRate());
    }

    @Test
    void approvesOnlyPendingLoanForRegisteredLender() throws InvalidLoanStateException {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan loan = pendingLoan(); loans.save(loan);
        new LoanService(loans, new FakeTransactionDAO(), new FakeRepaymentDAO(), lenderDAO()).approveLoan(loan.getLoanId(), 8);
        assertEquals("APPROVED", loans.loan.getStatus());
        assertEquals(8, loans.loan.getLenderId());
    }

    @Test
    void lenderCanChangeSuggestedRate() throws InvalidLoanStateException {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan loan = pendingLoan(); loan.setInterestRate(18); loans.save(loan);
        new LoanService(loans, new FakeTransactionDAO(), new FakeRepaymentDAO(), lenderDAO()).approveLoan(loan.getLoanId(), 8, 10);
        assertEquals(10, loans.loan.getInterestRate());
    }

    @Test
    void disbursementWritesLedgerAndSchedule() throws InvalidLoanStateException {
        FakeLoanDAO loans = new FakeLoanDAO();
        Loan loan = pendingLoan(); loan.setStatus("APPROVED"); loans.save(loan);
        FakeTransactionDAO transactions = new FakeTransactionDAO(); FakeRepaymentDAO repayments = new FakeRepaymentDAO();
        new LoanService(loans, transactions, repayments, lenderDAO()).disburseLoan(loan.getLoanId());
        assertEquals("DISBURSED", loans.loan.getStatus());
        assertEquals("DISBURSEMENT", transactions.transaction.getType());
        assertEquals(12, repayments.items.size());
    }

    private Loan pendingLoan() { Loan loan = new Loan(); loan.setLoanId(1); loan.setBorrowerId(4); loan.setPrincipal(1000); loan.setInterestRate(12); loan.setTermMonths(12); loan.setStatus("PENDING"); loan.setRequestDate(java.time.LocalDate.now()); return loan; }
    private UserDAO lenderDAO() { return new UserDAO() {
        public User save(User user) { return user; }
        public Optional<User> findById(int id) { return id == 8 ? Optional.of(new User(8, "Lender", "l@example.com", "hash", "LENDER")) : Optional.empty(); }
        public Optional<User> findByEmail(String email) { return Optional.empty(); }
        public List<User> findAll() { return List.of(); }
        public boolean update(User user) { return true; }
        public boolean delete(int id) { return true; }
    }; }

    private static class FakeLoanDAO implements LoanDAO {
        Loan loan; public Loan save(Loan value) { if (value.getLoanId() == 0) value.setLoanId(1); loan = value; return value; }
        public Optional<Loan> findById(int id) { return Optional.ofNullable(loan); }
        public List<Loan> findByBorrower(int id) { return loan == null ? List.of() : List.of(loan); }
        public List<Loan> findByLender(int id) { return loan == null ? List.of() : List.of(loan); }
        public List<Loan> findByStatus(String status) { return loan != null && status.equals(loan.getStatus()) ? List.of(loan) : List.of(); }
        public List<Loan> findAll() { return loan == null ? List.of() : List.of(loan); }
        public boolean updateStatus(int id, String status) { loan.setStatus(status); return true; }
        public boolean assignLender(int id, int lenderId) { loan.setLenderId(lenderId); return true; }
        public boolean updateInterestRate(int id, double interestRate) { loan.setInterestRate(interestRate); return true; }
    }
    private static class FakeRepaymentDAO implements RepaymentDAO {
        List<Repayment> items = new ArrayList<>(); public Repayment save(Repayment repayment) { repayment.setRepaymentId(items.size() + 1); items.add(repayment); return repayment; }
        public Repayment findById(int id) { return items.stream().filter(item -> item.getRepaymentId() == id).findFirst().orElse(null); }
        public List<Repayment> findByLoan(int id) { return items; } public List<Repayment> findOverdue() { return List.of(); } public boolean markPaid(int id, double amount) { return true; }
    }
    private static class FakeTransactionDAO implements TransactionDAO { Transaction transaction; public Transaction save(Transaction value) { transaction = value; return value; } public List<Transaction> findByLoan(int id) { return transaction == null ? List.of() : List.of(transaction); } }
}
