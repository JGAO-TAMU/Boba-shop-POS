\i ./seed/CreateTables.sql
\i ./seed/John_Doe.sql
\i ./seed/inventory.sql
\i ./seed/menu.sql
\i ./seed/modificationsmenu.sql
\i ./seed/modificationingredients.sql
\i ./seed/drinkingredients.sql
\copy Orders("timestamp", price, employeeID) FROM './orders.csv' WITH CSV HEADER;
