package com.example.development.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("development_attachment")
public class DevelopmentAttachment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId; // 项目ID

    private Long taskId; // 关联任务ID (可选)

    private String fileName; // 文件名

    private String filePath; // 文件路径

    private String fileType; // 文件类型

    private Long fileSize; // 文件大小 (字节)

    private String description; // 文件描述

    private LocalDateTime uploadTime;

    private LocalDateTime createTime;
}
