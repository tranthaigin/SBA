USE [OrchidManagementDB];
GO

IF OBJECT_ID(N'dbo.AppUser', N'U') IS NULL
BEGIN
    PRINT N'dbo.AppUser does not exist yet. Start the Spring Boot application once so Hibernate can create it.';
END
ELSE
BEGIN
    SELECT
        c.name AS column_name,
        TYPE_NAME(c.user_type_id) AS data_type,
        c.max_length,
        c.is_nullable
    FROM sys.columns AS c
    WHERE c.object_id = OBJECT_ID(N'dbo.AppUser')
    ORDER BY c.column_id;

    SELECT TOP (100)
        id,
        email,
        password,
        role,
        enabled
    FROM dbo.AppUser
    ORDER BY id DESC;
END
GO
