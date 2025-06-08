create database bas;
use bas;
drop table if exists balance;
create table balance(
    id bigint primary key NOT NULL auto_increment comment 'id',
    accountId bigint not null comment '账户id',
    amount DECIMAL(18,2)comment '余额',
    currency varchar(32) not null comment '币种'
);
drop table  if exists transaction_status;
CREATE TABLE transaction_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(64) NOT NULL UNIQUE,
    from_account VARCHAR(32) NOT NULL,
    to_account VARCHAR(32) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency_type VARCHAR(10) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `user` ( `user` VARCHAR(32) NOT NULL COMMENT '用户名', `nickname` VARCHAR(24) DEFAULT NULL COMMENT '昵称',
                      `Identity Type` TINYINT DEFAULT NULL COMMENT '类型',
                      `Identity Card` TINYINT DEFAULT NULL COMMENT '证件号码',
                      `gender` VARCHAR(24) DEFAULT NULL COMMENT '性别',
                      `phone` VARCHAR(24) DEFAULT NULL COMMENT '电话',
                      `email` VARCHAR(48) DEFAULT NULL COMMENT '邮箱',
                      `logo` VARCHAR(255) DEFAULT NULL COMMENT '头像',
                      `password` VARCHAR(64) NOT NULL COMMENT '密码',
                      `card_id` VARCHAR(48) DEFAULT NULL COMMENT '卡号',
                      `balance` VARCHAR(48) DEFAULT NULL COMMENT '余额',
                      `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                      `is_delete` TINYINT DEFAULT 0 COMMENT '删除标记(0:正常 1:删除)',
                      PRIMARY KEY (`user`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';