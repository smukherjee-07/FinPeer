package com.finpeer.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Represents a loan request/agreement between a borrower and lender(s). */
public class Loan {
    private int loanId;
    private int borrowerId;
    private int lenderId;
    private double principal;
    private double interestRate;
    private int termMonths;
    private String status; // PENDING, APPROVED, REJECTED, DISBURSED, CLOSED, DEFAULTED
    private LocalDate requestDate;
    private LocalDate disbursementDate;
    private LocalDateTime scheduleCreatedAt;

    // Getters and setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public int getBorrowerId() { return borrowerId; }
    public void setBorrowerId(int borrowerId) { this.borrowerId = borrowerId; }

    public int getLenderId() { return lenderId; }
    public void setLenderId(int lenderId) { this.lenderId = lenderId; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getTermMonths() { return termMonths; }
    public void setTermMonths(int termMonths) { this.termMonths = termMonths; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getDisbursementDate() { return disbursementDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }

    public LocalDateTime getScheduleCreatedAt() { return scheduleCreatedAt; }
    public void setScheduleCreatedAt(LocalDateTime scheduleCreatedAt) { this.scheduleCreatedAt = scheduleCreatedAt; }
}
