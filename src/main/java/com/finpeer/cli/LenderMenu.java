package com.finpeer.cli;

import com.finpeer.dao.JdbcLoanDAO;
import com.finpeer.dao.JdbcUserDAO;
import com.finpeer.exception.InvalidLoanStateException;
import com.finpeer.model.Loan;
import com.finpeer.model.User;
import com.finpeer.service.LoanService;
import java.util.Scanner;

/** Console menu for Lender role: review loan requests, approve/reject, view portfolio. */
public class LenderMenu {
    private final Scanner scanner;
    private final User user;
    public LenderMenu(Scanner scanner, User user) { this.scanner = scanner; this.user = user; }
    public void show() {
        JdbcLoanDAO loanDAO = new JdbcLoanDAO(); JdbcUserDAO userDAO = new JdbcUserDAO(); LoanService loans = new LoanService();
        while (true) {
            System.out.println("\nLender: " + user.getName()); System.out.println("1. Pending loans\n2. Approve loan\n3. Disburse loan\n4. My portfolio\n5. View loan by ID\n0. Logout");
            String choice = scanner.nextLine().trim();
            try {
                if ("1".equals(choice)) loanDAO.findByStatus("PENDING").forEach(this::printLoan);
                else if ("2".equals(choice)) { System.out.print("Loan ID: "); int loanId = Integer.parseInt(scanner.nextLine()); Loan proposed = loanDAO.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found")); System.out.printf("Borrower suggested %.2f%%.%n", proposed.getInterestRate()); System.out.print("Keep it or enter a new rate (press Enter to keep): "); String rateInput = scanner.nextLine().trim(); double finalRate = rateInput.isEmpty() ? proposed.getInterestRate() : Double.parseDouble(rateInput); loans.approveLoan(loanId, user.getUserId(), finalRate); System.out.printf("Loan approved at %.2f%%.%n", finalRate); }
                else if ("3".equals(choice)) { System.out.print("Loan ID: "); loans.disburseLoan(Integer.parseInt(scanner.nextLine())); System.out.println("Loan disbursed."); }
                else if ("4".equals(choice)) loanDAO.findByLender(user.getUserId()).forEach(this::printLoan);
                else if ("5".equals(choice)) { System.out.print("Loan ID: "); printDetails(loanDAO.findById(Integer.parseInt(scanner.nextLine())).orElseThrow(() -> new IllegalArgumentException("Loan not found")), userDAO); }
                else if ("0".equals(choice)) return; else System.out.println("Choose a listed option.");
            } catch (RuntimeException | InvalidLoanStateException exception) { System.out.println("Error: " + exception.getMessage()); }
        }
    }
    private void printLoan(Loan loan) { System.out.printf("%d | borrower %d | principal %.2f | rate %.2f%% | %s%n", loan.getLoanId(), loan.getBorrowerId(), loan.getPrincipal(), loan.getInterestRate(), loan.getStatus()); }
    private void printDetails(Loan loan, JdbcUserDAO userDAO) { User borrower = userDAO.findById(loan.getBorrowerId()).orElse(null); System.out.printf("Loan #%d | borrower: %s <%s> | amount: %.2f | suggested/current rate: %.2f%% | term: %d months | lender: %d | status: %s | requested: %s%n", loan.getLoanId(), borrower == null ? "Unknown" : borrower.getName(), borrower == null ? "" : borrower.getEmail(), loan.getPrincipal(), loan.getInterestRate(), loan.getTermMonths(), loan.getLenderId(), loan.getStatus(), loan.getRequestDate()); }
}
