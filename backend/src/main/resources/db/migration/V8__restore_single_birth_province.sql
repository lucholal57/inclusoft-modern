UPDATE students
SET birth_place = CASE LOWER(TRIM(birth_place))
    WHEN 'rio grande' THEN 'Tierra del Fuego'
    WHEN 'río grande' THEN 'Tierra del Fuego'
    WHEN 'san miguel de tucuman' THEN 'Tucumán'
    WHEN 'san miguel de tucumán' THEN 'Tucumán'
    ELSE birth_place
END;

ALTER TABLE students DROP COLUMN birth_province;