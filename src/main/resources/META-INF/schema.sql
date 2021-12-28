CREATE TABLE `t_user`
(
    `id`          BIGINT unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name`        VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '名称',
    `extras`      VARCHAR(120) NOT NULL DEFAULT '' COMMENT '扩展信息',
    `description` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '描述信息',
    `creator`     VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '创建人',
    `editor`      VARCHAR(30)  NOT NULL DEFAULT '' COMMENT '修改人',
    `deleted`     TINYINT unsigned NOT NULL DEFAULT 0 COMMENT '是否启用, 0:禁用，1:启用',
    `createTime`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户表';

