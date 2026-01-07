package com.example.development.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.Result;
import com.example.development.entity.DevelopmentProject;
import com.example.development.entity.DevelopmentTask;
import com.example.development.entity.DevelopmentAttachment;
import com.example.development.service.DevelopmentProjectService;
import com.example.development.service.DevelopmentTaskService;
import com.example.development.service.DevelopmentAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/development/workbench")
public class WorkbenchController {

    @Autowired
    private DevelopmentProjectService projectService;

    @Autowired
    private DevelopmentTaskService taskService;

    @Autowired
    private DevelopmentAttachmentService attachmentService;

    @GetMapping("/projects/active")
    public Result<List<DevelopmentProject>> getActiveProjects(
            @RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<DevelopmentProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(DevelopmentProject::getStatus, "ISSUED")
               .ne(DevelopmentProject::getStatus, "CANCELLED");
        if (userId != null) {
            wrapper.eq(DevelopmentProject::getOwnerId, userId);
        }
        wrapper.orderByDesc(DevelopmentProject::getUpdateTime);
        return Result.success(projectService.list(wrapper));
    }

    @GetMapping("/projects/{id}")
    public Result<DevelopmentProject> getProjectDetail(@PathVariable Long id) {
        DevelopmentProject project = projectService.getById(id);
        if (project != null) {
            return Result.success(project);
        }
        return Result.error("项目不存在");
    }

    @GetMapping("/projects/{id}/tasks")
    public Result<List<DevelopmentTask>> getProjectTasks(@PathVariable Long id) {
        List<DevelopmentTask> tasks = taskService.getTasksByProjectId(id);
        return Result.success(tasks);
    }

    @GetMapping("/projects/{id}/attachments")
    public Result<List<DevelopmentAttachment>> getProjectAttachments(@PathVariable Long id) {
        List<DevelopmentAttachment> attachments = attachmentService.getAttachmentsByProjectId(id);
        return Result.success(attachments);
    }

    @PutMapping("/projects/{id}")
    public Result<Boolean> updateProject(
            @PathVariable Long id, 
            @RequestBody DevelopmentProject project) {
        project.setId(id);
        project.setUpdateTime(LocalDateTime.now());
        return Result.success(projectService.updateById(project));
    }

    @PutMapping("/projects/{id}/step")
    public Result<Boolean> updateProjectStep(
            @PathVariable Long id, 
            @RequestParam Integer step) {
        DevelopmentProject project = projectService.getById(id);
        if (project != null) {
            project.setCurrentStep(step);
            project.setUpdateTime(LocalDateTime.now());
            
            String[] taskNames = {"立项", "PDD设计", "第三方审定", "主管部门备案", "减排量核证", "资产签发"};
            if (step < taskNames.length) {
                project.setCurrentTask(taskNames[step]);
            }
            
            return Result.success(projectService.updateById(project));
        }
        return Result.error("项目不存在");
    }

    @PutMapping("/tasks/{taskId}/status")
    public Result<Boolean> updateTaskStatus(
            @PathVariable Long taskId, 
            @RequestParam String status) {
        return Result.success(taskService.updateTaskStatus(taskId, status));
    }

    @PostMapping("/attachments")
    public Result<Long> uploadAttachment(@RequestBody DevelopmentAttachment attachment) {
        attachment.setUploadTime(LocalDateTime.now());
        attachment.setCreateTime(LocalDateTime.now());
        attachmentService.save(attachment);
        return Result.success(attachment.getId());
    }

    @DeleteMapping("/attachments/{id}")
    public Result<Boolean> deleteAttachment(@PathVariable Long id) {
        return Result.success(attachmentService.deleteAttachment(id));
    }

    @GetMapping("/summary/{projectId}")
    public Result<Map<String, Object>> getProjectSummary(@PathVariable Long projectId) {
        Map<String, Object> summary = new HashMap<>();
        
        DevelopmentProject project = projectService.getById(projectId);
        if (project == null) {
            return Result.error("项目不存在");
        }
        
        List<DevelopmentTask> tasks = taskService.getTasksByProjectId(projectId);
        List<DevelopmentAttachment> attachments = attachmentService.getAttachmentsByProjectId(projectId);
        
        summary.put("project", project);
        summary.put("totalTasks", tasks.size());
        summary.put("completedTasks", tasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count());
        summary.put("attachments", attachments.size());
        summary.put("progress", tasks.isEmpty() ? 0 : (int)((double)summary.get("completedTasks") / tasks.size() * 100));
        
        return Result.success(summary);
    }

    @PostMapping("/projects/{id}/initialize")
    public Result<Boolean> initializeProjectTasks(@PathVariable Long id) {
        DevelopmentProject project = projectService.getById(id);
        if (project == null) {
            return Result.error("项目不存在");
        }
        
        List<DevelopmentTask> existingTasks = taskService.getTasksByProjectId(id);
        if (!existingTasks.isEmpty()) {
            return Result.error("项目任务已初始化");
        }
        
        taskService.initDefaultTasks(id);
        return Result.success(true);
    }
}
