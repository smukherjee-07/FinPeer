package com.finpeer.dao;

import com.finpeer.model.Transaction;
import java.util.List;

public interface TransactionDAO {
    Transaction save(Transaction transaction);
    List<Transaction> findByLoan(int loanId);
}
