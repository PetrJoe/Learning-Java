#!/bin/bash

# Base URL
URL="http://localhost:8080/api"

# 1. Register User
EMAIL="test$(date +%s)@example.com"
echo "Registering User ($EMAIL)..."
curl -s -X POST $URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"user$(date +%s)\", \"email\": \"$EMAIL\", \"password\": \"password123\"}"
echo -e "\n""\n\n"

# 2. Login
echo "Logging in..."
TOKEN=$(curl -s -X POST $URL/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"password123\"}" | grep -o '"token":"[^\"]*' | cut -d'"' -f4)

echo "Token received: $TOKEN"
echo -e "\n"

# 3. List Auto-Created Accounts
echo "Listing Auto-Created Accounts..."
ACCOUNTS_RESP=$(curl -s -X GET $URL/banking/accounts -H "Authorization: Bearer $TOKEN")
echo $ACCOUNTS_RESP
echo -e "\n"

# Count accounts (Should be 7)
COUNT=$(echo $ACCOUNTS_RESP | grep -o "id" | wc -l)
echo "Total Accounts Found: $COUNT (Expected 7)"

# Get USD Account ID for Deposit
ACC_ID=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"USD"' | head -n 1 | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "Using USD Account ID: $ACC_ID"

# Get EUR Account details for Transfer
EUR_ACC_DATA=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"EUR"')
EUR_ACC_ID=$(echo $EUR_ACC_DATA | grep -o '"id":[0-9]*' | cut -d':' -f2)
EUR_ACC_NUM=$(echo $EUR_ACC_DATA | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)

echo "Using EUR Account ID: $EUR_ACC_ID, Number: $EUR_ACC_NUM"
echo -e "\n"

# 4. Deposit Funds
echo "Depositing 1000 USD..."
curl -s -X POST $URL/banking/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"accountId\": $ACC_ID, \"amount\": 1000}"
echo -e "\n"

# Get EUR Account ID/Number
ACCOUNTS_RESP=$(curl -s -X GET $URL/banking/accounts -H "Authorization: Bearer $TOKEN")
EUR_ACC_ID=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"EUR"' | head -n 1 | grep -o '"id":[0-9]*' | cut -d':' -f2)
EUR_ACC_NUM=$(echo $ACCOUNTS_RESP | grep -o '{"id":[0-9]*,"accountNumber":"[0-9]*","iban":"[^"]*","currency":"EUR"' | head -n 1 | grep -o '"accountNumber":"[^"]*"' | cut -d'"' -f4)

echo "EUR Account: ID=$EUR_ACC_ID, Number=$EUR_ACC_NUM"

# 7. Simulate Transfer (USD to EUR with FX)
echo "Attempting Transfer (100 USD to EUR Account $EUR_ACC_NUM)..."
curl -s -X POST $URL/banking/transfer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"sourceAccountId\": $ACC_ID, \"targetAccountNumber\": \"$EUR_ACC_NUM\", \"amount\": 100, \"currency\": \"USD\", \"reference\": \"Cross Currency Transfer\"}"
echo -e "\n"

# 8. Check Transactions
echo "Checking Transactions for USD Account..."
curl -s -X GET $URL/banking/accounts/$ACC_ID/transactions \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "Checking Balance for EUR Account..."
curl -s -X GET $URL/banking/accounts \
  -H "Authorization: Bearer $TOKEN" | grep -o '{"id":'$EUR_ACC_ID'[^}]*}'
echo -e "\n"
