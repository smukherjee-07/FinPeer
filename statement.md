# Problem Statement

Traditional lending is often inaccessible to small borrowers and offers poor
returns to individual lenders due to intermediary costs and rigid institutional
processes. FinPeer addresses this by simulating a peer-to-peer microfinance
platform where individual lenders can fund loan requests from borrowers
directly, with the system handling matching, disbursement, repayment tracking,
and risk reporting.

## Scope

FinPeer is a console-based (CLI) Java application backed by a MySQL database.
It simulates the core lifecycle of peer-to-peer lending as an academic project —
it does not integrate with real payment gateways, banking APIs, or credit
bureaus, and does not use blockchain or machine learning. All financial
movement is simulated within the application's own ledger.

## Target Users

- **Borrowers** — individuals who request loans and repay them over time.
- **Lenders** — individuals who review loan requests and fund them, tracking
  their return on investment.
- **Admin** — oversees the platform, manages users, and monitors overall
  system health (defaults, outstanding loans, etc.).

## High-Level Features

- Role-based registration and login (Admin / Borrower / Lender)
- Loan request, approval/rejection, and disbursement workflow
- Repayment scheduling and processing with interest calculation
- Transaction ledger for all fund movements
- Reporting: outstanding loans, defaulters, lender portfolio summaries
