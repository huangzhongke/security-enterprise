CREATE TABLE `spider_account`
(
    `id`          bigint(20) NOT NULL,
    `user`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '账号',
    `password`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
    `agent_ip`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '代理IP',
    `original_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '网站起始IP',
    `type`        tinyint(4)                                              DEFAULT NULL COMMENT '分类',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='账号';

CREATE TABLE `spider_account_line`
(
    `account_id` bigint(20) NOT NULL COMMENT '账号ID',
    `line_id`    bigint(20) NOT NULL COMMENT '航线ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='中间表';

CREATE TABLE `spider_line`
(
    `id`     bigint(20) NOT NULL,
    `params` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '参数',
    `job_id` bigint(20) DEFAULT NULL COMMENT '定时任务ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  ROW_FORMAT = DYNAMIC COMMENT ='航线参数';

CREATE TABLE `spider_order`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `from`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '起始港',
    `to`                 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '目的港',
    `equipment`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '柜子',
    `quantity`           int(12)                                                      DEFAULT NULL COMMENT '数量',
    `orderSleepTime`     int(11)                                                      DEFAULT NULL COMMENT '下单间隔时间',
    `orderDate`          datetime                                                     DEFAULT NULL COMMENT '下单时间',
    `isProxy`            int(12)                                                      DEFAULT NULL COMMENT '是否使用代理',
    `isNeedSupplierName` int(12)                                                      DEFAULT NULL COMMENT '是否指定航线代码',
    `isNeedLineName`     int(12)                                                      DEFAULT NULL COMMENT '是否指定航名航次',
    `vessel`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '航名',
    `voyage`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '航次',
    `supplierName`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '航线代码',
    `account`            int(11)                                                      DEFAULT NULL COMMENT '账号',
    `etd`                int(11)                                                      DEFAULT NULL COMMENT 'etd间隔时间',
    `reference`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '小提单号',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `spider_references`
(
    `id`        bigint(20) NOT NULL,
    `reference` varchar(255) DEFAULT NULL,
    `account`   int(11)      DEFAULT NULL,
    `used`      tinyint(1)   DEFAULT NULL,
    `line_id`   bigint(20)   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `lineId` (`line_id`),
    CONSTRAINT `spider_references_ibfk_1` FOREIGN KEY (`line_id`) REFERENCES `spider_line` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;