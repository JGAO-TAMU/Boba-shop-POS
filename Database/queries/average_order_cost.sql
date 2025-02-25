-- average order total by day of the week
SELECT 
    EXTRACT(DOW FROM "timestamp") as day_of_week,
    AVG(price) as avg_order_value
FROM Orders
GROUP BY day_of_week
ORDER BY day_of_week;

