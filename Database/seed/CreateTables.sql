BEGIN;

DROP TABLE IF EXISTS ModIngredients CASCADE;
DROP TABLE IF EXISTS DrinkIngredients CASCADE;
DROP TABLE IF EXISTS Inventory CASCADE;
DROP TABLE IF EXISTS Modifications CASCADE;
DROP TABLE IF EXISTS ModificationsMenu CASCADE;
DROP TABLE IF EXISTS Drinks CASCADE;
DROP TABLE IF EXISTS Menu CASCADE;
DROP TABLE IF EXISTS Orders CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;

CREATE TABLE Employee (
    employeeID   SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    accessLevel  INT NOT NULL,
    clockIn      TIMESTAMP,  
    clockOut     TIMESTAMP
);

CREATE TABLE Orders (
    orderID     SERIAL PRIMARY KEY,
    "timestamp" TIMESTAMP NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    employeeID  INT NOT NULL,
    CONSTRAINT fk_orders_employee
        FOREIGN KEY (employeeID)
        REFERENCES Employee(employeeID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE Menu (
    menuID SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    basePrice DOUBLE PRECISION NOT NULL
);

CREATE TABLE Drinks (
    drinkID    SERIAL PRIMARY KEY,
    orderID    INT NOT NULL,
    menuID     INT NOT NULL,
    CONSTRAINT fk_drinks_orders
        FOREIGN KEY (orderID)
        REFERENCES Orders(orderID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_drinks_menu
        FOREIGN KEY (menuID)
        REFERENCES Menu(menuID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE ModificationsMenu (
    modMenuID  SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    price      DOUBLE PRECISION NOT NULL
);

CREATE TABLE Modifications (
    modID     SERIAL PRIMARY KEY,
    drinkID   INT NOT NULL,
    modMenuID INT NOT NULL,
    CONSTRAINT fk_mods_drinks
        FOREIGN KEY (drinkID)
        REFERENCES Drinks(drinkID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_mods_menu
        FOREIGN KEY (modMenuID)
        REFERENCES ModificationsMenu(modMenuID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Inventory (
    ingredientID  SERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    quantity      INT NOT NULL
);

CREATE TABLE DrinkIngredients (
    menuID       INT NOT NULL,
    ingredientID INT NOT NULL,
    quantityUsed INT NOT NULL DEFAULT 1,
    PRIMARY KEY (menuID, ingredientID),
    FOREIGN KEY (menuID) REFERENCES Menu(menuID) ON DELETE CASCADE,
    FOREIGN KEY (ingredientID) REFERENCES Inventory(ingredientID) ON DELETE CASCADE
);

CREATE TABLE ModIngredients (
    modMenuID    INT NOT NULL,
    ingredientID INT NOT NULL,
    quantityUsed INT NOT NULL DEFAULT 1,
    PRIMARY KEY (modMenuID, ingredientID),
    FOREIGN KEY (modMenuID) REFERENCES ModificationsMenu(modMenuID) ON DELETE CASCADE,
    FOREIGN KEY (ingredientID) REFERENCES Inventory(ingredientID) ON DELETE CASCADE
);

COMMIT;

