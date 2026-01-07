package com.example.development.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.Result;
import com.example.development.entity.DevelopmentProject;
import com.example.development.service.DevelopmentProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/development/projects")
public class DevelopmentProjectController {

    @Autowired
    private DevelopmentProjectService projectService;

    @GetMapping
    public Result<Page<DevelopmentProject>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String methodology,
            @RequestParam(required = false) Long ownerId) {
        
        Page<DevelopmentProject> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DevelopmentProject> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(DevelopmentProject::getStatus, status);
        }
        if (methodology != null && !methodology.isEmpty()) {
            queryWrapper.like(DevelopmentProject::getMethodology, methodology);
        }
        if (ownerId != null) {
            queryWrapper.eq(DevelopmentProject::getOwnerId, ownerId);
        }
        
        queryWrapper.orderByDesc(DevelopmentProject::getCreateTime);
        
        return Result.success(projectService.page(pageParam, queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<DevelopmentProject> getById(@PathVariable Long id) {
        DevelopmentProject project = projectService.getById(id);
        if (project != null) {
            return Result.success(project);
        }
        return Result.error("项目不存在");
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody DevelopmentProject project) {
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());
        if (project.getStatus() == null) {
            project.setStatus("PLANNED");
        }
        if (project.getCurrentStep() == null) {
            project.setCurrentStep(0);
            project.setCurrentTask("立项");
        }
        return Result.success(projectService.save(project));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody DevelopmentProject project) {
        project.setId(id);
        project.setUpdateTime(LocalDateTime.now());
        return Result.success(projectService.updateById(project));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(projectService.removeById(id));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<DevelopmentProject> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(DevelopmentProject::getStatus, "PLANNED");
        long plannedCount = projectService.count(wrapper);
        stats.put("plannedCount", plannedCount);
        
        wrapper.clear();
        wrapper.eq(DevelopmentProject::getStatus, "REGISTERED");
        long registeredCount = projectService.count(wrapper);
        stats.put("registeredCount", registeredCount);
        
        wrapper.clear();
        wrapper.eq(DevelopmentProject::getStatus, "VERIFIED");
        long verifiedCount = projectService.count(wrapper);
        stats.put("verifiedCount", verifiedCount);
        
        wrapper.clear();
        wrapper.eq(DevelopmentProject::getStatus, "ISSUED");
        long issuedCount = projectService.count(wrapper);
        stats.put("issuedCount", issuedCount);
        
        stats.put("totalCount", plannedCount + registeredCount + verifiedCount + issuedCount);
        
        return Result.success(stats);
    }

    @GetMapping("/by-step")
    public Result<List<Map<String, Object>>> getProjectsByStep() {
        List<Map<String, Object>> result = projectService.getProjectsGroupedByStep();
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        DevelopmentProject project = projectService.getById(id);
        if (project != null) {
            project.setStatus(status);
            project.setUpdateTime(LocalDateTime.now());
            return Result.success(projectService.updateById(project));
        }
        return Result.error("项目不存在");
    }

    @PutMapping("/{id}/step")
    public Result<Boolean> updateStep(
            @PathVariable Long id, 
            @RequestParam Integer step) {
        DevelopmentProject project = projectService.getById(id);
        if (project != null) {
            String[] taskNames = {"立项", "PDD设计", "第三方审定", "主管部门备案", "减排量核证", "资产签发"};
            project.setCurrentStep(step);
            if (step < taskNames.length) {
                project.setCurrentTask(taskNames[step]);
            }
            project.setUpdateTime(LocalDateTime.now());
            return Result.success(projectService.updateById(project));
        }
        return Result.error("项目不存在");
    }
}
