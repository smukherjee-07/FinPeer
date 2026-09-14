package com.finpeer.dao;

import com.finpeer.model.Transaction;
import com.finpeer.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionDAO implements TransactionDAO {
    @Override public Transaction save(Transaction transaction) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions (loan_id, amount, type) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, transaction.getLoanId()); statement.setDouble(2, transaction.getAmount()); statement.setString(3, transaction.getType()); statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) transaction.setTransactionId(keys.getInt(1)); } return transaction;
        } catch (SQLException exception) { throw new IllegalStateException("Could not save transaction", exception); }
    }
    @Override public List<Transaction> findByLoan(int loanId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE loan_id = ? ORDER BY ts")) {
            statement.setInt(1, loanId); try (ResultSet result = statement.executeQuery()) { while (result.next()) { Transaction transaction = new Transaction(); transaction.setTransactionId(result.getInt("transaction_id")); transaction.setLoanId(result.getInt("loan_id")); transaction.setAmount(result.getDouble("amount")); transaction.setType(result.getString("type")); Timestamp timestamp = result.getTimestamp("ts"); transaction.setTimestamp(timestamp == null ? null : timestamp.toLocalDateTime()); transactions.add(transaction); } } return transactions;
        } catch (SQLException exception) { throw new IllegalStateException("Could not read transactions", exception); }
    }
}
