-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema self-service-sap
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `self-service-sap` ;

-- -----------------------------------------------------
-- Schema self-service-sap
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `self-service-sap` DEFAULT CHARACTER SET utf8 ;
USE `self-service-sap` ;

-- -----------------------------------------------------
-- Table `self-service-sap`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`user` (
  `uuid` VARCHAR(40) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password_recovery_key` VARCHAR(255) NULL,
  `password_recovery_expiration_date` DATETIME NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`permission` (
  `id` VARCHAR(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`user_has_permission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`user_has_permission` (
  `user_uuid` VARCHAR(40) NOT NULL,
  `permission_id` VARCHAR(8) NOT NULL,
  PRIMARY KEY (`user_uuid`, `permission_id`),
  INDEX `fk_user_has_permission_permission1_idx` (`permission_id` ASC),
  INDEX `fk_user_has_permission_user_idx` (`user_uuid` ASC),
  CONSTRAINT `fk_user_has_permission_user`
    FOREIGN KEY (`user_uuid`)
    REFERENCES `self-service-sap`.`user` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_has_permission_permission1`
    FOREIGN KEY (`permission_id`)
    REFERENCES `self-service-sap`.`permission` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`enterprise`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`enterprise` (
  `uuid` VARCHAR(40) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`uuid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`enterprise_has_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`enterprise_has_user` (
  `enterprise_uuid` VARCHAR(40) NOT NULL,
  `user_uuid` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`enterprise_uuid`, `user_uuid`),
  INDEX `fk_enterprise_has_user_user1_idx` (`user_uuid` ASC),
  INDEX `fk_enterprise_has_user_enterprise1_idx` (`enterprise_uuid` ASC),
  CONSTRAINT `fk_enterprise_has_user_enterprise1`
    FOREIGN KEY (`enterprise_uuid`)
    REFERENCES `self-service-sap`.`enterprise` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_enterprise_has_user_user1`
    FOREIGN KEY (`user_uuid`)
    REFERENCES `self-service-sap`.`user` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`environment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`environment` (
  `uuid` VARCHAR(40) NOT NULL,
  `alias` VARCHAR(255) NOT NULL,
  `enterprise_uuid` VARCHAR(40) NOT NULL,
  `wsdl_location` VARCHAR(1024) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `systems_enabled` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`uuid`),
  INDEX `fk_environment_enterprise1_idx` (`enterprise_uuid` ASC),
  CONSTRAINT `fk_environment_enterprise1`
    FOREIGN KEY (`enterprise_uuid`)
    REFERENCES `self-service-sap`.`enterprise` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`self_service_request_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`self_service_request_type` (
  `id` VARCHAR(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`self_service_request`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`self_service_request` (
  `uuid` VARCHAR(40) NOT NULL,
  `user_uuid` VARCHAR(40) NULL,
  `self_service_request_type_id` VARCHAR(8) NOT NULL,
  `request_timestamp` TIMESTAMP NULL,
  `environment_uuid` VARCHAR(40) NOT NULL,
  `target_user` VARCHAR(255) NOT NULL,
  `notes` VARCHAR(255) NOT NULL,
  `client_ip` VARCHAR(45) NOT NULL,
  `confirmation_expiration_date` TIMESTAMP NULL,
  `confirmation_code` VARCHAR(40) NULL,
  `fulfillment_date` TIMESTAMP NULL,
  `invalidated` TINYINT(1) NOT NULL,
  `system` VARCHAR(255) NULL,
  PRIMARY KEY (`uuid`),
  INDEX `fk_self_service_request_user1_idx` (`user_uuid` ASC),
  INDEX `fk_self_service_request_self_service_request_type1_idx` (`self_service_request_type_id` ASC),
  INDEX `fk_self_service_request_environment1_idx` (`environment_uuid` ASC),
  UNIQUE INDEX `confirmation_code_UNIQUE` (`confirmation_code` ASC),
  CONSTRAINT `fk_self_service_request_user1`
    FOREIGN KEY (`user_uuid`)
    REFERENCES `self-service-sap`.`user` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_self_service_request_self_service_request_type1`
    FOREIGN KEY (`self_service_request_type_id`)
    REFERENCES `self-service-sap`.`self_service_request_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_self_service_request_environment1`
    FOREIGN KEY (`environment_uuid`)
    REFERENCES `self-service-sap`.`environment` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`system`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`system` (
  `id` VARCHAR(255) NOT NULL,
  `environment` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`, `environment`),
  INDEX `fk_system_environment1_idx` (`environment` ASC),
  CONSTRAINT `fk_system_environment1`
    FOREIGN KEY (`environment`)
    REFERENCES `self-service-sap`.`environment` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`active_directory_environment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`active_directory_environment` (
  `uuid` VARCHAR(40) NOT NULL,
  `alias` VARCHAR(255) NOT NULL,
  `enterprise` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`uuid`),
  INDEX `fk_active_directory_domain_enterprise1_idx` (`enterprise` ASC),
  CONSTRAINT `fk_active_directory_domain_enterprise1`
    FOREIGN KEY (`enterprise`)
    REFERENCES `self-service-sap`.`enterprise` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`configuration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`configuration` (
  `id` VARCHAR(255) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `self-service-sap`.`active_directory_domain`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `self-service-sap`.`active_directory_domain` (
  `uuid` VARCHAR(40) NOT NULL,
  `environment` VARCHAR(40) NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `bind_dn` VARCHAR(255) NOT NULL,
  `bind_pw` VARCHAR(255) NOT NULL,
  `search_base` VARCHAR(255) NOT NULL,
  `search_filter` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`uuid`),
  INDEX `fk_active_directory_domain_active_directory_environment1_idx` (`environment` ASC),
  CONSTRAINT `fk_active_directory_domain_active_directory_environment1`
    FOREIGN KEY (`environment`)
    REFERENCES `self-service-sap`.`active_directory_environment` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `self-service-sap`.`user`
-- -----------------------------------------------------
START TRANSACTION;
USE `self-service-sap`;
INSERT INTO `self-service-sap`.`user` (`uuid`, `username`, `name`, `password`, `email`, `password_recovery_key`, `password_recovery_expiration_date`) VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'admin', 'Sin nombre', 'fe4e89360be8efe311ce629309d49209803b51e39b1ec59e835dfff9b937093c', 'no-mail', NULL, NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table `self-service-sap`.`permission`
-- -----------------------------------------------------
START TRANSACTION;
USE `self-service-sap`;
INSERT INTO `self-service-sap`.`permission` (`id`, `name`, `description`) VALUES ('USRADM', 'Administrador de usuarios', 'Permiso para crear, modificar y eliminar usuarios');
INSERT INTO `self-service-sap`.`permission` (`id`, `name`, `description`) VALUES ('ENTADM', 'Administrador de empresas', 'Permiso para crear, modificar y eliminar empresas y entornos');
INSERT INTO `self-service-sap`.`permission` (`id`, `name`, `description`) VALUES ('ENTUSR', 'Usuario de empresa', 'Permiso para consumir los servicios de auto-servicio sap');

COMMIT;


-- -----------------------------------------------------
-- Data for table `self-service-sap`.`user_has_permission`
-- -----------------------------------------------------
START TRANSACTION;
USE `self-service-sap`;
INSERT INTO `self-service-sap`.`user_has_permission` (`user_uuid`, `permission_id`) VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'USRADM');
INSERT INTO `self-service-sap`.`user_has_permission` (`user_uuid`, `permission_id`) VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'ENTADM');

COMMIT;


-- -----------------------------------------------------
-- Data for table `self-service-sap`.`self_service_request_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `self-service-sap`;
INSERT INTO `self-service-sap`.`self_service_request_type` (`id`, `name`, `description`) VALUES ('USRUNLCK', 'Desbloquear usuario', 'Desbloquear usuario');
INSERT INTO `self-service-sap`.`self_service_request_type` (`id`, `name`, `description`) VALUES ('PWDRESET', 'Reestablecer contraseña', 'Reestablecer contraseña');

COMMIT;

