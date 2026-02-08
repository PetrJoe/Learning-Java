#!/bin/bash

# Base URL
URL="http://localhost:8080/api"

# 1. Register User
EMAIL="fee_test$(date +%s)@example.com"
echo "Registering User ($EMAIL)..."
curl -s -X POST $URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"fee_user$(date +%s)\", \"email\": \"$EMAIL\", \"password\": \"password123\"}" > /dev/null

# 2. Login
echo "Logging in..."
TOKEN=$(curl -s -X POST $URL/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"password123\"}" | grep -o '"token":"[^\"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "Login Failed"
    exit 1
fi

echo "Token received."

# 3. Get Account IDs
ACCOUNTS_RESP=$(curl -s -X GET $URL/banking/accounts -H "Authorization: Bearer $TOKEN")

# Get USD Account ID
USD_ACC_ID=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"USD"' | head -n 1 | grep -o '"id":[0-9]*' | cut -d':' -f2)
# Get EUR Account Number
EUR_ACC_NUM=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"EUR"' | head -n 1 | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)

echo "USD Account ID: $USD_ACC_ID"
echo "EUR Account Number: $EUR_ACC_NUM"

# 4. Deposit 1000 USD
echo "Depositing 1000 USD..."
curl -s -X POST $URL/banking/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"accountId\": $USD_ACC_ID, \"amount\": 1000}" > /dev/null

# 5. Transfer 100 USD to EUR (Cross-Currency) -> Expect 1% Fee (1 USD)
echo "Transferring 100 USD to EUR..."
TRANSFER_RESP=$(curl -s -X POST $URL/banking/transfer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"sourceAccountId\": $USD_ACC_ID, \"targetAccountNumber\": \"$EUR_ACC_NUM\", \"amount\": 100, \"currency\": \"USD\", \"reference\": \"Fee Test\"}")

# 6. Verify Fee in Transaction Response
FEE=$(echo $TRANSFER_RESP | grep -o '"fee":[0-9.]*' | cut -d':' -f2)
echo "Transaction Fee Recorded: $FEE"

if [ "$FEE" == "1.00" ] || [ "$FEE" == "1" ]; then
    echo "SUCCESS: Fee is correct (1.00)"
else
    echo "FAILURE: Fee is incorrect (Expected 1.00, got $FEE)"
    echo "Response: $TRANSFER_RESP"
fi

# 7. Verify Balance
# Expected Balance: 1000 - 100 (transfer) - 1 (fee) = 899
UPDATED_ACCOUNTS=$(curl -s -X GET $URL/banking/accounts -H "Authorization: Bearer $TOKEN")
USD_BALANCE=$(echo $UPDATED_ACCOUNTS | grep -o '{"id":'$USD_ACC_ID'[^}]*}' | grep -o '"balance":[0-9.]*' | cut -d':' -f2)

echo "USD Account Balance: $USD_BALANCE"

if [ "$USD_BALANCE" == "899.00" ]; then
    echo "SUCCESS: Balance is correct (899.00)"
else
    echo "FAILURE: Balance is incorrect (Expected 899.00, got $USD_BALANCE)"
fi
