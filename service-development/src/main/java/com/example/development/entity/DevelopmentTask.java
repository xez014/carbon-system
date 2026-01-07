package com.example.development.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("development_task")
public class DevelopmentTask {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId; // 项目ID

    private Integer step; // 阶段 (0-立项, 1-PDD设计, 2-第三方审定, 3-主管部门备案, 4-减排量核证, 5-资产签发)

    private String taskName; // 任务名称

    private String taskDescription; // 任务描述

    private String status; // 状态: PENDING, IN_PROGRESS, COMPLETED

    private LocalDateTime dueDate; // 截止日期

    private LocalDateTime completedDate; // 完成日期

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
