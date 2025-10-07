# SplitEase - Expense Splitting Application

## Overview

SplitEase is a Java-based command-line application designed to help users manage shared expenses and track payments. It allows users to register, create bills, split expenses (equally or manually), track payments, set reminders, and export overdue bills to CSV.

## Features

- **User Management**: Register and login with secure password hashing (SHA-256)
- **Bill Management**: Create and view bills with detailed information
- **Expense Splitting**:
  - Split equally among participants
  - Split manually with custom amounts
- **Payment Tracking**: Monitor pending and overdue payments
- **Reminders**: Automatic tracking of payment due dates
- **CSV Export**: Export overdue bills for record-keeping

## Technology Stack

- **Language**: Java 17
- **Database**: SQLite
- **JDBC Driver**: sqlite-jdbc-3.47.1.0

## Project Structure

```
/app/
├── src/
│   └── com/splitease/
│       ├── Main.java                    # Application entry point
│       ├── database/
│       │   └── DatabaseHelper.java      # Database operations
│       ├── managers/
│       │   ├── BillManager.java         # Bill CRUD operations
│       │   ├── ExpenseSplitter.java     # Expense splitting logic
│       │   ├── ReminderSystem.java      # Payment reminders
│       │   └── UserManager.java         # User authentication
│       ├── models/
│       │   └── Bill.java                # Bill data model
│       └── utils/
│           └── CSVExporter.java         # CSV export functionality
├── lib/
│   └── sqlite-jdbc-3.47.1.0.jar        # SQLite JDBC driver
├── bin/                                 # Compiled classes
├── splitease.db                         # SQLite database (created on first run)
└── run-splitease.sh                     # Convenient run script
```

## Installation

The application is already compiled and ready to run. Java 17 has been installed.

### To recompile (if needed):
```bash
cd /app
javac -cp "lib/*:src" -d bin src/com/splitease/**/*.java src/com/splitease/*.java
```

## Running the Application

### Option 1: Using the run script
```bash
cd /app
bash run-splitease.sh
```

### Option 2: Direct command
```bash
cd /app
java -cp "lib/*:bin" com.splitease.Main
```

## Usage Guide

### 1. Authentication

When you start the application, you'll see the authentication menu:

```
╔═══════════════════════════════════════╗
║         Authentication Menu          ║
╠═══════════════════════════════════════╣
║  1. Register                         ║
║  2. Login                            ║
║  3. Exit                             ║
╚═══════════════════════════════════════╝
```

**First-time users**: Choose option 1 to register
- Enter username
- Enter password (will be hashed with SHA-256)
- Enter email

**Returning users**: Choose option 2 to login

### 2. Main Menu

After logging in, you'll see:

```
╔═══════════════════════════════════════╗
║            Main Menu                 ║
║  Logged in as: [username]            ║
╠═══════════════════════════════════════╣
║  1. Add Bill                         ║
║  2. View All Bills                   ║
║  3. Split Expenses                   ║
║  4. Check Reminders                  ║
║  5. Export Overdue Bills (CSV)       ║
║  6. Logout                           ║
╚═══════════════════════════════════════╝
```

### 3. Adding a Bill

1. Choose option 1 from the main menu
2. Enter the bill amount (e.g., 100.00)
3. Enter a description (e.g., "Dinner at restaurant")
4. Select the payer from the list of available users
5. Enter participant usernames (comma-separated)
6. Optionally split the bill immediately

### 4. Splitting Expenses

**Equal Split**:
- Automatically divides the bill amount equally among all participants
- Handles rounding to ensure total matches bill amount
- Extra cents go to the payer

**Manual Split**:
- Assign specific amounts to each participant
- System validates that total shares match bill amount

### 5. Checking Reminders

- View all pending payments with days until due
- View all overdue payments with days overdue
- System automatically updates payment status

### 6. Exporting Overdue Bills

- Generates a CSV file with timestamp (e.g., `overdue_bills_2025-08-07_14-30-00.csv`)
- Includes: Username, Amount, Payer, Due Date, Days Overdue, Description

## Database Schema

The application uses SQLite with the following tables:

### users
- username (PRIMARY KEY)
- password_hash
- email

### bills
- id (PRIMARY KEY, AUTOINCREMENT)
- amount
- payer_username (FOREIGN KEY)
- date
- description

### bill_users
- bill_id (FOREIGN KEY)
- username (FOREIGN KEY)
- PRIMARY KEY (bill_id, username)

### bill_shares
- id (PRIMARY KEY, AUTOINCREMENT)
- bill_id (FOREIGN KEY)
- username (FOREIGN KEY)
- share_amount
- due_date
- status ('Pending' or 'Overdue')

## Example Workflow

1. **Register Users**:
   - User A registers: username=alice, email=alice@example.com
   - User B registers: username=bob, email=bob@example.com
   - User C registers: username=charlie, email=charlie@example.com

2. **Create a Bill** (Alice logs in):
   - Amount: $90.00
   - Description: "Team lunch"
   - Payer: alice
   - Participants: alice, bob, charlie

3. **Split Equally**:
   - Each person owes: $30.00
   - Due in: 7 days

4. **Check Reminders**:
   - See all pending payments
   - Track due dates

5. **Export Overdue** (after due date):
   - Generate CSV report
   - Share with participants

## Troubleshooting

### Database Issues
- Database file is created in `/app/splitease.db` on first run
- If you encounter permission issues, ensure the `/app` directory is writable

### Compilation Errors
- Ensure Java 17 is installed: `java -version`
- Verify SQLite JDBC driver is in `/app/lib/`
- Check classpath in compilation command

### Runtime Errors
- Ensure you're running from `/app` directory
- Verify classpath includes both `lib/*` and `bin`

## Security Notes

- Passwords are hashed using SHA-256 before storage
- Database uses foreign key constraints for data integrity
- Transactions ensure atomic operations

## Future Enhancements

- Payment marking as complete
- Bill editing and deletion
- Multi-currency support
- Email notifications for reminders
- Web-based interface
- Receipt upload and storage

## License

Open source application for expense management.

---

**Enjoy using SplitEase! Make splitting expenses easy and fair.**
