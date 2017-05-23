package cn.lu.mybatis.generator.codegen;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.AbstractGenerator;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.rules.ConditionalModelRules;
import org.mybatis.generator.internal.rules.FlatModelRules;
import org.mybatis.generator.internal.rules.HierarchicalModelRules;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lutiehua on 2017/5/23.
 */
public class IntrospectedTableJavaImpl extends IntrospectedTable {

    /** The client generators. */
    protected List<AbstractJavaGenerator> clientGenerators;

    public IntrospectedTableJavaImpl() {
        super(TargetRuntime.JAVA);
        clientGenerators = new ArrayList<AbstractJavaGenerator>();
    }

    /**
     * Instantiates a new introspected table.
     *
     * @param targetRuntime the target runtime
     */
    public IntrospectedTableJavaImpl(TargetRuntime targetRuntime) {
        super(targetRuntime);
        clientGenerators = new ArrayList<AbstractJavaGenerator>();
    }

    @Override
    public void initialize() {
        calculateJavaClientAttributes();
        calculateModelAttributes();
        calculateXmlAttributes();

        if (tableConfiguration.getModelType() == ModelType.HIERARCHICAL) {
            rules = new HierarchicalModelRules(this);
        } else if (tableConfiguration.getModelType() == ModelType.FLAT) {
            rules = new FlatModelRules(this);
        } else {
            rules = new ConditionalModelRules(this);
        }

//        context.getPlugins().initialized(this);
//        super.initialize();
    }

    @Override
    public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
        AbstractJavaClientGenerator javaGenerator = createJavaClientGenerator();
        initializeAbstractGenerator(javaGenerator, warnings);
        clientGenerators.add(javaGenerator);
    }

    /**
     * 创建Java类生成器
     *
     * @return
     */
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        if (context.getJavaClientGeneratorConfiguration() == null) {
            return null;
        }

        String type = context.getJavaClientGeneratorConfiguration()
                .getConfigurationType();

        AbstractJavaClientGenerator javaGenerator = null;
        if ("CONTROLLER".equalsIgnoreCase(type)) { //$NON-NLS-1$
            javaGenerator = new ControllerGenerator(false);
        } else if ("SERVICE".equalsIgnoreCase(type)) { //$NON-NLS-1$

        } else if ("SERVICE_IMPL".equalsIgnoreCase(type)) { //$NON-NLS-1$

        } else {
            javaGenerator = (AbstractJavaClientGenerator) ObjectFactory
                    .createInternalObject(type);
        }

        return javaGenerator;
    }

    /**
     * 设置Java类生成器的属性
     *
     * @param abstractGenerator
     * @param warnings
     */
    protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings) {
        if (abstractGenerator == null) {
            return;
        }

        abstractGenerator.setContext(context);
        abstractGenerator.setIntrospectedTable(this);
        abstractGenerator.setWarnings(warnings);
    }

    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> list = new ArrayList<GeneratedJavaFile>();
        for (AbstractJavaGenerator javaGenerator : clientGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        context.getJavaClientGeneratorConfiguration().getTargetProject(),
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                        context.getJavaFormatter());
                list.add(gjf);
            }
        }

        return list;
    }

    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return new ArrayList<>();
    }

    @Override
    public boolean isJava5Targeted() {
        return false;
    }

    @Override
    public int getGenerationSteps() {
        return 0;
    }

    @Override
    public boolean requiresXMLGenerator() {
        return false;
    }
}
