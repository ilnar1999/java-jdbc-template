INSERT INTO users VALUES (11, 'address1', 'VASYA1', 'VASYALogin1', 'VASYAPass1');
INSERT INTO users VALUES (12, 'address2', 'VASYA2', 'VASYALogin2', 'VASYAPass2');
INSERT INTO users VALUES (13, 'address3', 'VASYA3', 'VASYALogin3', 'VASYAPass3');

INSERT INTO items VALUES (11, 1,true,'description1','2004-12-31', 50,'2004-12-31','title1',11);
INSERT INTO items VALUES (12, 2,false,'description2','2004-12-31', 100,'2004-12-31','title2',11);
INSERT INTO items VALUES (13, 3,false,'description3','2004-12-31', 120,'2004-12-31','title3',12);

INSERT INTO bids VALUES (11, '2004-12-31', 10, 11, 13);
INSERT INTO bids VALUES (12, '2004-12-31', 20, 11, 12);
INSERT INTO bids VALUES (13, '2004-12-31', 30, 12, 13);