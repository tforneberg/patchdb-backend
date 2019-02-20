-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema patchdb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema patchdb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `patchdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `patchdb` ;

-- -----------------------------------------------------
-- Table `patchdb`.`bands`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patchdb`.`bands` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `patchdb`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patchdb`.`users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `image` VARCHAR(45) NULL DEFAULT NULL,
  `status` ENUM('admin', 'mod', 'user', 'blocked_user') NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `patchdb`.`patches`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `patchdb`.`patches` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `band_id` INT(11) NULL DEFAULT NULL,
  `date_inserted` DATETIME NOT NULL,
  `user_inserted` INT(11) NOT NULL,
  `description` VARCHAR(2048) NULL DEFAULT NULL,
  `image` VARCHAR(256) NULL DEFAULT NULL,
  `type` ENUM('woven', 'stitched', 'printed') NULL DEFAULT NULL,
  `num_of_copies` INT(11) NULL DEFAULT NULL,
  `manufacturer` VARCHAR(45) NULL DEFAULT NULL,
  `release_date` DATE NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `id_idx` (`user_inserted` ASC) VISIBLE,
  INDEX `band_id_idx` (`band_id` ASC) VISIBLE,
  CONSTRAINT `band_id`
    FOREIGN KEY (`band_id`)
    REFERENCES `patchdb`.`bands` (`id`),
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_inserted`)
    REFERENCES `patchdb`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
