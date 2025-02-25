--SPECIAL QUERY #1

-- shows total sales for all weeks

SELECT 
    DATE_TRUNC('week', timestamp) AS week_start,
    COUNT(*) AS order_count
FROM orders
GROUP BY week_start
ORDER BY week_start;
