-- rank drinks by popularity
SELECT 
    m.name as drink_name,
    COUNT(*) as times_ordered
FROM Drinks d
JOIN Menu m ON d.menuID = m.menuID
GROUP BY m.name
ORDER BY times_ordered DESC;