#!/bin/bash

# Base URL
URL="http://localhost:8080/api"

# 1. Register User with Name
EMAIL="john.doe.$(date +%s)@example.com"
echo "Registering User ($EMAIL)..."
curl -s -X POST $URL/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"john_doe_$(date +%s)\", \"email\": \"$EMAIL\", \"password\": \"password123\", \"firstName\": \"John\", \"lastName\": \"Doe\"}" > /dev/null

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

# 3. Get Accounts and Check Name
echo "Fetching My Accounts..."
ACCOUNTS_RESP=$(curl -s -X GET $URL/banking/accounts -H "Authorization: Bearer $TOKEN")

# Verify Name in Response
NAME_CHECK=$(echo $ACCOUNTS_RESP | grep -o "John Doe")
if [ -n "$NAME_CHECK" ]; then
    echo "SUCCESS: Account Holder Name 'John Doe' found in accounts list."
else
    echo "FAILURE: Account Holder Name not found."
    echo "Response: $ACCOUNTS_RESP"
fi

# Get an Account Number
ACC_NUM=$(echo $ACCOUNTS_RESP | grep -o '"accountNumber":"[^"]*"' | head -n 1 | cut -d'"' -f4)
echo "Testing Name Enquiry for Account: $ACC_NUM"

# 4. Perform Name Enquiry
LOOKUP_RESP=$(curl -s -X GET "$URL/banking/name-enquiry?accountNumber=$ACC_NUM" \
  -H "Authorization: Bearer $TOKEN")

echo "Lookup Response: $LOOKUP_RESP"

# Verify
LOOKUP_NAME=$(echo $LOOKUP_RESP | grep -o '"accountName":"[^"]*"' | cut -d'"' -f4)
if [ "$LOOKUP_NAME" == "John Doe" ]; then
    echo "SUCCESS: Name Enquiry returned 'John Doe'."
else
    echo "FAILURE: Name Enquiry returned '$LOOKUP_NAME' (Expected 'John Doe')."
fi
