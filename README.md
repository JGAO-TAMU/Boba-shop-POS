# Boba Shop POS System

Team Members: Jonathan Gao, Zakariya Mohamed Mobarak, Justin Nguyen, Claire Wang, Summer Wong

A comprehensive Java-based POS solution created for Sharetea-style boba tea shops. This system improves upon existing solutions by providing dual-interface functionality for both cashier operations and management oversight. The application utilizes PostgreSQL for robust data management and Java Swing for an intuitive GUI experience.

## Key Features
Multi-role Interface: Separate optimized interfaces for cashiers and managers
Order Management: Streamlined order creation with employee assignment and timestamp tracking
Inventory Control: Automated inventory updates with each transaction
Sales Analytics: Advanced reporting on sales trends by time period, ingredient usage, and product popularity
Restocking System: Low inventory alerts with integrated ordering capabilities

## Tech Stack
Frontend: Java Swing for intuitive user interfaces
Database: PostgreSQL for secure, reliable data storage
Connectivity: Java Database Connectivity (JDBC) for seamless application-database integration
Design & Deployment: Figma for UI/UX prototyping, AWS for cloud hosting

## How to Run our Code
Compile:
```` 
javac -cp "GUI/postgresql-42.2.8.jar" -d GUI/bin (Get-ChildItem -Path GUI/bobaapp -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
````

Run: 
`````
java -cp "GUI/bin;postgresql-42.2.8.jar" bobaapp.Main
`````
