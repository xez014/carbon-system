package com.example.development.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.development.entity.DevelopmentProject;

import java.util.List;
import java.util.Map;

public interface DevelopmentProjectService extends IService<DevelopmentProject> {
    List<Map<String, Object>> getProjectsGroupedByStep();
}
