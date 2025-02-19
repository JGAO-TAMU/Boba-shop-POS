SELECT 
    i.name as ingredient,
    SUM(di.quantityUsed) as total_usage
FROM DrinkIngredients di
JOIN Inventory i ON di.ingredientID = i.ingredientID
GROUP BY i.name
ORDER BY total_usage DESC;