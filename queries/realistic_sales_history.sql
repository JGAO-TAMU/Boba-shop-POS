-- FROM TERMINAL:
-- psql -h csce-315-db.engr.tamu.edu -U team_10 -d team_10_db -v hour=xx -f [path to realistic sales]

--show order count and total revenue by hour
\if :{?hour}
\else
\set hour 12  -- Default to noon if not specified
\endif

SELECT 
    EXTRACT(HOUR FROM timestamp) AS target_hour,
    COUNT(*) AS order_count,
    SUM(price) AS total_sales
FROM orders
WHERE EXTRACT(HOUR FROM timestamp) = :hour
GROUP BY target_hour;