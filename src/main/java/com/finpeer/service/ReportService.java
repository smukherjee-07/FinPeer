package com.finpeer.service;

import com.finpeer.dao.JdbcLoanDAO;
import com.finpeer.dao.JdbcRepaymentDAO;
import com.finpeer.dao.LoanDAO;
import com.finpeer.dao.RepaymentDAO;

/** Aggregation/reporting logic: outstanding loans, defaulters, lender portfolio summaries. */
public class ReportService {
    private final LoanDAO loanDAO;
    private final RepaymentDAO repaymentDAO;
    public ReportService() { this(new JdbcLoanDAO(), new JdbcRepaymentDAO()); }
    public ReportService(LoanDAO loanDAO, RepaymentDAO repaymentDAO) { this.loanDAO = loanDAO; this.repaymentDAO = repaymentDAO; }

    public void printOutstandingLoansReport() {
        loanDAO.findAll().stream().filter(loan -> "DISBURSED".equals(loan.getStatus()) || "APPROVED".equals(loan.getStatus())).forEach(loan -> System.out.printf("Loan #%d | borrower %d | principal %.2f | status %s%n", loan.getLoanId(), loan.getBorrowerId(), loan.getPrincipal(), loan.getStatus()));
    }

    public void printDefaultersReport() {
        repaymentDAO.findOverdue().forEach(repayment -> System.out.printf("Loan #%d | repayment #%d | due %.2f | due date %s%n", repayment.getLoanId(), repayment.getRepaymentId(), repayment.getAmountDue(), repayment.getDueDate()));
    }
}
