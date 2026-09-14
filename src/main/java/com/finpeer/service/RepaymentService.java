package com.finpeer.service;

import com.finpeer.exception.InsufficientFundsException;
import com.finpeer.dao.JdbcLoanDAO;
import com.finpeer.dao.JdbcRepaymentDAO;
import com.finpeer.dao.JdbcTransactionDAO;
import com.finpeer.dao.LoanDAO;
import com.finpeer.dao.RepaymentDAO;
import com.finpeer.dao.TransactionDAO;
import com.finpeer.model.Loan;
import com.finpeer.model.Repayment;
import com.finpeer.model.Transaction;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Business logic for generating repayment schedules and processing repayments. */
public class RepaymentService {
    private final RepaymentDAO repaymentDAO;
    private final TransactionDAO transactionDAO;
    private final LoanDAO loanDAO;
    public RepaymentService() { this(new JdbcRepaymentDAO(), new JdbcTransactionDAO(), new JdbcLoanDAO()); }
    public RepaymentService(RepaymentDAO repaymentDAO, TransactionDAO transactionDAO, LoanDAO loanDAO) { this.repaymentDAO = repaymentDAO; this.transactionDAO = transactionDAO; this.loanDAO = loanDAO; }

    public void generateSchedule(int loanId) {
        Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        if (!"DISBURSED".equals(loan.getStatus())) throw new IllegalStateException("Loan must be disbursed first");
        if (!repaymentDAO.findByLoan(loanId).isEmpty()) throw new IllegalStateException("Repayment schedule already exists");
        double monthlyRate = loan.getInterestRate() / 100.0 / 12.0;
        double payment = monthlyRate == 0 ? loan.getPrincipal() / loan.getTermMonths() : loan.getPrincipal() * monthlyRate / (1 - Math.pow(1 + monthlyRate, -loan.getTermMonths()));
        for (int month = 1; month <= loan.getTermMonths(); month++) { Repayment repayment = new Repayment(); repayment.setLoanId(loanId); repayment.setAmountDue(Math.round(payment * 100.0) / 100.0); repayment.setDueDate(LocalDate.now().plusMonths(month)); repayment.setStatus("PENDING"); repaymentDAO.save(repayment); }
    }

    public void makeRepayment(int repaymentId, double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Repayment must be positive");
        Repayment repayment = repaymentDAO.findById(repaymentId);
        if (repayment == null) throw new IllegalArgumentException("Repayment not found");
        double remaining = repayment.getAmountDue() - repayment.getAmountPaid();
        if (amount > remaining + 0.005) throw new InsufficientFundsException("Repayment exceeds amount due");
        if (!repaymentDAO.markPaid(repaymentId, repayment.getAmountPaid() + amount)) throw new IllegalStateException("Could not process repayment");
        Transaction transaction = new Transaction(); transaction.setLoanId(repayment.getLoanId()); transaction.setAmount(amount); transaction.setType("REPAYMENT"); transactionDAO.save(transaction);
    }

    public double makeLoanRepayment(int loanId, double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Repayment must be positive");
        Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        if (!"DISBURSED".equals(loan.getStatus())) throw new IllegalStateException("This loan is not disbursed yet. A lender must approve and disburse it before repayment can begin.");
        List<Repayment> schedule = repaymentDAO.findByLoan(loanId);
        if (schedule.isEmpty()) throw new IllegalStateException("This loan has no repayment schedule yet. Repayment begins after lender disbursement.");
        double outstanding = schedule.stream().mapToDouble(item -> item.getAmountDue() - item.getAmountPaid()).sum();
        if (amount > outstanding + 0.005) throw new InsufficientFundsException(String.format("Payment exceeds the remaining balance of %.2f", outstanding));
        double remainingPayment = amount;
        for (Repayment installment : schedule.stream().sorted(Comparator.comparing(Repayment::getDueDate)).toList()) {
            double installmentBalance = installment.getAmountDue() - installment.getAmountPaid();
            if (installmentBalance <= 0.005) continue;
            double applied = Math.min(remainingPayment, installmentBalance);
            if (!repaymentDAO.markPaid(installment.getRepaymentId(), installment.getAmountPaid() + applied)) throw new IllegalStateException("Could not update repayment");
            Transaction transaction = new Transaction(); transaction.setLoanId(loanId); transaction.setAmount(applied); transaction.setType("REPAYMENT"); transactionDAO.save(transaction);
            remainingPayment -= applied;
            if (remainingPayment <= 0.005) break;
        }
        double balance = outstanding - amount;
        if (balance <= 0.005) loanDAO.updateStatus(loanId, "CLOSED");
        return Math.max(0, Math.round(balance * 100.0) / 100.0);
    }
}
