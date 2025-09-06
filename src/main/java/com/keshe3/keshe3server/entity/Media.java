package com.keshe3.keshe3server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
* <p>
* 媒体表
* </p>
*
* @author CodeGenerator
* @since 2025-09-06
*/
@Data
public class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 媒体编号
    */
    private String id;

    /**
    * 上传用户ID
    */
    private String userId;

    /**
    * 媒体名称
    */
    private String mediaName;

    /**
    * 媒体类型
    */
    private String mediaType;

    /**
    * 媒体大小
    */
    private Long mediaSize;

    /**
    * 媒体存储路径
    */
    private String mediaPath;

    /**
    * 上传时间
    */
    private LocalDateTime uploadTime;
}