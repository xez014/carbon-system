package com.example.development.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("development_project")
public class DevelopmentProject {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name; // 项目名称

    private String methodology; // 方法学

    private String status; // 状态: PLANNED, REGISTERED, VERIFIED, ISSUED

    private Long ownerId; // 业主ID

    private String location; // 项目地点

    private Double estimatedEmissionReduction; // 预计减排量

    private Integer currentStep; // 当前阶段 (0-立项, 1-PDD设计, 2-第三方审定, 3-主管部门备案, 4-减排量核证, 5-资产签发)

    private String currentTask; // 当前任务描述

    private String baselineDescription; // 基准线描述

    private Double actualEmissionReduction; // 实际减排量

    private LocalDateTime startDate; // 项目开始日期

    private LocalDateTime expectedEndDate; // 预计结束日期

    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
