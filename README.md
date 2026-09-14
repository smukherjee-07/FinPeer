<div align="center">

# 💰 FinPeer

**A console-based Peer Lending / Microfinance Management System built in Java & MySQL.**

[![Java](https://img.shields.io/badge/Java-25%20LTS-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/build-Maven-red?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![JUnit5](https://img.shields.io/badge/tested%20with-JUnit%205-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey)]()

</div>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Requirements](#-requirements)
- [Installation](#-installation)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Set Up the Database](#2-set-up-the-database)
  - [3. Configure Credentials](#3-configure-credentials)
  - [4. Build the Project](#4-build-the-project)
- [Running the Project](#️-running-the-project)
- [Testing](#-testing)
- [Project Structure](#️-project-structure)
- [Loan Lifecycle](#-loan-lifecycle)
- [Configuration Reference](#️-configuration-reference)
- [Troubleshooting](#️-troubleshooting)
- [Contributing](#-contributing)

---

## 📋 Overview

FinPeer simulates a peer-to-peer microfinance platform: borrowers request
loans, lenders review and fund them, and the system manages the full
lifecycle — disbursement, repayment scheduling, transaction ledger, and
reporting — with role-based access control. All financial movement is
simulated within the application's own ledger; no real payment gateways
are involved.

---

## ✨ Features

- 👥 **User Management** — registration/login for Admin, Borrower, and Lender roles
- 🔄 **Loan Lifecycle** — request → approve/reject → disburse → repay → close
- 📒 **Transactions & Ledger** — full audit trail of every fund movement
- 📊 **Reporting** — outstanding loans, defaulters, lender portfolio summaries
- 🔐 **Role-Based Access Control** — each role sees only its own console menu
- 🛡️ **Validation & Custom Exceptions** — invalid states and insufficient funds are caught explicitly

---

## 📋 Requirements

| Tool | Purpose |
|---|---|
| [Java 25 LTS (JDK)](https://openjdk.org/) | Compiles and runs the application |
| [MySQL 8](https://www.mysql.com/) | Persists users, loans, repayments, and transactions |
| [Maven](https://maven.apache.org/) | Build and dependency management |
| [Git](https://git-scm.com/) | Clones the repository |

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/<smukherjee-07>/FinPeer.git
cd FinPeer
```

### 2. Set Up the Database

<details>
<summary><strong>Windows</strong></summary>

```powershell
mysql -u root -p < sql\schema.sql
```
</details>

<details>
<summary><strong>Linux / macOS</strong></summary>

```bash
mysql -u root -p < sql/schema.sql
```
</details>

This creates the `finpeer` database along with the `users`, `loans`,
`repayments`, and `transactions` tables.

### 3. Configure Credentials

FinPeer can be configured two ways — pick whichever fits your workflow.

<details>
<summary><strong>Option A: Properties file</strong></summary>

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Then edit `db.properties` with your MySQL username and password.
</details>

<details>
<summary><strong>Option B: Environment variables</strong></summary>

The application also accepts `FINPEER_DB_URL`, `FINPEER_DB_USERNAME`, and
`FINPEER_DB_PASSWORD` environment variables, which take precedence over
`db.properties` when set.

```bash
export FINPEER_DB_URL="jdbc:mysql://localhost:3306/finpeer?useSSL=false&serverTimezone=UTC"
export FINPEER_DB_USERNAME="root"
export FINPEER_DB_PASSWORD="your_password_here"
```
</details>

### 4. Build the Project

```bash
mvn clean install
```

---

## ▶️ Running the Project

| Method | Command |
|---|---|
| Via Maven | `mvn exec:java` |
| Packaged JAR | `java -jar target/finpeer.jar` |

You'll be greeted with a role-based console menu:

```text
=== Welcome to FinPeer ===
1. Login
2. Register
3. Exit
```

---

## 🧪 Testing

```bash
mvn test
```

---

## 🗂️ Project Structure

```text
FinPeer/
│
├── README.md
├── statement.md
├── pom.xml
├── .gitignore
│
├── docs/
│   ├── architecture-diagram.png
│   ├── workflow-diagram.png
│   ├── use-case-diagram.png
│   ├── class-diagram.png
│   ├── sequence-diagram.png
│   └── er-diagram.png
│
├── sql/
│   └── schema.sql
│
└── src/
    │
    ├── main/
    │   │
    │   ├── java/
    │   │   └── com/
    │   │       └── finpeer/
    │   │           │
    │   │           ├── Main.java
    │   │           │
    │   │           ├── model/
    │   │           │   ├── User.java
    │   │           │   ├── Borrower.java
    │   │           │   ├── Lender.java
    │   │           │   ├── Loan.java
    │   │           │   ├── Repayment.java
    │   │           │   └── Transaction.java
    │   │           │
    │   │           ├── dao/
    │   │           │   ├── UserDAO.java
    │   │           │   ├── LoanDAO.java
    │   │           │   ├── RepaymentDAO.java
    │   │           │   └── TransactionDAO.java
    │   │           │
    │   │           ├── service/
    │   │           │   ├── AuthService.java
    │   │           │   ├── LoanService.java
    │   │           │   ├── RepaymentService.java
    │   │           │   └── ReportService.java
    │   │           │
    │   │           ├── cli/
    │   │           │   ├── BorrowerMenu.java
    │   │           │   ├── LenderMenu.java
    │   │           │   └── AdminMenu.java
    │   │           │
    │   │           ├── exception/
    │   │           │   ├── AuthenticationException.java
    │   │           │   ├── InsufficientFundsException.java
    │   │           │   └── InvalidLoanStateException.java
    │   │           │
    │   │           ├── util/
    │   │           │   ├── DBConnection.java
    │   │           │   ├── InputValidator.java
    │   │           │   └── PasswordUtil.java
    │   │           │
    │   │           └── config/
    │   │               └── AppConfig.java
    │   │
    │   └── resources/
    │       ├── db.properties.example
    │       └── db.properties
    │
    └── test/
        └── java/
            └── com/
                └── finpeer/
                    ├── service/
                    │   ├── AuthServiceTest.java
                    │   ├── LoanServiceTest.java
                    │   ├── RepaymentServiceTest.java
                    │   └── ReportServiceTest.java
                    │
                    ├── util/
                    │   ├── InputValidatorTest.java
                    │   └── PasswordUtilTest.java
                    │
                    └── integration/
                        └── DatabaseIntegrationTest.java
```

---

## 🔄 Loan Lifecycle

```text
Borrower Request
      │
      ▼
   PENDING ──reject──> REJECTED
      │
    approve
      │
      ▼
  APPROVED ──disburse──> DISBURSED
                              │
                     repayments made
                              │
                              ▼
                           CLOSED
                    (or DEFAULTED on missed payments)
```

---

## ⚙️ Configuration Reference

| Variable / Property | Description | Default |
|---|---|---|
| `db.url` / `FINPEER_DB_URL` | JDBC connection string | `jdbc:mysql://localhost:3306/finpeer` |
| `db.username` / `FINPEER_DB_USERNAME` | MySQL username | `root` |
| `db.password` / `FINPEER_DB_PASSWORD` | MySQL password | — |
| `MIN_INTEREST_RATE` / `MAX_INTEREST_RATE` | Allowed interest rate bounds (`AppConfig`) | `5.0` – `24.0` |
| `MIN_TERM_MONTHS` / `MAX_TERM_MONTHS` | Allowed loan term bounds (`AppConfig`) | `3` – `60` |

---

## 🛠️ Troubleshooting

| Issue | Likely Fix |
|---|---|
| `Communications link failure` | MySQL isn't running, or the port/host in your connection string is wrong. |
| `Access denied for user` | Double-check `db.username` / `db.password` (or the `FINPEER_DB_*` env vars). |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Run `mvn clean install` to pull the JDBC driver dependency. |
| Menu doesn't reflect role permissions | Confirm the logged-in user's `role` column matches `ADMIN` / `BORROWER` / `LENDER` exactly. |

---

## 🤝 Contributing

This is an academic submission, but suggestions are welcome:

1. Fork the project
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request