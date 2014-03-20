--Question 2)
--Setup User Table (we had to change the table name because
-- User is a reserved keyword)
CREATE TABLE AuctionUser(
	user_id INTEGER PRIMARY KEY NOT NULL,
	email VARCHAR(128) NOT NULL UNIQUE, -- an email is associated only with a single user
	password VARCHAR(128) NOT NULL
);

--Setup Seller Table
CREATE TABLE Seller(
	seller_id INTEGER PRIMARY KEY NOT NULL,
	phone_number VARCHAR(128) NOT NULL,
	FOREIGN KEY (seller_id) REFERENCES AuctionUser(user_id)
);

--Setup Buyer Table
CREATE TABLE Buyer(
	buyer_id INTEGER PRIMARY KEY NOT NULL,
	home_address VARCHAR(128) NOT NULL,
	FOREIGN KEY (buyer_id) REFERENCES AuctionUser(user_id)
);

--Setup Category Table
CREATE TABLE Category(
	cat_id INTEGER PRIMARY KEY NOT NULL,
	cat_name VARCHAR(128) NOT NULL
);

--Setup Tag Table
CREATE TABLE Tag(
	tag_id INTEGER PRIMARY KEY NOT NULL,
	tag_name VARCHAR(128) NOT NULL
);

--Setup Item Table
CREATE TABLE Item(
	item_id INTEGER PRIMARY KEY NOT NULL,
	seller_id INTEGER REFERENCES Seller(seller_id),
	cat_id INTEGER REFERENCES Category(cat_id),
	title VARCHAR(128) NOT NULL,
	description TEXT NOT NULL,
	price DOUBLE PRECISION NOT NULL, -- price is in BTC units
	status VARCHAR(128) NOT NULL,
	start_time TIMESTAMP NOT NULL,
	end_time TIMESTAMP NOT NULL
);

--Setup ItemTags table
CREATE TABLE ItemTags(
	item_id INTEGER NOT NULL REFERENCES Item(item_id) ON DELETE CASCADE ON UPDATE CASCADE,
	tag_id INTEGER NOT NULL REFERENCES Tag(tag_id),
	PRIMARY KEY (item_id, tag_id)
);

--Setup Bitcoin_address Table
CREATE TABLE BitcoinAddress(
	user_id INTEGER NOT NULL REFERENCES AuctionUser(user_id),
	address VARCHAR(128) PRIMARY KEY NOT NULL,
	description VARCHAR(128) NOT NULL
);

--Setup Review Table
CREATE TABLE Review(
	review_id INTEGER PRIMARY KEY NOT NULL,
	seller_id INTEGER NOT NULL REFERENCES Seller(seller_id),
	buyer_id INTEGER NOT NULL REFERENCES Buyer(buyer_id),
	score INTEGER NOT NULL,
	content VARCHAR(256) NOT NULL
);

--Setup Bid Table
CREATE TABLE Bid(
	bid_id INTEGER PRIMARY KEY NOT NULL,
	item_id INTEGER NOT NULL REFERENCES Item(item_id) ON DELETE CASCADE ON UPDATE CASCADE,
	buyer_id INTEGER NOT NULL REFERENCES Buyer(buyer_id),
	amount VARCHAR(128) NOT NULL,
	time TIMESTAMP NOT NULL
);

--Setup Transaction Table
CREATE TABLE Transaction(
	transaction_id INTEGER PRIMARY KEY NOT NULL,
	seller_id INTEGER NOT NULL REFERENCES AuctionUser(user_id),
	buyer_id INTEGER NOT NULL REFERENCES AuctionUser(user_id),
	item_id INTEGER NOT NULL REFERENCES Item(item_id) ON DELETE CASCADE ON UPDATE CASCADE,
	time TIMESTAMP NOT NULL,
	amount DOUBLE PRECISION NOT NULL
);

--Question 3)
--Populate the DB with dummy data coming from www.fakenamegenerator.com
INSERT INTO AuctionUser (user_id, email, password) VALUES
	(1, 'SaeedAnasSrour@jourrapide.com', 'tahNgoo3sh'),
	(2, 'KeawanGula@jourrapide.com', 'LiChua0tiu'),
	(3, 'SofiaRochaCorreia@teleworm.us', 'ge4Yoobe'),
	(4, 'ClaraGoncalvesRibeiro@jourrapide.com', 'Quoh7nookae0'),
	(5, 'IsaacFrolov@dayrep.com', 'Sheuy8ieng');

--Question 4)
--Populate the DB with extra dummy data from www.fakenamegenerator.com and www.ebay.com
INSERT INTO AuctionUser (user_id, email, password) VALUES
	(6, 'LucasSeleznyov@dayrep.com', 'oweegh3E'),
	(7, 'LothoGaukrogers@dayrep.com', 'deMie5shu9e'),
	(8, 'RoburGoldworthy@jourrapide.com', 'aanguF9eabae'),
	(9, 'FaiCheng@rhyta.com', 'fieXeinah7'),
	(10, 'LokKang@teleworm.us', 'Eiseiw3ae');

