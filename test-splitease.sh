#!/bin/bash
# Automated test script for SplitEase

echo "========================================"
echo "Testing SplitEase Application"
echo "========================================"
echo ""

# Remove old database if exists
rm -f /app/splitease.db

# Create test input file
cat > /tmp/test_input.txt << 'EOF'
1
alice
password123
alice@example.com
1
bob
password456
bob@example.com
1
charlie
password789
charlie@example.com
2
alice
password123
1
90.00
Team Lunch
alice
alice, bob, charlie
yes
1
7
2
4
6
EOF

echo "Running automated test with sample data..."
echo ""
cd /app
java -cp "lib/*:bin" com.splitease.Main < /tmp/test_input.txt

echo ""
echo "========================================"
echo "Test completed!"
echo "========================================"
echo ""
echo "Database created at: /app/splitease.db"
echo ""

# Check if database was created
if [ -f /app/splitease.db ]; then
    echo "✓ Database file created successfully"
    echo ""
    echo "Database contents:"
    echo "- Users table:"
    sqlite3 /app/splitease.db "SELECT username, email FROM users;"
    echo ""
    echo "- Bills table:"
    sqlite3 /app/splitease.db "SELECT id, amount, payer_username, description FROM bills;"
    echo ""
    echo "- Bill shares table:"
    sqlite3 /app/splitease.db "SELECT bill_id, username, share_amount, due_date, status FROM bill_shares;"
else
    echo "✗ Database file was not created"
fi
