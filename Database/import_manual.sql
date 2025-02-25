
TRUNCATE TABLE orders CASCADE;
\COPY Orders FROM 'orders.csv' WITH (FORMAT csv, HEADER true);
\COPY Drinks FROM 'drinks.csv' WITH (FORMAT csv, HEADER true);
\COPY Modifications FROM 'modifications.csv' WITH (FORMAT csv, HEADER true);
