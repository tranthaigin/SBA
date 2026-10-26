USE LibraryDB;
GO

SELECT DB_NAME() AS current_database;

SELECT TABLE_NAME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME IN ('STUDENTS', 'BOOKS', 'STUDENTS_BOOKS', 'BORROWINGS')
ORDER BY TABLE_NAME;

SELECT s.id, s.email, b.id AS book_id, b.isbn, b.title
FROM STUDENTS s
LEFT JOIN STUDENTS_BOOKS sb ON sb.student_id = s.id
LEFT JOIN BOOKS b ON b.id = sb.book_id
ORDER BY s.id, b.id;

SELECT * FROM STUDENTS_BOOKS ORDER BY student_id, book_id;
SELECT * FROM BORROWINGS ORDER BY id;

-- Verify both foreign keys and the unique/primary constraint of the join table.
SELECT fk.name AS foreign_key_name,
       OBJECT_NAME(fk.parent_object_id) AS child_table,
       COL_NAME(fkc.parent_object_id, fkc.parent_column_id) AS child_column,
       OBJECT_NAME(fk.referenced_object_id) AS parent_table
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc ON fkc.constraint_object_id = fk.object_id
WHERE OBJECT_NAME(fk.parent_object_id) = 'STUDENTS_BOOKS';

SELECT i.name, i.is_unique, COL_NAME(ic.object_id, ic.column_id) AS column_name
FROM sys.indexes i
JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
WHERE OBJECT_NAME(i.object_id) = 'STUDENTS_BOOKS'
ORDER BY i.name, ic.key_ordinal;
