package com.finpeer;

import com.finpeer.cli.AdminMenu;
import com.finpeer.cli.BorrowerMenu;
import com.finpeer.cli.LenderMenu;
import com.finpeer.exception.AuthenticationException;
import com.finpeer.model.User;
import com.finpeer.service.AuthService;
import java.util.Scanner;

/**
 * FinPeer - Peer Lending / Microfinance Management System
 * Entry point for the console (CLI) application.
 *
 * Responsibilities:
 *  - Bootstraps the application
 *  - Shows the top-level menu (Login / Register / Exit)
 *  - Routes authenticated users to the correct role-based menu
 *    (AdminMenu / BorrowerMenu / LenderMenu)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Welcome to FinPeer ===");
        AuthService authService = new AuthService();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Login\n2. Register\n0. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine().trim();
                try {
                    if ("1".equals(choice)) {
                        System.out.print("Email: "); String email = scanner.nextLine();
                        System.out.print("Password: "); String password = scanner.nextLine();
                        User user = authService.login(email, password);
                        if ("ADMIN".equals(user.getRole())) new AdminMenu(scanner, user).show();
                        else if ("BORROWER".equals(user.getRole())) new BorrowerMenu(scanner, user).show();
                        else new LenderMenu(scanner, user).show();
                    } else if ("2".equals(choice)) {
                        System.out.print("Name: "); String name = scanner.nextLine();
                        System.out.print("Email: "); String email = scanner.nextLine();
                        System.out.print("Password (8+ characters): "); String password = scanner.nextLine();
                        System.out.print("Role (BORROWER/LENDER): "); String role = scanner.nextLine();
                        authService.register(name, email, password, role); System.out.println("Registration successful.");
                    } else if ("0".equals(choice)) return;
                    else System.out.println("Choose a listed option.");
                } catch (AuthenticationException | IllegalArgumentException exception) { System.out.println("Error: " + exception.getMessage()); }
                catch (RuntimeException exception) { System.out.println("Application error: " + exception.getMessage()); }
            }
        }
    }
}
