package com.finpeer.dao;

import com.finpeer.model.Loan;
import com.finpeer.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanDAO implements LoanDAO {
    private static final String SELECT = "SELECT loan_id, borrower_id, lender_id, principal, interest_rate, term_months, status, request_date, disbursement_date, schedule_created_at FROM loans";
    @Override public Loan save(Loan loan) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO loans (loan_id, borrower_id, principal, interest_rate, term_months, status, request_date) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, loan.getLoanId()); statement.setInt(2, loan.getBorrowerId()); statement.setDouble(3, loan.getPrincipal()); statement.setDouble(4, loan.getInterestRate()); statement.setInt(5, loan.getTermMonths()); statement.setString(6, loan.getStatus()); statement.setDate(7, Date.valueOf(loan.getRequestDate())); statement.executeUpdate();
            return loan;
        } catch (SQLException exception) { throw new IllegalStateException("Could not save loan", exception); }
    }
    @Override public Optional<Loan> findById(int id) { return find("loan_id = ?", id).stream().findFirst(); }
    @Override public List<Loan> findByBorrower(int id) { return find("borrower_id = ?", id); }
    @Override public List<Loan> findByLender(int id) { return find("lender_id = ?", id); }
    @Override public List<Loan> findByStatus(String status) { return find("status = ?", status); }
    @Override public List<Loan> findAll() { return find("1 = 1"); }
    private List<Loan> find(String condition, Object... values) {
        List<Loan> loans = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(SELECT + " WHERE " + condition + " ORDER BY loan_id")) {
            for (int index = 0; index < values.length; index++) statement.setObject(index + 1, values[index]);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) loans.add(map(result)); } return loans;
        } catch (SQLException exception) { throw new IllegalStateException("Could not read loans", exception); }
    }
    @Override public boolean updateStatus(int id, String status) {
        if ("DISBURSED".equals(status)) return update("UPDATE loans SET status = ?, disbursement_date = CURRENT_DATE, schedule_created_at = CURRENT_TIMESTAMP WHERE loan_id = ?", status, id);
        return update("UPDATE loans SET status = ? WHERE loan_id = ?", status, id);
    }
    @Override public boolean assignLender(int id, int lenderId) { return update("UPDATE loans SET lender_id = ? WHERE loan_id = ?", lenderId, id); }
    @Override public boolean updateInterestRate(int id, double interestRate) { return update("UPDATE loans SET interest_rate = ? WHERE loan_id = ?", interestRate, id); }
    private boolean update(String sql, Object first, Object second) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) { statement.setObject(1, first); statement.setObject(2, second); return statement.executeUpdate() == 1; }
        catch (SQLException exception) { throw new IllegalStateException("Could not update loan", exception); }
    }
    private Loan map(ResultSet result) throws SQLException {
        Loan loan = new Loan(); loan.setLoanId(result.getInt("loan_id")); loan.setBorrowerId(result.getInt("borrower_id")); loan.setLenderId(result.getInt("lender_id")); loan.setPrincipal(result.getDouble("principal")); loan.setInterestRate(result.getDouble("interest_rate")); loan.setTermMonths(result.getInt("term_months")); loan.setStatus(result.getString("status"));
        Date request = result.getDate("request_date"); Date disbursement = result.getDate("disbursement_date"); Timestamp scheduleCreated = result.getTimestamp("schedule_created_at"); loan.setRequestDate(request == null ? null : request.toLocalDate()); loan.setDisbursementDate(disbursement == null ? null : disbursement.toLocalDate()); loan.setScheduleCreatedAt(scheduleCreated == null ? null : scheduleCreated.toLocalDateTime()); return loan;
    }
}