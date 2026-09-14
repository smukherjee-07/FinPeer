package com.finpeer.model;

import java.time.LocalDate;

/** Represents a single scheduled or made repayment installment on a loan. */
public class Repayment {
    private int repaymentId;
    private int loanId;
    private double amountDue;
    private double amountPaid;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String status; // PENDING, PAID, LATE, MISSED

    public int getRepaymentId() { return repaymentId; }
    public void setRepaymentId(int repaymentId) { this.repaymentId = repaymentId; }

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public double getAmountDue() { return amountDue; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
