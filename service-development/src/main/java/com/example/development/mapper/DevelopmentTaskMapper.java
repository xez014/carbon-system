package com.example.development.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.development.entity.DevelopmentTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DevelopmentTaskMapper extends BaseMapper<DevelopmentTask> {
}
