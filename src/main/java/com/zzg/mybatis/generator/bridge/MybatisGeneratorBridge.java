package com.zzg.mybatis.generator.bridge;

import cn.lu.mybatis.generator.codegen.ControllerCommentGenerator;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.plugins.DbRemarksCommentGenerator;
import com.zzg.mybatis.generator.plugins.JavaVOModelGeneratorConfiguration;
import com.zzg.mybatis.generator.plugins.VoRemarksCommenctGenerator;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The bridge between GUI and the mybatis generator. All the operation to  mybatis generator should proceed through this
 * class
 * <p>
 * Created by Owen on 6/30/16.
 */
public class MybatisGeneratorBridge {

	private static final Logger _LOG = LoggerFactory.getLogger(MybatisGeneratorBridge.class);

    private GeneratorConfig generatorConfig;

    private DatabaseConfig selectedDatabaseConfig;

    private ProgressCallback progressCallback;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    public MybatisGeneratorBridge() {
        init();
    }

    private void init() {
        Configuration config = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
	    context.addProperty("javaFileEncoding", "UTF-8");
	    config.addContext(context);
    }

    public void setGeneratorConfig(GeneratorConfig generatorConfig) {
        this.generatorConfig = generatorConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.selectedDatabaseConfig = databaseConfig;
    }

    public void generate() throws Exception {
        Configuration config = new Configuration();
        String connectorLibPath = ConfigHelper.findConnectorLibPath(selectedDatabaseConfig.getDbType());
		_LOG.info("connectorLibPath: {}", connectorLibPath);
        config.addClasspathEntry(connectorLibPath);
        Context context = new Context(ModelType.CONDITIONAL);
//        config.addContext(context);
        // Table config
        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(generatorConfig.getTableName());
        tableConfig.setDomainObjectName(generatorConfig.getDomainObjectName());

        // 不要那些多余的Example
        tableConfig.setCountByExampleStatementEnabled(false);
        tableConfig.setUpdateByExampleStatementEnabled(false);
        tableConfig.setDeleteByExampleStatementEnabled(false);
        tableConfig.setSelectByExampleStatementEnabled(false);

        //添加GeneratedKey主键生成
		if (StringUtils.isNoneEmpty(generatorConfig.getGenerateKeys())) {
			tableConfig.setGeneratedKey(new GeneratedKey(generatorConfig.getGenerateKeys(), selectedDatabaseConfig.getDbType(), true, null));
		}

        if (generatorConfig.getMapperName() != null) {
            tableConfig.setMapperName(generatorConfig.getMapperName());
        }
        // add ignore columns
        if (ignoredColumns != null) {
            ignoredColumns.stream().forEach(ignoredColumn -> {
                tableConfig.addIgnoredColumn(ignoredColumn);
            });
        }
        if (columnOverrides != null) {
            columnOverrides.stream().forEach(columnOverride -> {
                tableConfig.addColumnOverride(columnOverride);
            });
        }
        if (generatorConfig.isUseActualColumnNames()) {
			tableConfig.addProperty("useActualColumnNames", "true");
        }
        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setDriverClass(DbType.valueOf(selectedDatabaseConfig.getDbType()).getDriverClass());
        jdbcConfig.setConnectionURL(DbUtil.getConnectionUrlWithSchema(selectedDatabaseConfig));
        jdbcConfig.setUserId(selectedDatabaseConfig.getUsername());
        jdbcConfig.setPassword(selectedDatabaseConfig.getPassword());
        // java model
        JavaModelGeneratorConfiguration modelConfig = new JavaModelGeneratorConfiguration();
        modelConfig.setTargetPackage(generatorConfig.getModelPackage());
        modelConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getModelPackageTargetFolder());
        // Mapper config
        SqlMapGeneratorConfiguration mapperConfig = new SqlMapGeneratorConfiguration();
        mapperConfig.setTargetPackage(generatorConfig.getMappingXMLPackage());
        mapperConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getMappingXMLTargetFolder());
        // DAO
        JavaClientGeneratorConfiguration daoConfig = new JavaClientGeneratorConfiguration();
        daoConfig.setConfigurationType("XMLMAPPER");
        daoConfig.setTargetPackage(generatorConfig.getDaoPackage());
        daoConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getDaoTargetFolder());
        // Java VO Model
        JavaVOModelGeneratorConfiguration javaVOModelConfig = new JavaVOModelGeneratorConfiguration();
        javaVOModelConfig.setConfigurationType("JAVA_VO_MODEL");
        javaVOModelConfig.setTargetPackage(generatorConfig.getVoModelPackage());
        javaVOModelConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getVoModelPackageTargetFolder());

        JavaTypeResolverConfiguration javaTypeConfiguration = new JavaTypeResolverConfiguration();
        javaTypeConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.MyJavaTypeResolverImpl");

        context.setId("myid");
        context.addTableConfiguration(tableConfig);
        context.setJdbcConnectionConfiguration(jdbcConfig);
        context.setJavaModelGeneratorConfiguration(modelConfig);
        // 增加VO Model对象
        context.setJavaVOModelGeneratorConfiguration(javaVOModelConfig);
        context.setSqlMapGeneratorConfiguration(mapperConfig);
        context.setJavaClientGeneratorConfiguration(daoConfig);
        context.setJavaTypeResolverConfiguration(javaTypeConfiguration);
