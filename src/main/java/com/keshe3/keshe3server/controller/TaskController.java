package com.keshe3.keshe3server.controller;

import com.keshe3.keshe3server.entity.Task;
import com.keshe3.keshe3server.resp.TzResp;
import com.keshe3.keshe3server.service.ITaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* <p>
* 任务表 前端控制器
* </p>
*
* @author CodeGenerator
* @since 2025-09-06
*/
@RestController
@RequestMapping("task")
public class TaskController {


    /**
     * 添加任务
     */
    @PostMapping("addTask")
    public TzResp<?> addTask(Task task) {
        //获取并保存文件（调用上传文件）（媒体服务）

        //创建任务信息并保存至数据库（调用信息创建）
        //添加到任务队列（调用任务添加）
        //返回
        return null;
    }
}