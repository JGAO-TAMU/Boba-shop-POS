-- show busiest hours 
SELECT 
    EXTRACT(HOUR FROM "timestamp") as hour,
    COUNT(*) as order_count
FROM Orders
GROUP BY hour
ORDER BY order_count DESC;