package com.finpeer.service;

import com.finpeer.exception.InvalidLoanStateException;
import com.finpeer.model.Loan;
import com.finpeer.dao.JdbcLoanDAO;
import com.finpeer.dao.JdbcTransactionDAO;
import com.finpeer.dao.LoanDAO;
import com.finpeer.dao.TransactionDAO;
import com.finpeer.dao.JdbcRepaymentDAO;
import com.finpeer.dao.RepaymentDAO;
import com.finpeer.dao.UserDAO;
import com.finpeer.dao.JdbcUserDAO;
import com.finpeer.model.Transaction;
import com.finpeer.model.Repayment;
import com.finpeer.config.AppConfig;
import java.time.LocalDate;
import java.security.SecureRandom;

/** Business logic for the loan lifecycle: request -> approve/reject -> disburse -> close. */
public class LoanService {
    private final LoanDAO loanDAO;
    private final TransactionDAO transactionDAO;
    private final RepaymentDAO repaymentDAO;
    private final UserDAO userDAO;
    private static final SecureRandom RANDOM = new SecureRandom();
    public LoanService() { this(new JdbcLoanDAO(), new JdbcTransactionDAO(), new JdbcRepaymentDAO(), new JdbcUserDAO()); }
    public LoanService(LoanDAO loanDAO, TransactionDAO transactionDAO) { this(loanDAO, transactionDAO, new JdbcRepaymentDAO(), new JdbcUserDAO()); }
    public LoanService(LoanDAO loanDAO, TransactionDAO transactionDAO, RepaymentDAO repaymentDAO) { this(loanDAO, transactionDAO, repaymentDAO, new JdbcUserDAO()); }
    public LoanService(LoanDAO loanDAO, TransactionDAO transactionDAO, RepaymentDAO repaymentDAO, UserDAO userDAO) { this.loanDAO = loanDAO; this.transactionDAO = transactionDAO; this.repaymentDAO = repaymentDAO; this.userDAO = userDAO; }

    public Loan requestLoan(int borrowerId, double principal, double interestRate, int termMonths) {
        if (borrowerId <= 0 || principal <= 0 || interestRate < AppConfig.MIN_INTEREST_RATE || interestRate > AppConfig.MAX_INTEREST_RATE || termMonths < AppConfig.MIN_TERM_MONTHS || termMonths > AppConfig.MAX_TERM_MONTHS) throw new IllegalArgumentException("Loan must use a positive principal, interest from 5% to 24%, and a 3 to 60 month term");
        Loan loan = new Loan(); loan.setLoanId(nextPublicLoanId()); loan.setBorrowerId(borrowerId); loan.setPrincipal(principal); loan.setInterestRate(interestRate); loan.setTermMonths(termMonths); loan.setStatus("PENDING"); loan.setRequestDate(LocalDate.now());
        return loanDAO.save(loan);
    }

    private int nextPublicLoanId() {
        for (int attempt = 0; attempt < 20; attempt++) {
            int candidate = 10000 + RANDOM.nextInt(990000);
            if (loanDAO.findById(candidate).isEmpty()) return candidate;
        }
        throw new IllegalStateException("Could not generate a unique loan ID. Please try again.");
    }

    public Loan requestLoan(int borrowerId, double principal, int termMonths) {
        return requestLoan(borrowerId, principal, AppConfig.DEFAULT_INTEREST_RATE, termMonths);
    }

    public void approveLoan(int loanId, int lenderId) throws InvalidLoanStateException {
        Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new InvalidLoanStateException("Loan not found"));
        approveLoan(loanId, lenderId, loan.getInterestRate());
    }

    public void approveLoan(int loanId, int lenderId, double lenderInterestRate) throws InvalidLoanStateException {
        Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new InvalidLoanStateException("Loan not found"));
        if (!"PENDING".equals(loan.getStatus())) throw new InvalidLoanStateException("Only pending loans can be approved");
        if (lenderInterestRate < AppConfig.MIN_INTEREST_RATE || lenderInterestRate > AppConfig.MAX_INTEREST_RATE) throw new InvalidLoanStateException("Interest rate must be between 5% and 24%");
        if (userDAO.findById(lenderId).filter(user -> "LENDER".equals(user.getRole())).isEmpty()) throw new InvalidLoanStateException("Only registered lenders can approve loans");
        if (lenderId <= 0 || !loanDAO.assignLender(loanId, lenderId) || !loanDAO.updateInterestRate(loanId, lenderInterestRate) || !loanDAO.updateStatus(loanId, "APPROVED")) throw new InvalidLoanStateException("Could not approve loan");
    }

    public void disburseLoan(int loanId) throws InvalidLoanStateException {
        Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new InvalidLoanStateException("Loan not found"));
        if (!"APPROVED".equals(loan.getStatus())) throw new InvalidLoanStateException("Only approved loans can be disbursed");
        if (!loanDAO.updateStatus(loanId, "DISBURSED")) throw new InvalidLoanStateException("Could not disburse loan");
        Transaction transaction = new Transaction(); transaction.setLoanId(loanId); transaction.setAmount(loan.getPrincipal()); transaction.setType("DISBURSEMENT"); transactionDAO.save(transaction);
        createSchedule(loan);
    }

    private void createSchedule(Loan loan) {
        double monthlyRate = loan.getInterestRate() / 100.0 / 12.0;
        double payment = monthlyRate == 0 ? loan.getPrincipal() / loan.getTermMonths() : loan.getPrincipal() * monthlyRate / (1 - Math.pow(1 + monthlyRate, -loan.getTermMonths()));
        for (int month = 1; month <= loan.getTermMonths(); month++) { Repayment repayment = new Repayment(); repayment.setLoanId(loan.getLoanId()); repayment.setAmountDue(Math.round(payment * 100.0) / 100.0); repayment.setDueDate(LocalDate.now().plusMonths(month)); repayment.setStatus("PENDING"); repaymentDAO.save(repayment); }
    }
}
