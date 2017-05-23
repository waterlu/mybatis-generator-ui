package cn.lu.mybatis.generator.codegen;

import com.zzg.mybatis.generator.plugins.JavaVOModelGeneratorConfiguration;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;

/**
 * Created by lutiehua on 2017/5/23.
 */
public abstract class AbstractControllerMethodGenerator extends AbstractGenerator {

    public AbstractControllerMethodGenerator() {
        super();
    }

    public abstract void addControllerElement(TopLevelClass topLevelClass);

    /**
     * 义务异常类名
     *
     * @return
     */
    public FullyQualifiedJavaType getBusinessException() {
        String fullClassName = "cn.zjhf.kingold.common.exception.BusinessException";
        return new FullyQualifiedJavaType(fullClassName);
    }

    /**
     * 对象名
     *
     * @return
     */
    public String getDomainName() {
        String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return domainName.substring(0, 1).toUpperCase() + domainName.substring(1);
    }

    /**
     * 参数VO类名
     *
     * @return
     */
    public FullyQualifiedJavaType getParamVOClass() {
        // 表对象名
        String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 包名
        JavaVOModelGeneratorConfiguration config = context.getJavaVOModelGeneratorConfiguration();
        String packageName = config.getTargetPackage();

        // 全类名
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName);
        buffer.append(".");
        buffer.append(domainName);
        buffer.append("ParamVO");
        String className = buffer.toString();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(className);
        return type;
    }

    /**
     * 返回VO类名
     *
     * @return
     */
    public FullyQualifiedJavaType getResultVOClass() {
        // 表对象名
        String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 包名
        JavaModelGeneratorConfiguration config = context.getJavaModelGeneratorConfiguration();
        String packageName = config.getTargetPackage();

        // 全类名
        StringBuffer buffer = new StringBuffer();
        buffer.append(packageName);
        buffer.append(".");
        buffer.append(domainName);
        String className = buffer.toString();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(className);
        return type;
    }
}
