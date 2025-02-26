--shows revenue from modifications
SELECT 
    mm.name,
    COUNT(*) * mm.price as total_revenue
FROM Modifications m
JOIN ModificationsMenu mm ON m.modMenuID = mm.modMenuID
GROUP BY mm.name, mm.price
ORDER BY total_revenue DESC;