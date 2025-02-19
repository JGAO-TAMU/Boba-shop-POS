SELECT 
    m.name AS menu_item,
    i.name AS ingredient_name
FROM 
    menu m
LEFT JOIN 
    drinkingredients di ON m.menuid = di.menuid
LEFT JOIN 
    inventory i ON di.ingredientID = i.ingredientID
ORDER BY 
    m.menuid, i.name;