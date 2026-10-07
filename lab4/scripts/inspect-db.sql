SELECT DB_NAME() AS database_name, @@VERSION AS server_version;
SELECT t.name AS table_name, c.name AS column_name, ty.name AS data_type,
       c.max_length, c.is_nullable, c.is_identity
FROM sys.tables t JOIN sys.columns c ON c.object_id=t.object_id
JOIN sys.types ty ON c.user_type_id=ty.user_type_id
WHERE t.name IN ('orchids','orchid_categories') ORDER BY t.name,c.column_id;
SELECT OBJECT_NAME(kc.parent_object_id) AS table_name, kc.name AS primary_key,
       c.name AS key_column
FROM sys.key_constraints kc
JOIN sys.index_columns ic ON ic.object_id=kc.parent_object_id AND ic.index_id=kc.unique_index_id
JOIN sys.columns c ON c.object_id=ic.object_id AND c.column_id=ic.column_id
WHERE kc.type='PK' AND OBJECT_NAME(kc.parent_object_id) IN ('orchids','orchid_categories');
SELECT fk.name, OBJECT_NAME(fk.parent_object_id) AS owning_table,
       pc.name AS fk_column, OBJECT_NAME(fk.referenced_object_id) AS referenced_table,
       rc.name AS referenced_column
FROM sys.foreign_keys fk JOIN sys.foreign_key_columns fkc ON fk.object_id=fkc.constraint_object_id
JOIN sys.columns pc ON pc.object_id=fkc.parent_object_id AND pc.column_id=fkc.parent_column_id
JOIN sys.columns rc ON rc.object_id=fkc.referenced_object_id AND rc.column_id=fkc.referenced_column_id
WHERE OBJECT_NAME(fk.parent_object_id)='orchids';
SELECT category_id, category_name FROM orchid_categories ORDER BY category_id;
SELECT orchidid, orchid_name, category_id, is_natural, is_attractive FROM orchids ORDER BY orchidid;
