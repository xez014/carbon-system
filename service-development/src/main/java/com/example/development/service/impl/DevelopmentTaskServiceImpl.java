package com.example.development.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.development.entity.DevelopmentTask;
import com.example.development.mapper.DevelopmentTaskMapper;
import com.example.development.service.DevelopmentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DevelopmentTaskServiceImpl extends ServiceImpl<DevelopmentTaskMapper, DevelopmentTask> implements DevelopmentTaskService {

    @Autowired
    private DevelopmentTaskMapper taskMapper;

    private static final String[] STEP_NAMES = {"立项", "PDD设计", "第三方审定", "主管部门备案", "减排量核证", "资产签发"};
    private static final String[] STEP_DESCRIPTIONS = {
        "提交项目建议书",
        "编写项目设计文件",
        "DOE 现场审定",
        "发改委/生态部备案",
        "监测与核证",
        "CCER 上市"
    };

    @Override
    public List<DevelopmentTask> getTasksByProjectId(Long projectId) {
        LambdaQueryWrapper<DevelopmentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevelopmentTask::getProjectId, projectId)
               .orderByAsc(DevelopmentTask::getStep);
        return taskMapper.selectList(wrapper);
    }

    @Override
    public List<DevelopmentTask> getTasksByStep(Long projectId, Integer step) {
        LambdaQueryWrapper<DevelopmentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevelopmentTask::getProjectId, projectId)
               .eq(DevelopmentTask::getStep, step)
               .orderByAsc(DevelopmentTask::getCreateTime);
        return taskMapper.selectList(wrapper);
    }

    @Override
    public boolean updateTaskStatus(Long taskId, String status) {
        DevelopmentTask task = taskMapper.selectById(taskId);
        if (task != null) {
            task.setStatus(status);
            if ("COMPLETED".equals(status)) {
                task.setCompletedDate(LocalDateTime.now());
            }
            task.setUpdateTime(LocalDateTime.now());
            return taskMapper.updateById(task) > 0;
        }
        return false;
    }

    @Override
    public List<DevelopmentTask> initDefaultTasks(Long projectId) {
        List<DevelopmentTask> tasks = new ArrayList<>();
        
        for (int i = 0; i < STEP_NAMES.length; i++) {
            DevelopmentTask task = new DevelopmentTask();
            task.setProjectId(projectId);
            task.setStep(i);
            task.setTaskName(STEP_NAMES[i]);
            task.setTaskDescription(STEP_DESCRIPTIONS[i]);
            task.setStatus(i == 0 ? "IN_PROGRESS" : "PENDING");
            task.setCreateTime(LocalDateTime.now());
            task.setUpdateTime(LocalDateTime.now());
            taskMapper.insert(task);
            tasks.add(task);
        }
        
        return tasks;
    }
}
