-- list inventory items with stock < 1000
SELECT name, quantity
FROM Inventory
WHERE quantity < 1000
ORDER BY quantity;