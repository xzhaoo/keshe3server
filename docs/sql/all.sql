DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                   `id` varchar(18) NOT NULL COMMENT '用户编号',
                   `user_name` varchar(20) NOT NULL COMMENT '用户名称',
                   `user_password` varchar(20) NOT NULL COMMENT '用户密码',
                   `user_email` varchar(20) NOT NULL COMMENT '用户邮箱',
                   `user_permission` varchar(2) NOT NULL COMMENT '用户权限',
                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表';

DROP TABLE IF EXISTS `media`;
CREATE TABLE `media`  (
                          `id` varchar(18) NOT NULL COMMENT '媒体编号',
                          `user_id` varchar(18) NOT NULL COMMENT '上传用户ID',
                          `media_name` varchar(255) NOT NULL COMMENT '媒体名称',
                          `media_type` varchar(2) NOT NULL COMMENT '媒体类型',
                          `media_size` bigint NOT NULL COMMENT '媒体大小',
                          `media_path` varchar(255) NOT NULL COMMENT '媒体存储路径',
                          `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '媒体表';

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`  (
                          `id` varchar(18) NOT NULL COMMENT '任务编号',
                          `user_id` varchar(18) NOT NULL COMMENT '上传用户ID',
                          `media_id` varchar(18) NOT NULL COMMENT '媒体编号',
                          `start_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '任务开始时间',
                          `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '任务结束时间',
                          `task_status` varchar(20) NOT NULL COMMENT '任务状态',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表';
