SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema appmarket
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `appmarket` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `appmarket` ;

-- -----------------------------------------------------
-- Table `appmarket`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `appmarket`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `groupId` INT NOT NULL,
  `mobile` VARCHAR(45) NOT NULL COMMENT '手机号',
  `name` VARCHAR(45) NULL COMMENT '姓名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `createTime` DATETIME NOT NULL COMMENT '创建时间',
  `updateTime` DATETIME NULL COMMENT '最近一次更新',
  `status` TINYINT(1) NOT NULL COMMENT '用户状态',
  `role` TINYINT(1) NOT NULL COMMENT '用户角色',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `mobile_UNIQUE` (`mobile` ASC))
ENGINE = InnoDB
COMMENT = '用户';


-- -----------------------------------------------------
-- Table `appmarket`.`apps`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `appmarket`.`apps` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `sku` VARCHAR(45) NOT NULL,
  `version` VARCHAR(25) NOT NULL,
  `createTime` DATETIME NOT NULL,
  `updateTime` DATETIME NULL,
  `status` TINYINT(1) NOT NULL,
  `platform` TINYINT(1) NOT NULL,
  `bundleIdentifier` VARCHAR(200) NOT NULL,
  `metaData` TEXT(1000) NULL,
  `enableUpdateCheck` TINYINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `sku_UNIQUE` (`sku` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `appmarket`.`user_groups`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `appmarket`.`user_groups` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(120) NOT NULL COMMENT '分组名',
  `enabled` TINYINT(1) NOT NULL COMMENT '是否启用',
  PRIMARY KEY (`id`, `name`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
COMMENT = '用户分组';


-- -----------------------------------------------------
-- Table `appmarket`.`group_app_accesses`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `appmarket`.`group_app_accesses` (
  `groupId` INT NOT NULL,
  `appId` INT NOT NULL,
  PRIMARY KEY (`groupId`, `appId`))
ENGINE = InnoDB
COMMENT = '用户组应用访问';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;