INSERT INTO Seller (seller_id, phone_number) VALUES
	(1,'514-123-4567'),
	(3,'450-345-6789'),
	(6,'514-880-1028'),
	(7,'613-296-3852'),
	(10,'08273 74 17 64');

INSERT INTO Buyer (buyer_id, home_address) VALUES
	(2, '2954 Sheppard Ave Toronto, ON M1S 1T4'),
	(4, '1609 Sixth Street New Westminster, BC V3L 3C1'),
	(5, '2464 Heritage Drive Calgary, AB T2V 2W2'),
	(8, '533 Heavner Court Manhattan, NY 10016 '),
	(9, '1336 Woodridge Lane Memphis, TN 38116');

INSERT INTO Category (cat_id, cat_name) VALUES
	(1, 'Motors'),
	(2, 'Fashion'),
	(3, 'Electronics'),
	(4, 'Collectibles'),
	(5, 'Home'),
	(6, 'Sporting Goods'),
	(7, 'Toys'),
	(8, 'Gifts');

INSERT INTO Tag (tag_id, tag_name) VALUES
	(1, 'Brand new'),
	(2, 'Used'),
	(3, 'Giveaway'),
	(4, 'Best Offer'),
	(5, 'Broken');

INSERT INTO Item (item_id, seller_id, cat_id, title, description, price, status, start_time, end_time) VALUES
	(1, 1, 1, 'Suzuki GSX-R 2004', 'THE BIKE LOOKS NEW, ALWAYS GARAGE KEPT, NEVER DOWN, UPGRADED EXHAUST, IDLES SMOOTH RUNS FAST!!!!! 5GREAT BIKE DON"T MISS OUT !!! FOR QUESTIONS ON THE BIKE EMAIL ME!!!!', 3, 'available', '2013-10-05 02:33:22', '2013-11-05 10:10:10'),
	(2, 3, 3, 'Oculus Rift development kit', 'Oculus Rift Development Kit in excellent condition!  Works perfectly, used less than two hours.  Includes all of the following: Oculus Rift visor with video converter console, Carrying case, Six (6) eye lenses, Power plug, HDMI to DVI adapter, HDMI to HDMI cable, USB cable, Three (3) additional power plugs for non-USA locations, Lens cleaning cloth, Printed instructions. Sorry, no international shipping. ', 0.5, 'available', '2013-10-3 06:06:59', '2013-10-13 12:34:56'),
	(3, 6, 8, 'Amazon gift card', 'Selling unused amazon gift card with a value of 10000$. Contact me for more info.', 10, 'available', '2014-01-01 01:01:01', '2014-01-10 02:02:02'),
	(4, 7, 6, 'Hockey stick', 'Brand new hockey stick which was used by the legendary Gretzky.', 10, 'available', '2014-02-01 02:30:31', '2014-03-01 03:30:33'),
	(5, 10, 2, 'Armani Jeans 32', 'Very used, torn from everywhere. BUT was worn by the legendary Omeokachie Kambinachi.', 2, 'available', '2014-03-01 04:11:23', '2014-03-03 05:22:33'),
	(6, 10, 2, 'Armani Jeans 42', 'In sale until 2040!!!!', 3, 'available', '2014-03-01 04:11:23', '2040-03-03 05:22:33');

INSERT INTO ItemTags (item_id, tag_id) VALUES
	(1, 1),
	(1, 4),
	(2, 2),
	(2, 4),
	(3, 1),
	(4, 1),
	(5, 2),
	(5, 5);

INSERT INTO BitcoinAddress (address, user_id, description) VALUES
	('1FC1BGu7fJXW7hZxQFqEDUwEEtwegnDzGS', 1, 'primary address'),
	('1JLMKRdweJBagAA6o3wBR9P9BZWWmz3jdD', 2, 'my address'),
	('1EnGVxDv9noaigTCtqZWjomhJ3ji3jEpWT', 3, 'cool address'),
	('1FVCaRTKQtpxeE4gypz69NvDkyZUd7Y3SJ', 4, 'stolen'),
	('16S5kyMHqNdZBnJKDtvpBt5BqpGEB2MQ5K', 5, 'description'),
	('17UyJyrnrBmVZexXzvnwUrsdiuLhtMyUrx', 6, 'my description'),
	('1GWUn6Mzts9FpNd3JkxqUB3oNHDmkm6fvz', 9, '1234');

INSERT INTO Review (review_id, seller_id, buyer_id, score, content) VALUES
	(1, 1, 2, 8, 'satisfying seller, a+++++'),
	(2, 3, 4, 9, 'oustanding, would buy again!!!!!'),
	(3, 6, 5, 4, 'very poor, item was broken'),
	(4, 7, 8, 1, 'disappointing'),
	(5, 10, 9, 6, 'passable');

