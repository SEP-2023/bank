INSERT INTO accounts (number, balance, user_id)
VALUES ('12345678', '10000', 1);

INSERT INTO merchants (id, username, password, merchant_id, merchant_password, account, name)
VALUES (nextval('user_seq'), 'prodavac', 'pass', 'nekiId', 'pass', 1, 'Prodavac 1');


INSERT INTO accounts (number, balance, user_id)
VALUES ('87654321', '10000', 2);

INSERT INTO clients (id, username, password, name, account)
VALUES (nextval('user_seq'), 'prodavac', 'pass', 'Klijent 1', 2);


INSERT INTO credit_cards (pan, security_code, cardholder_name, expiration_month, expiration_year, account)
VALUES ('C6B25FFAC5D2C50F41AF3AC4D97D07D95FD4F2D5F9FCEF98DC709E5741060508', '111', 'Ivana Jankovic', '07', '27', 2);

