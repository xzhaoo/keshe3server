package com.keshe3.keshe3server.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 映射 Python detect 接口返回的 JSON 响应
 */
public class PythonDetectionResp {

    private int code;           // 返回码
    private String status;      // success / error / stopped
    private String message;     // 提示信息

    @JsonProperty("file_type")
    private String fileType;    // image 或 video

    // 图片专用
    @JsonProperty("person_count")
    private Integer personCount; // 检测到的人数（图片）

    // 视频专用
    @JsonProperty("total_frames_processed")
    private Integer totalFramesProcessed;

    @JsonProperty("frame_interval")
    private Integer frameInterval;

    @JsonProperty("processed_frames")
    private Integer processedFrames; // 如果中断，返回已处理帧数

    // 结果文件路径
    @JsonProperty("result_file_url")
    private String resultFileUrl;

    @JsonProperty("absolute_url")
    private String absoluteUrl;

    // Getter / Setter
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Integer getPersonCount() { return personCount; }
    public void setPersonCount(Integer personCount) { this.personCount = personCount; }

    public Integer getTotalFramesProcessed() { return totalFramesProcessed; }
    public void setTotalFramesProcessed(Integer totalFramesProcessed) { this.totalFramesProcessed = totalFramesProcessed; }

    public Integer getFrameInterval() { return frameInterval; }
    public void setFrameInterval(Integer frameInterval) { this.frameInterval = frameInterval; }

    public Integer getProcessedFrames() { return processedFrames; }
    public void setProcessedFrames(Integer processedFrames) { this.processedFrames = processedFrames; }

    public String getResultFileUrl() { return resultFileUrl; }
    public void setResultFileUrl(String resultFileUrl) { this.resultFileUrl = resultFileUrl; }

    public String getAbsoluteUrl() { return absoluteUrl; }
    public void setAbsoluteUrl(String absoluteUrl) { this.absoluteUrl = absoluteUrl; }
}
