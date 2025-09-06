package com.keshe3.keshe3server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
* <p>
    * 任务表
    * </p>
*
* @author CodeGenerator
* @since 2025-09-06
*/
@Data
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 任务编号
    */
    private String id;

    /**
    * 上传用户ID
    */
    private String userId;

    /**
    * 媒体编号
    */
    private String mediaId;

    /**
    * 任务开始时间
    */
    private LocalDateTime startTime;

    /**
    * 任务结束时间
    */
    private LocalDateTime endTime;

    /**
    * 任务状态
    */
    private String taskStatus;
}