CREATE TABLE `admin_user` (
  `id` varchar(20) NOT NULL,
  `password` varchar(64) DEFAULT NULL,
  `name` varchar(30) NOT NULL,
  `del_yn` tinyint NOT NULL,
  `reg_date` datetime NOT NULL,
  `mod_date` datetime DEFAULT NULL,
  `email` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `rearer_user` (
  `admin_id` varchar(20) NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `contact` varchar(20) NOT NULL,
  `birthday` date NOT NULL,
  `reg_date` datetime NOT NULL,
  `mod_date` datetime DEFAULT NULL,
  `del_yn` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`contact`),
  UNIQUE KEY `UNIQUE_rearer` (`id`,`contact`),
  KEY `FK___admin_id___in_patient` (`admin_id`),
  CONSTRAINT `FK___admin_id___in_patient` FOREIGN KEY (`admin_id`) REFERENCES `admin_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `baby_user` (
  `rearer_id` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `sex` int NOT NULL,
  `birthday` date NOT NULL,
  `reg_date` datetime NOT NULL,
  `mod_date` datetime DEFAULT NULL,
  `del_yn` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK___rearer_id___in_patient` (`rearer_id`),
  CONSTRAINT `FK___rearer_id___in_patient` FOREIGN KEY (`rearer_id`) REFERENCES `rearer_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `baby_growth` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `baby_id` int DEFAULT NULL,
  `nurse` int unsigned DEFAULT '0',
  `feces` int unsigned DEFAULT '0',
  `urine` int unsigned DEFAULT '0',
  `vomit` int unsigned DEFAULT '0',
  `mod_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reg_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK___baby_id___in_babygrowth` (`baby_id`),
  CONSTRAINT `FK___baby_id___in_babygrowth` FOREIGN KEY (`baby_id`) REFERENCES `baby_user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `environment_last` (
  `admin_id` varchar(20) NOT NULL,
  `dust_data` float DEFAULT NULL,
  `dust_mod_date` datetime DEFAULT NULL,
  `co2_data` float DEFAULT NULL,
  `co2_mod_date` datetime DEFAULT NULL,
  `temp_data` float DEFAULT NULL,
  `temp_mod_date` datetime DEFAULT NULL,
  `humi_data` float DEFAULT NULL,
  `humi_mod_date` datetime DEFAULT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `map_admin_scanner` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `admin_id` varchar(20) NOT NULL,
  `mac` varchar(17) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_Admin_Scanner` (`admin_id`,`mac`),
  CONSTRAINT `FK_admin_id_in_MapAdminScanner` FOREIGN KEY (`admin_id`) REFERENCES `admin_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `map_device` (
  `did` int NOT NULL,
  `admin_id` varchar(20) NOT NULL,
  `baby_id` int DEFAULT NULL,
  `cam_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`did`),
  UNIQUE KEY `cam_id_UNIQUE` (`cam_id`),
  KEY `FK_admin_id_in_MapDevice` (`admin_id`),
  CONSTRAINT `FK_admin_id_in_MapDevice` FOREIGN KEY (`admin_id`) REFERENCES `admin_user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `measure_last` (
  `did` int NOT NULL,
  `temp_data` float DEFAULT NULL,
  `temp_battery` tinyint unsigned DEFAULT NULL,
  `temp_mod_date` datetime DEFAULT NULL,
  `heart_data` float DEFAULT NULL,
  `heart_battery` tinyint unsigned DEFAULT NULL,
  `heart_mod_date` datetime DEFAULT NULL,
  `breath_data` int unsigned DEFAULT NULL,
  `breath_battery` tinyint unsigned DEFAULT NULL,
  `breath_mod_date` datetime DEFAULT NULL,
  `spo2_data` float DEFAULT NULL,
  `spo2_battery` tinyint unsigned DEFAULT NULL,
  `spo2_mod_date` datetime DEFAULT NULL,
  `weight_data` float DEFAULT NULL,
  `weight_battery` tinyint unsigned DEFAULT NULL,
  `weight_mod_date` datetime DEFAULT NULL,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
