package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;

/**
 * Created by lutiehua on 2017/5/20.
 */
public class JavaVOModelGenerator extends AbstractJavaModelGenerator {

    @Override
    public CommentGenerator getCommentGenerator(Context context) {
        return context.getVOCommentGenerator();
    }

    @Override
    public FullyQualifiedJavaType getJavaType(IntrospectedTable introspectedTable) {
        return new FullyQualifiedJavaType(introspectedTable.getVoModalType());
    }

    @Override
    public String getRootClass() {
        return "ParamVO";
    }
}
