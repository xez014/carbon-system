package com.example.development.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.development.entity.DevelopmentProject;
import com.example.development.mapper.DevelopmentProjectMapper;
import com.example.development.service.DevelopmentProjectService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DevelopmentProjectServiceImpl extends ServiceImpl<DevelopmentProjectMapper, DevelopmentProject> implements DevelopmentProjectService {

    @Override
    public List<Map<String, Object>> getProjectsGroupedByStep() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String[] stepNames = {"立项", "PDD设计", "第三方审定", "主管部门备案", "减排量核证", "资产签发"};
        String[] stepDescriptions = {
            "提交项目建议书，明确基准线情景和项目边界",
            "依据方法学编写完整的 PDD 文件",
            "联系有资质的 DOE 进行现场审定",
            "准备完整材料向主管部门备案",
            "建立监测计划，定期核算实际减排量",
            "完成核证后，提交 CCER 上市申请"
        };
        
        for (int i = 0; i < stepNames.length; i++) {
            LambdaQueryWrapper<DevelopmentProject> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DevelopmentProject::getCurrentStep, i)
                   .ne(DevelopmentProject::getStatus, "ISSUED")
                   .ne(DevelopmentProject::getStatus, "CANCELLED");
            List<DevelopmentProject> projects = this.list(wrapper);
            
            Map<String, Object> stepInfo = new LinkedHashMap<>();
            stepInfo.put("step", i);
            stepInfo.put("stepName", stepNames[i]);
            stepInfo.put("description", stepDescriptions[i]);
            stepInfo.put("count", projects.size());
            stepInfo.put("projects", projects);
            
            result.add(stepInfo);
        }
        
        return result;
    }
}
