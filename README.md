# Functional Programming with Scala (retail store rule engine ualifies orders' transactions)  
### A simple Scala Rule Engine that qualifies orders’ transactions to discounts based on a set of qualifying rules & automatically calculates the proper discount based on some calculation rules.Then writes the results to an oracle database & keeps tracking the logs data.  
## Problem Statement:  
A huge retail store wants a rule engine that qualifies orders’ transactions to discounts based on a set of qualifying rules. And automatically calculates the proper discount based on some calculation rules.  
## Discount Calculations:  
- **Expiration Date Discount:**  
Qualifying Rule:  
 if the number of days is less than 30 days remaining for the product to expire (from the day of transaction, i.e. timestamp).   
Discount calculation rules:     
if 29 days remaining -> 1% discount  
if 28 days remaining -> 2% discount   
if 27 days remaining -> 3% discount   
etc …
- **Product Category Discount:**
  Qualifying Rule:  
  If the product category is "Cheese", a discount of 10% is applied.  
  If the product category is "Wine", a discount of 5% is applied.  
   Otherwise, no discount is applied.
- **Product Category Discount:**
  Qualifying Rule:  
    Products that are sold on 23rd of March have a special discount! (Celebrating the end of java project?)
  Discount calculation rule:     
    50% discount 
- **Quantity Discount:**
  Qualifying Rule:  
   If the quantity of products purchased is more than 5 of the same product, a discount will be applied.
  Discount calculation rules: 
If the quantity is between 6 and 9, a discount of 5% is applied.  
If the quantity of products purchased is between 10 and 14, a discount of 7% is applied.  
If the quantity of products purchased is 15 or more, a discount of 10% is applied.  
Otherwise, no discount is applied.  
    


