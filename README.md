# Functional Programming with Scala (Retail Store Rule Engine Qualifies Orders' Transactions)  
### A simple Scala Rule Engine that qualifies orders’ transactions to discounts based on a set of qualifying rules & automatically calculates the proper discount based on some calculation rules.Then writes the results to an oracle database & keeps tracking the logs data.  
## Problem Statement:  
A huge retail store wants a rule engine that qualifies orders’ transactions to discounts based on a set of qualifying rules. And automatically calculates the proper discount based on some calculation rules.  
## Discount Calculations:  
- **Expiration Date Discount:**  
Qualifying Rule:  
 if the number of days is less than 30 days remaining for the product to expire (from the day of transaction, i.e. timestamp).   
Discount calculation rules:
 if 29 days remaining -> 1% discount  
 if 28 days remaining -> 2% discount   
 if 27 days remaining -> 3% discount   
etc …
- **Product Category Discount:**
  Qualifying Rule:  
   If the product category is "Cheese", a discount of 10% is applied.  
   If the product category is "Wine", a discount of 5% is applied.  
   Otherwise, no discount is applied.
- **Product Category Discount:**
  Qualifying Rule:  
    Products that are sold on 23rd of March have a special discount! (Celebrating the end of java project?)
  Discount calculation rule:     
   50% discount 
- **Quantity Discount:**  
  Qualifying Rule:  
   If the quantity of products purchased is more than 5 of the same product, a discount will be applied.    
  Discount calculation rules:   
 If the quantity is between 6 and 9, a discount of 5% is applied.  
 If the quantity of products purchased is between 10 and 14, a discount of 7% is applied.  
 If the quantity of products purchased is 15 or more, a discount of 10% is applied.  
Otherwise, no discount is applied.
 - **Purchasing Channel Discount:**  
 Qualifying Rule:   
  Sales that are made through the "App" will have a special discount. A discount of the quantity rounded up to the nearest multiple of 5 is applied. Otherwise, no discount is applied.  
 Discount calculation rules:    
 If quantity: 1, 2, 3, 4, 5 ‐> discount 5%.  
 If quantity 6, 7, 8, 9, 10 ‐> discount 10%.  
 If quantity 11, 12, 13, 14, 15 ‐> discount 15%.
 etc …
 - **Payment Method Discount:**  
   If the payment method is "Visa", a discount of 5% is applied. Otherwise, no discount is applied.

 Transactions that didn’t qualify to any discount will have 0% discount.   
 Transactions that qualified to more than one discount will get the top 2 and get their average.   
 calculate the final price and load the result in a database table of your choice.  
 It is required to log the engine’s events in a log file “rules_engine.log”.(TIMESTAMP LOGLEVEL MESSAGE )  

## Technical considerations:  
the core logic must be written in a pure functional manner.    
In the core functional logic:     
 No mutable data structures allowed.  
 No loops allowed and no Null values are used.  
 All your functions are pure.  
 All functions are documented. 

## Tools and Technologies:
 Scala Programming Language.
 oracle database.
 IntelliJ IDE.   

-**Row Data:**  
![login](https://github.com/shoroukabdelraouf/FP_with_Scala_-retail_store_rule_engine_qualifies_orders-_transactions/blob/31534dc15290339afcd22a14c7d4e966e4363ec9/screens/orders_csv.png)  
-**Output data:**  
![login](https://github.com/shoroukabdelraouf/FP_with_Scala_-retail_store_rule_engine_qualifies_orders-_transactions/blob/9c9c0334b6153de677ca0f5288ae6fd8f46db341/screens/toad%20output.png)




