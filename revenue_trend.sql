SELECT 
    DATE_TRUNC('day', "timestamp") as date,
    SUM(price) as daily_revenue
FROM Orders
GROUP BY date
ORDER BY date;