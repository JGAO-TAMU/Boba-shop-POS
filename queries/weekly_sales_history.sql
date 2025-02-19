--SPECIAL QUERY #1

-- custom params:
-- \set year xxxx
-- \set week x
-- \i weekly_sales_history.sql

-- shows total sales for specified week
--default values
\if :{?year}
\else
\set year 2025
\endif

\if :{?week}
\else
\set week 1
\endif

SELECT 
    DATE_TRUNC('week', timestamp) AS week_start,
    COUNT(*) AS order_count
FROM orders
WHERE EXTRACT(ISOYEAR FROM timestamp) = :year
AND EXTRACT(WEEK FROM timestamp) = :week
GROUP BY week_start
ORDER BY week_start;
