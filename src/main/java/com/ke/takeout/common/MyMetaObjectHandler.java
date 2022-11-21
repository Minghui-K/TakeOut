package com.ke.takeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 用于将每个表的公共字段统一填充
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter("createTime")) metaObject.setValue("createTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        // 从当前线程的变量中获得id
        if (metaObject.hasSetter("createUser")) metaObject.setValue("createUser", BaseContext.getId());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getId());
    }
}
