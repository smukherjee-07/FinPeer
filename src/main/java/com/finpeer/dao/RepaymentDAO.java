package com.finpeer.dao;

import com.finpeer.model.Repayment;
import java.util.List;

public interface RepaymentDAO {
    Repayment save(Repayment repayment);
    Repayment findById(int repaymentId);
    List<Repayment> findByLoan(int loanId);
    List<Repayment> findOverdue();
    boolean markPaid(int repaymentId, double amountPaid);
}
