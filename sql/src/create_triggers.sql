CREATE OR REPLACE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION set_order_num_and_time()
RETURNS trigger AS
$BODY$
BEGIN
    NEW.orderNumber := nextval('orderNumber_seq');
    NEW.orderTime := current_timestamp;
    RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS order_trigger ON Orders;
CREATE TRIGGER order_trigger BEFORE INSERT
ON Orders FOR EACH ROW
EXECUTE PROCEDURE set_order_num_and_time();