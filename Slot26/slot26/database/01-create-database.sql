USE [master];
GO

IF DB_ID(N'OrchidManagementDB') IS NULL
BEGIN
    CREATE DATABASE [OrchidManagementDB];
    PRINT N'Created database OrchidManagementDB.';
END
ELSE
BEGIN
    PRINT N'Database OrchidManagementDB already exists; no changes made.';
END
GO
