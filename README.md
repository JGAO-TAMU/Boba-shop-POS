# project2-team10

Team Members: Jonathan Gao, Zakariya Mohamed Mobarak, Justin Nguyen, Claire Wang, Summer Wong

Using Sharetea as a reference, we want to improve the POS system they currently use. We are creating a system for a boba shop that will do POS tasks, such as taking orders and transactions, and use a database to keep track of inventory and orders. We are designing a GUI for cashiers and managers to utilize. 

The manager GUI can see order trends based on time, ingredient, drink, etc. They can also see the inventory of supplies, modification ingredients, and drink ingredients. The manager can see when inventory items need to be restocked and place orders for those items. 

The cashier side can see the orders an employee is assigned to make and take customers' orders. While an employee is clocked in, an order has a timestamp of when the order is placed. The system will update the inventory based on what has been ordered. 

## Tech Stack: 
- Technologies: Figma, AWS
- Frontend: Java Swing
- API: Java Database Connectivity
- Database/Backend: PostgreSQL, Python

## How to Run our Code
````
Compile: 
javac -cp "GUI/postgresql-42.2.8.jar" -d GUI/bin (Get-ChildItem -Path GUI/bobaapp -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })


Run: 
java -cp "GUI/bin;postgresql-42.2.8.jar" bobaapp.Main
`````