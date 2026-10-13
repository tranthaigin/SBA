IF DB_ID('FUNewsManagementSBA301') IS NULL
    CREATE DATABASE FUNewsManagementSBA301;
GO
USE FUNewsManagementSBA301;
GO

IF OBJECT_ID('dbo.Category', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Category (
        CategoryID SMALLINT IDENTITY(1,1) PRIMARY KEY,
        CategoryName NVARCHAR(100) NOT NULL,
        CategoryDesciption NVARCHAR(250) NOT NULL,
        ParentCategoryID SMALLINT NULL,
        IsActive BIT NULL
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.Category)
BEGIN
    INSERT dbo.Category(CategoryName, CategoryDesciption, ParentCategoryID, IsActive)
    VALUES
        (N'Academic news', N'Academic announcements', NULL, 1),
        (N'Student Affairs', N'Student activities and events', NULL, 1),
        (N'Campus Safety', N'Campus safety information', NULL, 1);
END;
GO

-- Minimal NewsArticle mapping permitted by section 6.2 of the Slot22 PDF.
IF OBJECT_ID('dbo.NewsArticle', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.NewsArticle (
        NewsArticleID NVARCHAR(20) PRIMARY KEY,
        CategoryID SMALLINT NOT NULL,
        CONSTRAINT FK_NewsArticle_Category
            FOREIGN KEY (CategoryID) REFERENCES dbo.Category(CategoryID)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.NewsArticle WHERE NewsArticleID = N'S22-SEED-001')
BEGIN
    INSERT dbo.NewsArticle(NewsArticleID, CategoryID)
    SELECT N'S22-SEED-001', CategoryID FROM dbo.Category
    WHERE CategoryName = N'Academic news';
END;
GO

SELECT * FROM dbo.Category;
SELECT * FROM dbo.NewsArticle;
