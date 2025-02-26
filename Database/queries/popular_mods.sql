-- shows popular modification choices
SELECT 
    mm.name as modification,
    COUNT(*) as times_ordered
FROM Modifications m
JOIN ModificationsMenu mm ON m.modMenuID = mm.modMenuID
GROUP BY mm.name
ORDER BY times_ordered DESC;
