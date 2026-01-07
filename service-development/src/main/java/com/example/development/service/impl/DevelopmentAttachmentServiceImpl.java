package com.example.development.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.development.entity.DevelopmentAttachment;
import com.example.development.mapper.DevelopmentAttachmentMapper;
import com.example.development.service.DevelopmentAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DevelopmentAttachmentServiceImpl extends ServiceImpl<DevelopmentAttachmentMapper, DevelopmentAttachment> implements DevelopmentAttachmentService {

    @Autowired
    private DevelopmentAttachmentMapper attachmentMapper;

    @Override
    public List<DevelopmentAttachment> getAttachmentsByProjectId(Long projectId) {
        LambdaQueryWrapper<DevelopmentAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevelopmentAttachment::getProjectId, projectId)
               .orderByDesc(DevelopmentAttachment::getUploadTime);
        return attachmentMapper.selectList(wrapper);
    }

    @Override
    public List<DevelopmentAttachment> getAttachmentsByTaskId(Long taskId) {
        LambdaQueryWrapper<DevelopmentAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DevelopmentAttachment::getTaskId, taskId)
               .orderByDesc(DevelopmentAttachment::getUploadTime);
        return attachmentMapper.selectList(wrapper);
    }

    @Override
    public boolean deleteAttachment(Long id) {
        return attachmentMapper.deleteById(id) > 0;
    }
}
