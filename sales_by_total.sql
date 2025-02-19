SELECT 
    CASE 
        WHEN price < 5 THEN 'Under $5'
        WHEN price >= 5 AND price < 10 THEN '$5-$10'
        WHEN price >= 10 AND price < 15 THEN '$10-$15'
        ELSE 'Over $15'
    END as price_range,
    COUNT(*) as order_count
FROM Orders
GROUP BY price_range
ORDER BY price_range;