//        context.setJavaControllerGeneratorConfiguration(javaControllerConfig);

        // Comment
        CommentGeneratorConfiguration commentConfig = new CommentGeneratorConfiguration();
        commentConfig.setConfigurationType(DbRemarksCommentGenerator.class.getName());
        if (generatorConfig.isComment()) {
            commentConfig.addProperty("columnRemarks", "true");
        }
        if (generatorConfig.isAnnotation()) {
            commentConfig.addProperty("annotations", "true");
        }
        if (generatorConfig.isApiDoc()) {
            commentConfig.addProperty("apiDoc", "true");
        }
        context.setCommentGeneratorConfiguration(commentConfig);

        // Java VO Model Comment
        CommentGeneratorConfiguration voCommentConfig = new CommentGeneratorConfiguration();
        voCommentConfig.setConfigurationType(VoRemarksCommenctGenerator.class.getName());
        if (generatorConfig.isComment()) {
            voCommentConfig.addProperty("columnRemarks", "true");
        }
        if (generatorConfig.isAnnotation()) {
            voCommentConfig.addProperty("annotations", "true");
        }
        if (generatorConfig.isApiDoc()) {
            voCommentConfig.addProperty("apiDoc", "true");
        }
        context.setCommentGeneratorConfiguration(voCommentConfig);

        
        //实体添加序列化
        PluginConfiguration serializablePluginConfiguration = new PluginConfiguration();
        serializablePluginConfiguration.addProperty("type", "org.mybatis.generator.plugins.SerializablePlugin");
        serializablePluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(serializablePluginConfiguration);
        
        // limit/offset插件
        if (generatorConfig.isOffsetLimit()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("type", "com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
            pluginConfiguration.setConfigurationType("com.zzg.mybatis.generator.plugins.MySQLLimitPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        context.setTargetRuntime("MyBatis3");

        context.addProperty("author", generatorConfig.getAuthor());


        /**
         * JAVA文件
         */
        Context context2 = new Context(ModelType.CONDITIONAL);

        // Java Controller
        JavaClientGeneratorConfiguration javaControllerConfig = new JavaClientGeneratorConfiguration();
        javaControllerConfig.setConfigurationType("CONTROLLER");
        javaControllerConfig.setTargetPackage(generatorConfig.getControllerPackage());
        javaControllerConfig.setTargetProject(generatorConfig.getProjectFolder() + "/" + generatorConfig.getControllerPackageTargetFolder());

        // Table config
        TableConfiguration tableConfig2 = new TableConfiguration(context2);
        tableConfig2.setTableName(generatorConfig.getTableName());
        tableConfig2.setDomainObjectName(generatorConfig.getDomainObjectName());

        // 注释
        CommentGeneratorConfiguration commentConfig2 = new CommentGeneratorConfiguration();
        commentConfig2.setConfigurationType(ControllerCommentGenerator.class.getName());

        context2.setId("JavaController");
        context2.addTableConfiguration(tableConfig2);
        context2.setJdbcConnectionConfiguration(jdbcConfig);
        context2.setJavaClientGeneratorConfiguration(daoConfig);
        context2.setJavaModelGeneratorConfiguration(modelConfig);
        context2.setJavaVOModelGeneratorConfiguration(javaVOModelConfig);
        context2.setJavaTypeResolverConfiguration(javaTypeConfiguration);
        context2.setJavaClientGeneratorConfiguration(javaControllerConfig);
        context2.setCommentGeneratorConfiguration(commentConfig2);
        context2.setTargetRuntime("Java");
        context2.addProperty("author", generatorConfig.getAuthor());

        config.addContext(context);
        config.addContext(context2);

        List<String> warnings = new ArrayList<>();
        Set<String> fullyqualifiedTables = new HashSet<>();
        Set<String> contexts = new HashSet<>();
        ShellCallback shellCallback = new DefaultShellCallback(true); // override=true
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
        myBatisGenerator.generate(progressCallback, contexts, fullyqualifiedTables);
    }


    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public void setIgnoredColumns(List<IgnoredColumn> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }
}
