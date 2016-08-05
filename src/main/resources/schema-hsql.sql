-- HSQLDB 数据库程序数据库初始化脚本
-- 2016-07-26

--
-- 创建应用信息表 ( apps ) --

CREATE TABLE IF NOT EXISTS PUBLIC.APPS
(ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
NAME VARCHAR(45) NOT NULL,
SKU VARCHAR(45) DEFAULT '' NOT NULL,
VERSION VARCHAR(25) NOT NULL,
UPDATETIME TIMESTAMP,
CREATETIME TIMESTAMP NOT NULL,
STATUS TINYINT NOT NULL,
PLATFORM TINYINT NOT NULL,
BUNDLEIDENTIFIER VARCHAR(200) NOT NULL,
METADATA VARCHAR(1000),
ICON BLOB,
BUNDLE BLOB NOT NULL,
PRIMARY KEY (ID));

--
-- 创建用户表 ( users ) --

CREATE TABLE IF NOT EXISTS PUBLIC.USERS
(ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
MOBILE VARCHAR(45) NOT NULL,
NAME VARCHAR(45),
PASSWORD VARCHAR(100) NOT NULL,
UPDATETIME TIMESTAMP,
CREATETIME TIMESTAMP NOT NULL,
STATUS TINYINT NOT NULL,
ROLE TINYINT NOT NULL,
PRIMARY KEY (ID),
UNIQUE (MOBILE));

ALTER TABLE PUBLIC.USERS ADD COLUMN NAME VARCHAR(45);
ALTER TABLE PUBLIC.APPS ADD COLUMN SKU VARCHAR(45) DEFAULT '' NOT NULL;