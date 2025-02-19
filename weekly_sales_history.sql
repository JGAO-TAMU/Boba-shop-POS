-- TO USE:
-- \set year xxxx
-- \set week x
-- \i weekly_sales_history.sql

-- FROM TERMINAL:
-- psql -h csce-315-db.engr.tamu.edu -U team_10 -d team_10_db -v year=2024 -v week=50 -f [path to weekly sales]

--default values
\if :{?year}
\else
\set year 2025  -- Default only if not provided
\endif

\if :{?week}
\else
\set week 1     -- Default only if not provided
\endif

SELECT 
    DATE_TRUNC('week', timestamp) AS week_start,
    COUNT(*) AS order_count
FROM orders
WHERE EXTRACT(ISOYEAR FROM timestamp) = :year
AND EXTRACT(WEEK FROM timestamp) = :week
GROUP BY week_start
ORDER BY week_start;
