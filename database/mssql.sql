-- MSSQL

USE master
GO

-- -----------------------------------------------------
-- Schema self-service-sap
-- -----------------------------------------------------
DROP DATABASE IF EXISTS [self-service-sap]
GO

-- -----------------------------------------------------
-- Schema self-service-sap
-- -----------------------------------------------------
CREATE DATABASE [self-service-sap]
GO

USE [self-service-sap]
GO

-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."configuration"
-- -----------------------------------------------------
CREATE TABLE [self-service-sap].[dbo].[configuration] (
  "id" VARCHAR(255) NOT NULL,
  "value" VARCHAR(255) NOT NULL,
  PRIMARY KEY ("id")
);

-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."user"
-- -----------------------------------------------------
CREATE TABLE [self-service-sap].[dbo].[user] (
  "uuid" VARCHAR(40) NOT NULL,
  "username" VARCHAR(255) NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "password" VARCHAR(64) NOT NULL,
  "email" VARCHAR(255) NOT NULL,
  "password_recovery_key" VARCHAR(255) NULL,
  "password_recovery_expiration_date" DATETIME NULL,
  PRIMARY KEY ("uuid"),
  CONSTRAINT "username_UNIQUE" UNIQUE ("username")
);

-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."permission"
-- -----------------------------------------------------
CREATE TABLE [self-service-sap].[dbo]."permission" (
  "id" VARCHAR(8) NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "description" VARCHAR(255) NOT NULL,
  PRIMARY KEY ("id"))
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."user_has_permission"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."user_has_permission" (
  "user_uuid" VARCHAR(40) NOT NULL,
  "permission_id" VARCHAR(8) NOT NULL,
  PRIMARY KEY ("user_uuid", "permission_id"),
  INDEX "fk_user_has_permission_permission1_idx" ("permission_id" ASC),
  INDEX "fk_user_has_permission_user_idx" ("user_uuid" ASC),
  CONSTRAINT "fk_user_has_permission_user"
    FOREIGN KEY ("user_uuid")
    REFERENCES [self-service-sap].[dbo]."user" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT "fk_user_has_permission_permission1"
    FOREIGN KEY ("permission_id")
    REFERENCES [self-service-sap].[dbo]."permission" ("id")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."enterprise"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."enterprise" (
  "uuid" VARCHAR(40) NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  PRIMARY KEY ("uuid"))
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."enterprise_has_user"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."enterprise_has_user" (
  "enterprise_uuid" VARCHAR(40) NOT NULL,
  "user_uuid" VARCHAR(40) NOT NULL,
  PRIMARY KEY ("enterprise_uuid", "user_uuid"),
  INDEX "fk_enterprise_has_user_user1_idx" ("user_uuid" ASC),
  INDEX "fk_enterprise_has_user_enterprise1_idx" ("enterprise_uuid" ASC),
  CONSTRAINT "fk_enterprise_has_user_enterprise1"
    FOREIGN KEY ("enterprise_uuid")
    REFERENCES [self-service-sap].[dbo]."enterprise" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT "fk_enterprise_has_user_user1"
    FOREIGN KEY ("user_uuid")
    REFERENCES [self-service-sap].[dbo]."user" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."environment"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."environment" (
  "uuid" VARCHAR(40) NOT NULL,
  "alias" VARCHAR(255) NOT NULL,
  "enterprise_uuid" VARCHAR(40) NOT NULL,
  "wsdl_location" VARCHAR(1024) NOT NULL,
  "username" VARCHAR(255) NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  "systems_enabled" BIT NOT NULL DEFAULT 0,
  PRIMARY KEY ("uuid"),
  INDEX "fk_environment_enterprise1_idx" ("enterprise_uuid" ASC),
  CONSTRAINT "fk_environment_enterprise1"
    FOREIGN KEY ("enterprise_uuid")
    REFERENCES [self-service-sap].[dbo]."enterprise" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."self_service_request_type"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."self_service_request_type" (
  "id" VARCHAR(8) NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "description" VARCHAR(255) NOT NULL,
  PRIMARY KEY ("id"))
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."self_service_request"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."self_service_request" (
  "uuid" VARCHAR(40) NOT NULL,
  "user_uuid" VARCHAR(40) NULL,
  "self_service_request_type_id" VARCHAR(8) NOT NULL,
  "request_timestamp" DATETIME NULL,
  "environment_uuid" VARCHAR(40) NOT NULL,
  "target_user" VARCHAR(255) NOT NULL,
  "notes" VARCHAR(255) NOT NULL,
  "client_ip" VARCHAR(45) NOT NULL,
  "confirmation_expiration_date" DATETIME NULL,
  "confirmation_code" VARCHAR(40) NULL,
  "fulfillment_date" DATETIME NULL,
  "invalidated" BIT NOT NULL,
  "system" VARCHAR(255) NULL,
  PRIMARY KEY ("uuid"),
  INDEX "fk_self_service_request_user1_idx" ("user_uuid" ASC),
  INDEX "fk_self_service_request_self_service_request_type1_idx" ("self_service_request_type_id" ASC),
  INDEX "fk_self_service_request_environment1_idx" ("environment_uuid" ASC),
  CONSTRAINT "confirmation_code_UNIQUE" UNIQUE ("confirmation_code"),
  CONSTRAINT "fk_self_service_request_user1"
    FOREIGN KEY ("user_uuid")
    REFERENCES [self-service-sap].[dbo]."user" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT "fk_self_service_request_self_service_request_type1"
    FOREIGN KEY ("self_service_request_type_id")
    REFERENCES [self-service-sap].[dbo]."self_service_request_type" ("id")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT "fk_self_service_request_environment1"
    FOREIGN KEY ("environment_uuid")
    REFERENCES [self-service-sap].[dbo]."environment" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."system"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."system" (
  "id" VARCHAR(255) NOT NULL,
  "environment" VARCHAR(40) NOT NULL,
  PRIMARY KEY ("id", "environment"),
  INDEX "fk_system_environment1_idx" ("environment" ASC),
  CONSTRAINT "fk_system_environment1"
    FOREIGN KEY ("environment")
    REFERENCES [self-service-sap].[dbo]."environment" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table `self-service-sap`.`active_directory_environment`
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo].[active_directory_environment] (
  "uuid" VARCHAR(40) NOT NULL,
  "alias" VARCHAR(255) NOT NULL,
  "enterprise" VARCHAR(40) NOT NULL,
  PRIMARY KEY ("uuid"),
  INDEX "fk_active_directory_domain_enterprise1_idx" ("enterprise" ASC),
  CONSTRAINT "fk_active_directory_domain_enterprise1"
    FOREIGN KEY ("enterprise")
    REFERENCES [self-service-sap].[dbo]."enterprise" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Table [self-service-sap].[dbo]."active_directory_domain"
-- -----------------------------------------------------
CREATE TABLE  [self-service-sap].[dbo]."active_directory_domain" (
  "uuid" VARCHAR(40) NOT NULL,
  "environment" VARCHAR(40) NOT NULL,
  "url" VARCHAR(255) NOT NULL,
  "bind_dn" VARCHAR(255) NOT NULL,
  "bind_pw" VARCHAR(255) NOT NULL,
  "search_base" VARCHAR(255) NOT NULL,
  "search_filter" VARCHAR(255) NOT NULL,
  PRIMARY KEY ("uuid"),
  INDEX "fk_active_directory_domain_active_directory_environment1_idx" ("environment" ASC),
  CONSTRAINT "fk_active_directory_domain_active_directory_environment1"
    FOREIGN KEY ("environment")
    REFERENCES [self-service-sap].[dbo]."active_directory_environment" ("uuid")
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
;


-- -----------------------------------------------------
-- Data for table [self-service-sap].[dbo]."user"
-- -----------------------------------------------------
BEGIN TRANSACTION;
USE [self-service-sap];
INSERT INTO [self-service-sap].[dbo]."user" ("uuid", "username", "name", "password", "email", "password_recovery_key", "password_recovery_expiration_date") VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'admin', 'Sin nombre', 'fe4e89360be8efe311ce629309d49209803b51e39b1ec59e835dfff9b937093c', 'no-mail', NULL, NULL);

COMMIT;


-- -----------------------------------------------------
-- Data for table [self-service-sap].[dbo]."permission"
-- -----------------------------------------------------
BEGIN TRANSACTION;
USE [self-service-sap];
INSERT INTO [self-service-sap].[dbo]."permission" ("id", "name", "description") VALUES ('USRADM', 'Administrador de usuarios', 'Permiso para crear, modificar y eliminar usuarios');
INSERT INTO [self-service-sap].[dbo]."permission" ("id", "name", "description") VALUES ('ENTADM', 'Administrador de empresas', 'Permiso para crear, modificar y eliminar empresas y entornos');
INSERT INTO [self-service-sap].[dbo]."permission" ("id", "name", "description") VALUES ('ENTUSR', 'Usuario de empresa', 'Permiso para consumir los servicios de auto-servicio sap');

COMMIT;


-- -----------------------------------------------------
-- Data for table [self-service-sap].[dbo]."user_has_permission"
-- -----------------------------------------------------
BEGIN TRANSACTION;
USE [self-service-sap];
INSERT INTO [self-service-sap].[dbo]."user_has_permission" ("user_uuid", "permission_id") VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'USRADM');
INSERT INTO [self-service-sap].[dbo]."user_has_permission" ("user_uuid", "permission_id") VALUES ('4e33d1ac-5698-11e7-907b-a6006ad3dba0', 'ENTADM');

COMMIT;


-- -----------------------------------------------------
-- Data for table [self-service-sap].[dbo]."self_service_request_type"
-- -----------------------------------------------------
BEGIN TRANSACTION;
USE [self-service-sap];
INSERT INTO [self-service-sap].[dbo]."self_service_request_type" ("id", "name", "description") VALUES ('USRUNLCK', 'Desbloquear usuario', 'Desbloquear usuario');
INSERT INTO [self-service-sap].[dbo]."self_service_request_type" ("id", "name", "description") VALUES ('PWDRESET', 'Reestablecer contraseña', 'Reestablecer contraseña');

COMMIT;