-- Create the acronym table if it does not already exist which will consist of 
-- the acronym, actual term, and a short  definition 
DROP TABLE IF EXISTS acro_table;
CREATE TABLE acro_table (
    acronym varchar(20),
    stands_for varchar(300),
    short_def varchar(1000),
    PRIMARY KEY(acronym)
); 

-- Add the first acronym
INSERT INTO acro_table
VALUES ('MAD', 'Macy''s Acronym Dictionary', 'This program, which helps you '
    + 'identify and understand acronyms used here at Macy''s Tech.');
    




