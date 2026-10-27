/*
    SBA301 Lab 06 - SQL Server database setup

    Sample account password (all accounts): P@ssword123
    The password column stores BCrypt hashes, never plaintext passwords.

    This script is idempotent: it can be executed more than once without
    recreating the database, table, constraint, or sample rows.
*/

USE master;
GO

IF DB_ID(N'OrchidJWTLab') IS NULL
BEGIN
    PRINT N'Creating database OrchidJWTLab...';
    CREATE DATABASE OrchidJWTLab;
END
ELSE
BEGIN
    PRINT N'Database OrchidJWTLab already exists.';
END;
GO

USE OrchidJWTLab;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO

/*
    Table mapping:
      User.id        -> id
      User.fullName  -> full_name
      User.email     -> email
      User.password  -> password
      User.createdAt -> created_at
      User.updatedAt -> updated_at
*/
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    PRINT N'Creating table dbo.users...';

    CREATE TABLE dbo.users
    (
        id         INT IDENTITY(1, 1) NOT NULL,
        full_name  NVARCHAR(150) NOT NULL,
        email      NVARCHAR(100) NOT NULL,
        [password] NVARCHAR(100) NOT NULL,
        created_at DATETIME2(6) NOT NULL
            CONSTRAINT DF_users_created_at DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2(6) NOT NULL
            CONSTRAINT DF_users_updated_at DEFAULT SYSUTCDATETIME(),

        CONSTRAINT PK_users PRIMARY KEY CLUSTERED (id),
        CONSTRAINT UQ_users_email UNIQUE (email),
        CONSTRAINT CK_users_email_not_blank
            CHECK (LEN(LTRIM(RTRIM(email))) > 0),
        CONSTRAINT CK_users_full_name_not_blank
            CHECK (LEN(LTRIM(RTRIM(full_name))) > 0),
        CONSTRAINT CK_users_password_bcrypt
            CHECK ([password] LIKE '$2a$%' OR
                   [password] LIKE '$2b$%' OR
                   [password] LIKE '$2y$%')
    );
END
ELSE
BEGIN
    PRINT N'Table dbo.users already exists.';
END;
GO

/*
    Add the unique constraint when the table came from an older version of
    the lab. The guard also avoids creating a duplicate unique index.
*/
IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
   AND NOT EXISTS
   (
       SELECT 1
       FROM sys.indexes AS i
       INNER JOIN sys.index_columns AS ic
           ON ic.object_id = i.object_id AND ic.index_id = i.index_id
       INNER JOIN sys.columns AS c
           ON c.object_id = ic.object_id AND c.column_id = ic.column_id
       WHERE i.object_id = OBJECT_ID(N'dbo.users')
         AND i.is_unique = 1
         AND c.name = N'email'
   )
BEGIN
    ALTER TABLE dbo.users
        ADD CONSTRAINT UQ_users_email UNIQUE (email);
END;
GO

BEGIN TRANSACTION;

/*
    Seed accounts. BCrypt uses a random salt, so different hashes can represent
    the same password. All three hashes below match: P@ssword123
*/
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'student01@fpt.edu.vn')
BEGIN
    INSERT INTO dbo.users (full_name, email, [password])
    VALUES
    (
        N'SBA301 Student 01',
        N'student01@fpt.edu.vn',
        N'$2a$10$AdJeoVsvL1Z/mTil0aRZvetTxcccs7N2jVBQk9Ds57DlwU03H5jhy'
    );
END;

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'student02@fpt.edu.vn')
BEGIN
    INSERT INTO dbo.users (full_name, email, [password])
    VALUES
    (
        N'SBA301 Student 02',
        N'student02@fpt.edu.vn',
        N'$2a$10$yR/BQ6ugXNEsMbagcLsSfuRAAQ.kCDkpWn9SYuICiZpxa0StOfGQy'
    );
END;

IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'lecturer@fpt.edu.vn')
BEGIN
    INSERT INTO dbo.users (full_name, email, [password])
    VALUES
    (
        N'SBA301 Lecturer',
        N'lecturer@fpt.edu.vn',
        N'$2a$10$0wUTruuvMVSGRjWqb/rNweL2wtKjg9dSvNuZr/dfaF20mKIlJENcq'
    );
END;

COMMIT TRANSACTION;
GO

/* Verification - hashes are intentionally shown only for this local lab. */
SELECT
    DB_NAME() AS current_database,
    COUNT(*) AS total_users
FROM dbo.users;

SELECT
    id,
    full_name,
    email,
    LEFT([password], 7) + N'***' AS password_hash_preview,
    created_at,
    updated_at
FROM dbo.users
ORDER BY id;
GO
