package com.finpeer.dao;

import com.finpeer.model.Repayment;
import com.finpeer.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRepaymentDAO implements RepaymentDAO {
    @Override public Repayment save(Repayment repayment) {
        String sql = "INSERT INTO repayments (loan_id, amount_due, amount_paid, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, repayment.getLoanId()); statement.setDouble(2, repayment.getAmountDue()); statement.setDouble(3, repayment.getAmountPaid()); statement.setDate(4, Date.valueOf(repayment.getDueDate())); statement.setString(5, repayment.getStatus()); statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) repayment.setRepaymentId(keys.getInt(1)); } return repayment;
        } catch (SQLException exception) { throw new IllegalStateException("Could not save repayment", exception); }
    }
    @Override public Repayment findById(int id) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM repayments WHERE repayment_id = ?")) {
            statement.setInt(1, id); try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; }
        } catch (SQLException exception) { throw new IllegalStateException("Could not find repayment", exception); }
    }
    @Override public List<Repayment> findByLoan(int loanId) { return find("loan_id = ?", loanId); }
    @Override public List<Repayment> findOverdue() { return find("due_date < CURRENT_DATE AND status = 'PENDING'"); }
    private List<Repayment> find(String condition, Object... values) {
        List<Repayment> repayments = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM repayments WHERE " + condition + " ORDER BY due_date")) {
            for (int index = 0; index < values.length; index++) statement.setObject(index + 1, values[index]);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) repayments.add(map(result)); } return repayments;
        } catch (SQLException exception) { throw new IllegalStateException("Could not read repayments", exception); }
    }
    @Override public boolean markPaid(int id, double amountPaid) {
        String sql = "UPDATE repayments SET amount_paid = ?, paid_date = CASE WHEN ? >= amount_due THEN CURRENT_DATE ELSE paid_date END, status = CASE WHEN ? >= amount_due THEN 'PAID' ELSE 'PENDING' END WHERE repayment_id = ? AND amount_paid < amount_due";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) { statement.setDouble(1, amountPaid); statement.setDouble(2, amountPaid); statement.setDouble(3, amountPaid); statement.setInt(4, id); return statement.executeUpdate() == 1; }
        catch (SQLException exception) { throw new IllegalStateException("Could not mark repayment paid", exception); }
    }
    private Repayment map(ResultSet result) throws SQLException {
        Repayment repayment = new Repayment(); repayment.setRepaymentId(result.getInt("repayment_id")); repayment.setLoanId(result.getInt("loan_id")); repayment.setAmountDue(result.getDouble("amount_due")); repayment.setAmountPaid(result.getDouble("amount_paid"));
        Date due = result.getDate("due_date"); Date paid = result.getDate("paid_date"); repayment.setDueDate(due == null ? null : due.toLocalDate()); repayment.setPaidDate(paid == null ? null : paid.toLocalDate()); repayment.setStatus(result.getString("status")); return repayment;
    }
}
