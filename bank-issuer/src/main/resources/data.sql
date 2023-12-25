INSERT INTO accounts (number, balance, user_id)
VALUES ('12345678', '10000', 1);

INSERT INTO merchants (id, username, password, merchant_id, merchant_password, account, name)
VALUES (nextval('user_seq'), 'prodavac2', 'pass', 'nekiId2', 'pass', 1, 'Prodavac 1');


INSERT INTO accounts (number, balance, user_id)
VALUES ('87654321', '1000', 2);

INSERT INTO clients (id, username, password, name, account)
VALUES (nextval('user_seq'), 'klijent', 'pass', 'Klijent 1', 2);

INSERT INTO credit_cards (pan, security_code, cardholder_name, expiration_month, expiration_year, account)
VALUES ('2222333344445555', '111', 'Ivana Jankovic', '07', '27', 2);

