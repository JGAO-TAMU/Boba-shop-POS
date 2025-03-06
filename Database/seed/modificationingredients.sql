-- Mapping of modification menu items to their ingredients with quantities

-- TOPPINGS

-- for Boba Pearls (modMenuID = 1)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (1, 19, 1);

-- for Pudding (modMenuID = 2)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (2, 17, 1);

-- for Grass Jelly (modMenuID = 3)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (3, 18, 1);

-- for Lychee Jelly (modMenuID = 4)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (4, 13, 1);

-- for Mango Bits (modMenuID = 5)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (5, 14, 1);

-- for Strawberry Bits (modMenuID = 6)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (6, 15, 1);

-- for Peach Bits (modMenuID = 7)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed) 
VALUES (7, 16, 1);

-- SUGAR LEVELS

-- for Sugar - 100% (modMenuID = 8)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (8, 21, 4);

-- for 75% Sugar (modMenuID = 9)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (9, 21, 3);

-- for 50% Sugar (modMenuID = 10)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (10, 21, 2);

-- for 25% Sugar (modMenuID = 11)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (11, 21, 1);

-- for 0% Sugar (modMenuID = 12)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (12, 21, 0);

-- ICE LEVELS

-- for Ice - Normal (modMenuID = 13)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (13, 20, 4);

-- for Extra Ice (modMenuID = 14)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (14, 20, 6);

-- for Less Ice (modMenuID = 15)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (15, 20, 2);

-- for No Ice (modMenuID = 16)
INSERT INTO ModIngredients (modMenuID, ingredientID, quantityUsed)
VALUES (16, 20, 0);