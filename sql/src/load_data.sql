COPY Users
--FROM '/extra/shall016/project_2/data/users.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/users.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101;

COPY Store
--FROM '/extra/shall016/project_2/data/stores.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/stores.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Product
--FROM '/extra/shall016/project_2/data/products.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/products.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Warehouse
--FROM '/extra/shall016/project_2/data/warehouse.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/warehouse.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Orders
--FROM '/extra/shall016/project_2/data/orders.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/orders.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE orders_orderNumber_seq RESTART 501;


COPY ProductSupplyRequests
--FROM '/extra/shall016/project_2/data/productSupplyRequests.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/productSupplyRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productsupplyrequests_requestNumber_seq RESTART 11;

COPY ProductUpdates
--FROM '/extra/shall016/project_2/data/productUpdates.csv'
FROM '/extra/hdye001/CS152_Database_Project/data/productUpdates.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productupdates_updateNumber_seq RESTART 51;
