--SPECIAL QUERY #3
-- shows top 10 days by revenue
SELECT 
    DATE_TRUNC('day', "timestamp") as sale_date,
    SUM(price) as daily_total
FROM Orders
GROUP BY sale_date
ORDER BY daily_total DESC
LIMIT 10;