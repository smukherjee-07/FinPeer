package com.finpeer.cli;

import com.finpeer.dao.JdbcLoanDAO;
import com.finpeer.dao.JdbcRepaymentDAO;
import com.finpeer.dao.JdbcUserDAO;
import com.finpeer.model.Loan;
import com.finpeer.model.Repayment;
import com.finpeer.model.User;
import com.finpeer.service.LoanService;
import com.finpeer.service.RepaymentService;
import java.time.LocalDate;
import java.util.Scanner;

/** Console menu for Borrower role: request loans, view repayment schedule, make repayments. */
public class BorrowerMenu {
    private final Scanner scanner;
    private final User user;
    public BorrowerMenu(Scanner scanner, User user) { this.scanner = scanner; this.user = user; }
    public void show() {
        LoanService loans = new LoanService(); RepaymentService repayments = new RepaymentService(); JdbcLoanDAO loanDAO = new JdbcLoanDAO(); JdbcRepaymentDAO repaymentDAO = new JdbcRepaymentDAO(); JdbcUserDAO userDAO = new JdbcUserDAO();
        while (true) {
            System.out.println("\nBorrower: " + user.getName()); System.out.println("1. Request loan\n2. My loans\n3. My repayment schedule\n4. Make repayment\n5. View loan by ID\n0. Logout");
            String choice = scanner.nextLine().trim();
            try {
                if ("1".equals(choice)) { System.out.print("Principal: "); double principal = Double.parseDouble(scanner.nextLine()); System.out.print("Suggested annual interest rate (5-24%): "); double suggestedRate = Double.parseDouble(scanner.nextLine()); System.out.print("Term months (3-60): "); int term = Integer.parseInt(scanner.nextLine()); Loan loan = loans.requestLoan(user.getUserId(), principal, suggestedRate, term); System.out.printf("Loan #%d requested with your suggested rate of %.2f%%. The lender may accept or change it.%n", loan.getLoanId(), loan.getInterestRate()); }
                else if ("2".equals(choice)) loanDAO.findByBorrower(user.getUserId()).forEach(this::printLoan);
                else if ("3".equals(choice)) { System.out.print("Loan ID: "); int loanId = Integer.parseInt(scanner.nextLine()); Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found")); if (loan.getBorrowerId() != user.getUserId()) throw new IllegalArgumentException("That loan does not belong to you"); printSchedule(loan, repaymentDAO.findByLoan(loanId)); }
                else if ("4".equals(choice)) { System.out.print("Loan ID: "); int loanId = Integer.parseInt(scanner.nextLine()); Loan loan = loanDAO.findById(loanId).orElseThrow(() -> new IllegalArgumentException("Loan not found")); if (loan.getBorrowerId() != user.getUserId()) throw new IllegalArgumentException("That loan does not belong to you"); System.out.print("Amount to pay: "); double amount = Double.parseDouble(scanner.nextLine()); double remaining = repayments.makeLoanRepayment(loanId, amount); System.out.printf("Payment recorded. Remaining balance: %.2f%n", remaining); }
                else if ("5".equals(choice)) { System.out.print("Loan ID: "); Loan loan = loanDAO.findById(Integer.parseInt(scanner.nextLine())).orElseThrow(() -> new IllegalArgumentException("Loan not found")); if (loan.getBorrowerId() != user.getUserId()) throw new IllegalArgumentException("That loan does not belong to you"); printDetails(loan, userDAO); }
                else if ("0".equals(choice)) return; else System.out.println("Choose a listed option.");
            } catch (Exception exception) { System.out.println("Error: " + exception.getMessage()); }
        }
    }
    private void printLoan(Loan loan) { System.out.printf("%d | principal %.2f | rate %.2f%% | term %d months | %s%n", loan.getLoanId(), loan.getPrincipal(), loan.getInterestRate(), loan.getTermMonths(), loan.getStatus()); }
    private void printDetails(Loan loan, JdbcUserDAO userDAO) { User borrower = userDAO.findById(loan.getBorrowerId()).orElse(null); System.out.printf("Loan #%d | borrower: %s <%s> | amount: %.2f | rate: %.2f%% | term: %d months | status: %s | requested: %s%n", loan.getLoanId(), borrower == null ? "Unknown" : borrower.getName(), borrower == null ? "" : borrower.getEmail(), loan.getPrincipal(), loan.getInterestRate(), loan.getTermMonths(), loan.getStatus(), loan.getRequestDate()); }
    private void printSchedule(Loan loan, java.util.List<Repayment> schedule) {
        LocalDate firstDue = schedule.stream().map(Repayment::getDueDate).min(LocalDate::compareTo).orElse(null);
        LocalDate lastDue = schedule.stream().map(Repayment::getDueDate).max(LocalDate::compareTo).orElse(null);
        double emi = schedule.isEmpty() ? 0 : schedule.get(0).getAmountDue();
        double remaining = schedule.stream().mapToDouble(item -> item.getAmountDue() - item.getAmountPaid()).sum();
        System.out.printf("Schedule created: %s | first due: %s | ends: %s | monthly EMI: %.2f | remaining: %.2f%n", loan.getScheduleCreatedAt() == null ? "Not disbursed" : loan.getScheduleCreatedAt(), firstDue, lastDue, emi, remaining);
        schedule.forEach(item -> System.out.printf("Installment #%d | due: %s | EMI: %.2f | paid: %.2f | status: %s%n", item.getRepaymentId(), item.getDueDate(), item.getAmountDue(), item.getAmountPaid(), item.getStatus()));
    }
}
