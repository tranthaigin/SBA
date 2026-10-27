IF DB_ID('SBA301_OrchidDB') IS NULL
    CREATE DATABASE SBA301_OrchidDB;
GO
USE SBA301_OrchidDB;
GO
CREATE DATABASE OrchidManagementDB;
GO

USE OrchidManagementDB;
GO

CREATE TABLE Orchid (
    OrchidID INT IDENTITY(1,1) PRIMARY KEY,
    OrchidName NVARCHAR(100) NOT NULL,
    IsNatural BIT NOT NULL,
    OrchidDescription NVARCHAR(500),
    OrchidCategory NVARCHAR(100),
    IsAttractive BIT NOT NULL,
    OrchidURL NVARCHAR(500)
);
GO

INSERT INTO Orchid
    (OrchidName, IsNatural, OrchidDescription, OrchidCategory, IsAttractive, OrchidURL)
VALUES
    (N'Taichung Beauty', 1, N'Popular Cattleya orchid', N'Cattleya', 1,
     N'https://example.com/orchid1.jpg'),
    (N'Phalaenopsis White', 1, N'White moth orchid', N'Phalaenopsis', 1,
     N'https://example.com/orchid2.jpg'),
    (N'Dendrobium Purple', 1, N'Purple dendrobium orchid', N'Dendrobium', 0,
     N'https://example.com/orchid3.jpg');
GO
