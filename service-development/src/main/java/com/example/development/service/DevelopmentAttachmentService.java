package com.example.development.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.development.entity.DevelopmentAttachment;

import java.util.List;

public interface DevelopmentAttachmentService extends IService<DevelopmentAttachment> {

    List<DevelopmentAttachment> getAttachmentsByProjectId(Long projectId);

    List<DevelopmentAttachment> getAttachmentsByTaskId(Long taskId);

    boolean deleteAttachment(Long id);
}
