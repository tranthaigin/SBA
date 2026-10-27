IF DB_ID('FUNewsManagementSBA301') IS NULL
    CREATE DATABASE FUNewsManagementSBA301;
GO

USE FUNewsManagementSBA301;
GO

IF OBJECT_ID('dbo.Category', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Category (
        CategoryID SMALLINT IDENTITY(1, 1) PRIMARY KEY,
        CategoryName NVARCHAR(100) NOT NULL,
        CategoryDesciption NVARCHAR(250) NOT NULL,
        ParentCategoryID SMALLINT NULL,
        IsActive BIT NULL
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Category WHERE CategoryName = N'Academic news')
    INSERT dbo.Category (CategoryName, CategoryDesciption, ParentCategoryID, IsActive)
    VALUES (N'Academic news', N'Academic announcements', NULL, 1);

IF NOT EXISTS (SELECT 1 FROM dbo.Category WHERE CategoryName = N'Student Affairs')
    INSERT dbo.Category (CategoryName, CategoryDesciption, ParentCategoryID, IsActive)
    VALUES (N'Student Affairs', N'Student activities and events', NULL, 1);

IF NOT EXISTS (SELECT 1 FROM dbo.Category WHERE CategoryName = N'Campus Safety')
    INSERT dbo.Category (CategoryName, CategoryDesciption, ParentCategoryID, IsActive)
    VALUES (N'Campus Safety', N'Campus safety information', NULL, 1);
GO

SELECT * FROM dbo.Category;
