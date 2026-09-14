package com.finpeer.model;

import java.time.LocalDateTime;

/** Ledger entry for any money movement in the system (disbursement, repayment, etc.) */
public class Transaction {
    private int transactionId;
    private int loanId;
    private double amount;
    private String type; // DISBURSEMENT, REPAYMENT, FEE
    private LocalDateTime timestamp;

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
