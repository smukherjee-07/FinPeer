package com.finpeer.model;

/** Extends User with borrower-specific fields (credit score, active loans, etc.) */
public class Borrower extends User {
    private double creditScore;
    private double totalOutstanding;

    public Borrower() { super(); }

    public double getCreditScore() { return creditScore; }
    public void setCreditScore(double creditScore) { this.creditScore = creditScore; }

    public double getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(double totalOutstanding) { this.totalOutstanding = totalOutstanding; }
}
