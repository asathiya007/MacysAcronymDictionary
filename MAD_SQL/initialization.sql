-- combination of both the create_db.sql and create_table.sql SQL scripts

-- Use the Macy's Acronym Dictionary database
USE macys_acro_dict;

-- Create the acronym table if it does not already exist which will consist of
-- the acronym, actual term, and a short  definition
DROP TABLE IF EXISTS acro_table;
CREATE TABLE acro_table (
    acronym varchar(20) NOT NULL,
    stands_for varchar(300) NOT NULL,
    short_def varchar(1000) NOT NULL,
    PRIMARY KEY(acronym)
);

-- Add the first acronym
INSERT INTO acro_table
VALUES ('MAD', 'Macy''s Acronym Dictionary', CONCAT('This program, which helps you ',
    'identify and understand acronyms used here at Macy''s Tech. MAD has been developed ',
    'by Akshay Sathiya during his time at Macy''s Tech in Johns Creek, GA.'));
