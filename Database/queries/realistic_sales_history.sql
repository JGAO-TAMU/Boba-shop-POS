--SPECIAL QUERY #2
--show order count and total revenue by hour over all the weeks

SELECT 
    EXTRACT(HOUR FROM timestamp) AS target_hour,
    COUNT(*) AS order_count,
    SUM(price) AS total_sales
FROM orders
GROUP BY target_hour
ORDER BY target_hour;
