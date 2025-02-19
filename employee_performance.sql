SELECT 
    e.name,
    COUNT(*) as orders_processed,
    SUM(o.price) as total_sales
FROM Orders o
JOIN Employee e ON o.employeeID = e.employeeID
GROUP BY e.name;