INSERT INTO Bid (bid_id, item_id, buyer_id, amount, time) VALUES
	(1, 1, 2, 0.75 ,'2014-02-01 03:35:15'),
	(2, 2, 4, 0.2,'2013-03-07 04:30:23'),
	(3, 3, 5, 3 ,'2011-04-01 05:35:15'),
	(4, 4, 8, 4.75 ,'2010-02-09 03:47:17'),
	(5, 5, 9, 1 ,'2014-03-01 02:55:19');

INSERT INTO Transaction (transaction_id, seller_id, buyer_id, item_id, time, amount) VALUES
	(1, 1, 2, 1, '2012-04-01 07:35:13', 2),
	(2, 3, 4, 2, '2013-05-21 06:39:11', 0.5),
	(3, 6, 5, 3, '2010-06-09 06:24:17', 7),
	(4, 7, 8, 4, '2009-08-25 03:39:11', 10),
	(5, 10, 9, 5, '2014-02-01 02:46:14', 2);

--Question 5)
-- Select currently available item listings: the name, description, category name, price, seller's contact info, start and end times
SELECT title, description, cat_name, price, phone_number AS contact, status, start_time, end_time 
FROM Item I, Category C, Seller S 
WHERE I.cat_id = C.cat_id 
	AND S.seller_id = I.seller_id 
	AND I.status = 'available'
	AND I.end_time > NOW()
ORDER BY I.item_id ASC;

-- Select all reviews with the reviewer's contact email, given score and review content for the seller 1
SELECT A.email, R.score, R.content 
FROM Review R
LEFT JOIN AuctionUser A ON R.buyer_id = A.user_id
WHERE R.seller_id = 1
ORDER BY R.review_id ASC;

-- Select all items corresponding to a given tag
SELECT I.title, I.description, C.cat_name, I.price
FROM Item I, Tag T, ItemTags IT, Category C
WHERE I.item_id = IT.item_id
	AND T.tag_id = IT.tag_id
	AND T.tag_name = 'Brand new'
	AND I.cat_id = C.cat_id
ORDER BY I.item_id ASC;

-- Select all transactions with amount larger than average
SELECT T.transaction_id
FROM Transaction T
WHERE T.amount > (SELECT AVG(amount) FROM Transaction);

-- Select sellers with an average rating score of 5 or more
SELECT A.email, phone_number
FROM Seller S, AuctionUser A, Review R
WHERE S.seller_id = A.user_id
	AND S.seller_id = R.seller_id
GROUP BY S.seller_id, A.email
HAVING avg(score) >= 5;

--Question 6)
--Add a buyer into the seller group
INSERT INTO Seller (seller_id, phone_number) 
	SELECT buyer_id, '514-495-1025' AS phone_number FROM Buyer WHERE buyer_id = 9;


--Change review scores to be in percentages instead of x/10
UPDATE Review SET score = score * 10;

--Delete all the items that have already been sold
DELETE FROM Item WHERE end_time < NOW();

--Remove Category table and bring the category name directly into the Item table using a Transaction
BEGIN;
ALTER TABLE Item
	ADD COLUMN cat_name VARCHAR(128);
UPDATE Item I
	SET cat_name = ic.cat_name
	FROM (SELECT I.item_id AS item_id, C.cat_name AS cat_name
		FROM Item I, Category C
		WHERE I.cat_id = C.cat_id) AS ic
	WHERE ic.item_id = I.item_id;
ALTER TABLE Item
	DROP COLUMN cat_id,
	ALTER COLUMN cat_name SET NOT NULL;
DROP TABLE Category;
COMMIT;

--Question 7)
--Create a view that merges all the data from AuctionUser, Seller and Buyer
CREATE VIEW AllUsers
	AS SELECT U.user_id, U.email, U.password, S.phone_number, B.home_address
		FROM AuctionUser U
			LEFT JOIN Seller S ON U.user_id = S.seller_id
			LEFT JOIN Buyer B ON U.user_id = B.buyer_id;

--Create a view that represents all Seller's average review scores
CREATE VIEW AverageReview
	AS SELECT U.email, AVG(R.score)
		FROM Review R, AllUsers U
		WHERE R.seller_id = U.user_id
		GROUP BY U.email;

--Question 8)
-- Add a CHECK constraint on table AuctionUser which enforces passwords of length 8 or more
ALTER TABLE AuctionUser ADD CONSTRAINT passwdchk CHECK (char_length(password) >= 8);
-- An insert that violates the constraints
INSERT INTO AuctionUser (user_id, email, password) VALUES (11, 'johnsmith@example.com', 'fail');

-- Add a CHECK constraint on table BitcoinAddress to validate the bitcoin address
ALTER TABLE BitcoinAddress ADD CONSTRAINT addrchk CHECK (char_length(address) >= 27 AND substring(address from '^[13][a-zA-Z0-9]{26,33}$') = address);
-- An insert that violates the constraints
INSERT INTO BitcoinAddress (address, user_id, description) VALUES ('1FC1BGu7', 1, 'fake addr');