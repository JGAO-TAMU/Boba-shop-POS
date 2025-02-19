-- psql -h csce-315-db.engr.tamu.edu -U team_10 -d team_10_db -f [path to script]
-- shows number of ingredients for each menu item
SELECT 
    m.name as menu_item,
    COUNT(di.ingredientID) as ingredient_count
FROM Menu m
LEFT JOIN DrinkIngredients di ON m.menuID = di.menuID
GROUP BY m.menuID, m.name
ORDER BY m.menuID