package cn.lu.mybatis.generator.codegen;

import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lutiehua on 2017/5/22.
 */
public class ControllerGenerator extends AbstractJavaClientGenerator {

    public ControllerGenerator(boolean requiresXMLGenerator) {
        super(requiresXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        // 类和方法注释生成逻辑控制
        ControllerCommentGenerator commentGenerator = (ControllerCommentGenerator)context.getCommentGenerator();

        // 表对象名
        String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 包名
        JavaClientGeneratorConfiguration config = context.getJavaClientGeneratorConfiguration();
        String packageName = config.getTargetPackage();

        // 全类名
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName);
        buffer.append(".");
        buffer.append(domainName);
        buffer.append("Controller");
        String className = buffer.toString();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(className);

        // 类
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        // 类注释
        commentGenerator.addJavaFileComment(topLevelClass);
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        addCreateMethod(topLevelClass);
//        addDeleteByPrimaryKeyMethod(interfaze);
//        addInsertMethod(topLevelClass);
//        addSelectByPrimaryKeyMethod(interfaze);
//        addSelectAllMethod(interfaze);
//        addUpdateByPrimaryKeyMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<>();
        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    protected void addCreateMethod(TopLevelClass topLevelClass) {
        AbstractControllerMethodGenerator methodGenerator = new CreateMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, topLevelClass);
    }

    protected void initializeAndExecuteGenerator(AbstractControllerMethodGenerator methodGenerator, TopLevelClass topLevelClass) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addControllerElement(topLevelClass);
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return null;
    }
}
