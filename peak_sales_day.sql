-- psql -h csce-315-db.engr.tamu.edu -U team_10 -d team_10_db -f [path to script]

SELECT 
    DATE_TRUNC('day', "timestamp") as sale_date,
    SUM(price) as daily_total
FROM Orders
GROUP BY sale_date
ORDER BY daily_total DESC
LIMIT 10;