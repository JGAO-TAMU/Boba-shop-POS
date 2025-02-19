-- TO USE:
-- \i popular_modifications.sql

-- FROM TERMINAL:
-- psql -h csce-315-db.engr.tamu.edu -U team_10 -d team_10_db -f /.../popular_modifications.sql

SELECT 
    mm.name as modification,
    COUNT(*) as times_ordered
FROM Modifications m
JOIN ModificationsMenu mm ON m.modMenuID = mm.modMenuID
GROUP BY mm.name
ORDER BY times_ordered DESC;
