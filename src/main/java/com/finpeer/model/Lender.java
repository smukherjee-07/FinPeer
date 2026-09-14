package com.finpeer.model;

/** Extends User with lender-specific fields (available funds, portfolio, etc.) */
public class Lender extends User {
    private double availableFunds;

    public Lender() { super(); }

    public double getAvailableFunds() { return availableFunds; }
    public void setAvailableFunds(double availableFunds) { this.availableFunds = availableFunds; }
}
