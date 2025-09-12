package com.keshe3.keshe3server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
* <p>
* 活动日志记录表
* </p>
*
* @author CodeGenerator
* @since 2025-09-12
*/
@Data
@TableName("activity_log")
public class ActivityLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 日志编号
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 用户ID
    */
    private String userId;

    /**
    * 操作
    */
    private String action;

    /**
    * 细节
    */
    private String details;

    /**
    * 操作时间
    */
    private LocalDateTime time;
}