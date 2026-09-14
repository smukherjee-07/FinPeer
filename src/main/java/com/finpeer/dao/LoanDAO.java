package com.finpeer.dao;

import com.finpeer.model.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanDAO {
    Loan save(Loan loan);
    Optional<Loan> findById(int loanId);
    List<Loan> findByBorrower(int borrowerId);
    List<Loan> findByLender(int lenderId);
    List<Loan> findByStatus(String status);
    List<Loan> findAll();
    boolean updateStatus(int loanId, String status);
    boolean assignLender(int loanId, int lenderId);
    boolean updateInterestRate(int loanId, double interestRate);
}
