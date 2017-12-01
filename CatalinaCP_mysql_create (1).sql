CREATE TABLE `Accounts` (
	`number` varchar(150) NOT NULL,
	`name` varchar(150) NOT NULL,
	`dob` DATE NOT NULL,
	`uname` varchar(150) NOT NULL UNIQUE,
	`pwHash` BINARY(64) NOT NULL,
	`dc` DATE NOT NULL,
	`email` varchar(150) NOT NULL,
	`phone` varchar(150) NOT NULL,
	`address` varchar(150) NOT NULL,
	`type` varchar(10) NOT NULL,
	PRIMARY KEY (`number`)
);

CREATE TABLE `Contracts` (
	`CID` int NOT NULL AUTO_INCREMENT,
	`numberTo` varchar(150) NOT NULL,
	`numberFrom` varchar(150) NOT NULL,
	`dc` DATE NOT NULL,
	`value` varchar(150) NOT NULL,
	`confirmed` bool NOT NULL,
	PRIMARY KEY (`CID`)
);

CREATE TABLE `Products` (
	`PID` int NOT NULL AUTO_INCREMENT,
	`dc` DATE NOT NULL,
	`description` varchar(150) NOT NULL,
	`size` varchar(5) NOT NULL,
	`rrp` varchar(150) NOT NULL,
	`owner` varchar(150) NOT NULL,
	PRIMARY KEY (`PID`)
);

CREATE TABLE `KYC` (
	`number` varchar(150) NOT NULL UNIQUE,
	`idImage` blob NOT NULL,
	`idAddress` blob NOT NULL,
	PRIMARY KEY (`number`)
);

CREATE TABLE `Keypairs` (
	`number` varchar(150) NOT NULL,
	`public` blob NOT NULL,
	`private` blob NOT NULL,
	PRIMARY KEY (`number`)
);

CREATE TABLE `ContractsJProducts` (
	`PID` int NOT NULL,
	`CID` int NOT NULL
);

ALTER TABLE `Contracts` ADD CONSTRAINT `Contracts_fk0` FOREIGN KEY (`numberTo`) REFERENCES `Accounts`(`number`);

ALTER TABLE `Contracts` ADD CONSTRAINT `Contracts_fk1` FOREIGN KEY (`numberFrom`) REFERENCES `Accounts`(`number`);

ALTER TABLE `Products` ADD CONSTRAINT `Products_fk0` FOREIGN KEY (`owner`) REFERENCES `Accounts`(`number`);

ALTER TABLE `KYC` ADD CONSTRAINT `KYC_fk0` FOREIGN KEY (`number`) REFERENCES `Accounts`(`number`);

ALTER TABLE `Keypairs` ADD CONSTRAINT `Keypairs_fk0` FOREIGN KEY (`number`) REFERENCES `Accounts`(`number`);

ALTER TABLE `ContractsJProducts` ADD CONSTRAINT `ContractsJProducts_fk0` FOREIGN KEY (`PID`) REFERENCES `Products`(`PID`);

ALTER TABLE `ContractsJProducts` ADD CONSTRAINT `ContractsJProducts_fk1` FOREIGN KEY (`CID`) REFERENCES `Contracts`(`CID`);

