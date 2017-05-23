package cn.lu.mybatis.generator.codegen;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by lutiehua on 2017/5/23.
 */
public class ControllerCommentGenerator implements CommentGenerator {

    @Override
    public void addConfigurationProperties(Properties properties) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    /**
     * 类注释
     *
     * @param topLevelClass
     *            the top level class
     * @param introspectedTable
     */
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String author = introspectedTable.getContext().getProperty("author");
        String time = dateFormat.format(new Date());
        String info = introspectedTable.getFullyQualifiedTable().getRemark();
        String mapping = introspectedTable.getTableConfiguration().getTableName().toLowerCase();
        if (!mapping.endsWith("s")) {
            mapping += "s";
        }
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + topLevelClass.getType().getShortName());
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * Created by " + author + " on " + time + ".");
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" */");
        topLevelClass.addAnnotation("@RestController");
        topLevelClass.addAnnotation("@RequestMapping(\"/" + mapping + "\")");
        topLevelClass.addAnnotation("@Api(value = \"" + info + "\", description = \"" + info + "\", basePath = \"/" + mapping + "\")");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {

    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

    }

    /**
     * 引入包
     *
     * @param compilationUnit
     */
    public void addJavaFileComment(CompilationUnit compilationUnit) {
//        compilationUnit.addImportedType(new FullyQualifiedJavaType("cn.zjhf.kingold.common.param.ParamVO"));
        compilationUnit.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
        compilationUnit.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
//        compilationUnit.addImportedType(new FullyQualifiedJavaType("cn.zjhf.kingold.common.exception.BusinessException"));
    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    @Override
    public void addRootComment(XmlElement rootElement) {

    }
}
