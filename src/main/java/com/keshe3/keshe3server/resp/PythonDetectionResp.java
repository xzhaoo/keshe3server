package com.keshe3.keshe3server.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PythonDetectionResp {

    /**
     * 响应码
     */
    private int code;

    /**
     * 检测状态
     */
    private String status;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 视频文件类型
     */
    @JsonProperty("file_type")
    private String fileType;

    /**
     * 检测到的人数
     */
    @JsonProperty("person_count")
    private Integer personCount;

    /**
     * 总帧数
     */
    @JsonProperty("total_frames_processed")
    private Integer totalFramesProcessed;

    /**
     * 视频帧率
     */
    @JsonProperty("frame_interval")
    private Integer frameInterval;

    /**
     * 已处理的帧数
     */
    @JsonProperty("processed_frames")
    private Integer processedFrames;

    /**
     * 视频检测结果文件 URL
     */
    @JsonProperty("result_file_url")
    private String resultFileUrl;

    /**
     * 视频检测结果文件绝对路径
     */
    @JsonProperty("absolute_url")
    private String absoluteUrl;
}