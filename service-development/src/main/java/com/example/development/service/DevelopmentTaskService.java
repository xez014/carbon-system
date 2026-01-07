package com.example.development.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.development.entity.DevelopmentTask;

import java.util.List;

public interface DevelopmentTaskService extends IService<DevelopmentTask> {

    List<DevelopmentTask> getTasksByProjectId(Long projectId);

    List<DevelopmentTask> getTasksByStep(Long projectId, Integer step);

    boolean updateTaskStatus(Long taskId, String status);

    List<DevelopmentTask> initDefaultTasks(Long projectId);
}
