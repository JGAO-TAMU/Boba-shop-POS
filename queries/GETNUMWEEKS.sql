SELECT COUNT(DISTINCT DATE_TRUNC('week', timestamp)) AS total_weeks
FROM orders;