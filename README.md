# Functional Programming with Scala (retail store rule engine ualifies orders' transactions)  
### A simple Scala Rule Engine that qualifies orders’ transactions to discounts based on a set of qualifying rules & automatically calculates the proper discount based on some calculation rules.Then writes the results to an oracle database & keeps tracking the logs data.  
## Problem Statement:  
A huge retail store wants a rule engine that qualifies orders’ transactions to discounts based on a set of qualifying rules. And automatically calculates the proper discount based on some calculation rules.  
## Discount Calculations:  
- **Expiration Date Discount:**  
less than 30 days remaining for the product to expire (from the day of transaction, i.e. timestamp) 


