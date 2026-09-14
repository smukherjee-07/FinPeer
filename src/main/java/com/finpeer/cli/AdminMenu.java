package com.finpeer.cli;

import com.finpeer.dao.JdbcUserDAO;
import com.finpeer.model.User;
import com.finpeer.service.ReportService;
import java.util.Scanner;

/** Console menu for Admin role: manage users, view reports, oversee loans. */
public class AdminMenu {
    private final Scanner scanner;
    private final User user;
    public AdminMenu(Scanner scanner, User user) { this.scanner = scanner; this.user = user; }
    public void show() {
        ReportService reports = new ReportService(); JdbcUserDAO users = new JdbcUserDAO();
        while (true) {
            System.out.println("\nAdmin: " + user.getName());
            System.out.println("1. Users\n2. Outstanding loans\n3. Overdue repayments\n0. Logout");
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) users.findAll().forEach(item -> System.out.printf("%d | %s | %s | %s%n", item.getUserId(), item.getName(), item.getEmail(), item.getRole()));
            else if ("2".equals(choice)) reports.printOutstandingLoansReport();
            else if ("3".equals(choice)) reports.printDefaultersReport();
            else if ("0".equals(choice)) return;
            else System.out.println("Choose a listed option.");
        }
    }
}
