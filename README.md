# FinPeer

A console-based Peer Lending / Microfinance Management System built in Java
with MySQL, developed as a VITyarthi "Build Your Own Project" submission.

## Overview

FinPeer simulates a peer-to-peer microfinance platform: borrowers request
loans, lenders review and fund them, and the system manages the full
lifecycle — disbursement, repayment scheduling, transaction ledger, and
reporting — with role-based access control.

## Features

- **User Management** — registration/login for Admin, Borrower, and Lender roles
- **Loan Lifecycle** — request → approve/reject → disburse → repay → close
- **Transactions & Ledger** — full audit trail of every fund movement
- **Reporting** — outstanding loans, defaulters, lender portfolio summaries

## Technologies / Tools Used

- Java 25 LTS
- MySQL 8
- JDBC (mysql-connector-j)
- Maven (build & dependency management)
- JUnit 5 (testing)

## Project Structure

```
src/main/java/com/finpeer/
├── model/       Entity classes (User, Loan, Transaction, Repayment, ...)
├── dao/         Data access layer (JDBC/SQL)
├── service/     Business logic (loan lifecycle, repayments, reports)
├── cli/         Console menus per role
├── exception/   Custom checked exceptions
├── util/        DB connection, password hashing, input validation
└── config/      Application-wide constants
```

## Setup & Installation

1. Clone the repository:
   ```
   git clone https://github.com/<your-username>/FinPeer.git
   cd FinPeer
   ```
2. Create the database:
   ```
   mysql -u root -p < sql/schema.sql
   ```
3. Configure your database credentials:
   ```

   The application also accepts `FINPEER_DB_URL`, `FINPEER_DB_USERNAME`, and
   `FINPEER_DB_PASSWORD` environment variables. The URL should point to the
   `finpeer` MySQL database, for example:
   `jdbc:mysql://localhost:3306/finpeer?useSSL=false&serverTimezone=UTC`.
   cp src/main/resources/db.properties.example src/main/resources/db.properties
   # then edit db.properties with your MySQL username/password
   ```
4. Build the project:
   ```
   mvn clean install
   ```

## Running the Project

```
mvn exec:java
```

Or run the packaged JAR directly:

```
java -jar target/finpeer.jar
```

## Testing

```
mvn test
```

## Screenshots

_(Add CLI screenshots here once the application is functional.)_
