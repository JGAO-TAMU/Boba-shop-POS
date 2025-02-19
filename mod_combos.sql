SELECT 
    m1.name as mod1,
    m2.name as mod2,
    COUNT(*) as combination_count
FROM Modifications mod1
JOIN Modifications mod2 ON mod1.drinkID = mod2.drinkID AND mod1.modMenuID < mod2.modMenuID
JOIN ModificationsMenu m1 ON mod1.modMenuID = m1.modMenuID
JOIN ModificationsMenu m2 ON mod2.modMenuID = m2.modMenuID
GROUP BY m1.name, m2.name
ORDER BY combination_count DESC
LIMIT 10;