/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zzg.mybatis.generator.plugins;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 此插件使用数据库表中列的注释来生成Java Model中属性的注释
 *
 * @author Owen Zou
 * 
 */
public class DbRemarksCommentGenerator implements CommentGenerator {


    private Properties properties;
    private boolean columnRemarks;
    private boolean isAnnotations;
    private boolean isApiDoc;

    private final static String DELETE_FLAG = "delete_flag";

    private final static String CREATE_TIME = "create_time";

    private final static String UPDATE_TIME = "update_time";

    private final static String STATUS = "status";

    public DbRemarksCommentGenerator() {
        super();
        properties = new Properties();
    }


    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
        if (isAnnotations) {
            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Table"));
            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Id"));
            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Column"));
            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.GeneratedValue"));
            compilationUnit.addImportedType(new FullyQualifiedJavaType("org.hibernate.validator.constraints.NotEmpty"));
        }

    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     */
    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
        return;
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        columnRemarks = isTrue(properties
                .getProperty("columnRemarks"));
        isAnnotations = isTrue(properties
                .getProperty("annotations"));
        isApiDoc = isTrue(properties
                .getProperty("apiDoc"));
    }

    public void addClassComment(InnerClass innerClass,
            IntrospectedTable introspectedTable) {
    }

    public void addModelClassComment(TopLevelClass topLevelClass,
                                IntrospectedTable introspectedTable) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String author = introspectedTable.getContext().getProperty("author");
        String time = dateFormat.format(new Date());
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable().getRemark());
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * Created by " + author + " on " + time + ".");
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" */");
        if (isAnnotations) {
//            topLevelClass.addAnnotation("@Table(name=\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\")");
        }
        if (isApiDoc) {
            topLevelClass.addAnnotation("@ApiModel(description = \"" + introspectedTable.getFullyQualifiedTable().getRemark() + "\")");
        }
    }

    public void addEnumComment(InnerEnum innerEnum,
            IntrospectedTable introspectedTable) {
    }

    public void addFieldComment(Field field,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            field.addJavaDocLine("/**");
            StringBuilder sb = new StringBuilder();
            sb.append(" * ");
            sb.append(introspectedColumn.getRemarks());
            field.addJavaDocLine(sb.toString());
            field.addJavaDocLine(" */");
        }

        if (isAnnotations) {
//            boolean isId = false;
//            for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
//                if (introspectedColumn == column) {
//                    isId = true;
//                    field.addAnnotation("@Id");
//                    field.addAnnotation("@GeneratedValue");
//                    break;
//                }
//            }
//            if (!introspectedColumn.isNullable() && !isId){
//                field.addAnnotation("@NotEmpty");
//            }
//            if (introspectedColumn.isIdentity()) {
//                if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
//                    field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
//                } else {
//                    field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
//                }
//            } else if (introspectedColumn.isSequenceColumn()) {
//                field.addAnnotation("@SequenceGenerator(name=\"\",sequenceName=\"" + introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement() + "\")");
//            }

            String columnName = introspectedColumn.getActualColumnName();
            int jdbcType = introspectedColumn.getJdbcType();
            if (jdbcType == Types.CHAR) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotBlank(message = \"" + columnName + "不能为空\")");
                }
                int length = introspectedColumn.getLength();
                String size = String.format("@Size(min = %d, max = %d)", length, length);
                field.addAnnotation(size);
            } else if (jdbcType == Types.VARCHAR) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotBlank(message = \"" + columnName + "不能为空\")");
                }
                int length = introspectedColumn.getLength();
                String size = String.format("@Size(max = %d)", length, length);
                field.addAnnotation(size);
            } else if (jdbcType == Types.DECIMAL) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotNull(message = \"" + columnName + "不能为空\")");
                }
                int length = introspectedColumn.getLength();
                int scale = introspectedColumn.getScale();
            } else if (jdbcType == Types.TINYINT) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotNull(message = \"" + columnName + "不能为空\")");
                }
                String min = String.format("@Min(value = %d)", Byte.MIN_VALUE);
                String max = String.format("@Max(value = %d)", Byte.MAX_VALUE);
                field.addAnnotation(min);
                field.addAnnotation(max);
            } else if (jdbcType == Types.SMALLINT) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotNull(message = \"" + columnName + "不能为空\")");
                }
                String min = String.format("@Min(value = %d)", Short.MIN_VALUE);
                String max = String.format("@Max(value = %d)", Short.MAX_VALUE);
                field.addAnnotation(min);
                field.addAnnotation(max);
            } else if (jdbcType == Types.INTEGER) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotNull(message = \"" + columnName + "不能为空\")");
                }
            } else if (jdbcType == Types.BIGINT) {
                if (!introspectedColumn.isNullable()) {
                    field.addAnnotation("@NotNull(message = \"" + columnName + "不能为空\")");
                }
            }
        }

        //@ApiModelProperty(value = "产品类型", required = true,  allowableValues = "FIXI-定期理财，PRIF-私募基金", example = "FIXI")
        if (isApiDoc) {
            String columnName = introspectedColumn.getActualColumnName();
            if (columnName.equalsIgnoreCase(DELETE_FLAG)) {
                if (introspectedColumn.isNullable()) {
                    field.addAnnotation("@ApiModelProperty(value = \"删除标记\", allowableValues = \"0-未删除，1-已删除\", example = \"0\")");
                } else {
                    field.addAnnotation("@ApiModelProperty(value = \"删除标记\", required = true, allowableValues = \"0-未删除，1-已删除\", example = \"0\")");
                }
            } else if (columnName.equalsIgnoreCase(CREATE_TIME)) {
                if (introspectedColumn.isNullable()) {
                    field.addAnnotation("@ApiModelProperty(value = \"记录创建时间\")");
                } else {
                    field.addAnnotation("@ApiModelProperty(value = \"记录创建时间\", required = true)");
                }
            } else if (columnName.equalsIgnoreCase(UPDATE_TIME)) {
                if (introspectedColumn.isNullable()) {
                    field.addAnnotation("@ApiModelProperty(value = \"最后更新时间\")");
                } else {
                    field.addAnnotation("@ApiModelProperty(value = \"最后更新时间\", required = true)");
                }
            } else {
                if (columnName.contains(STATUS)) {
                    if (introspectedColumn.isNullable()) {
                        field.addAnnotation("@ApiModelProperty(value = \"" + introspectedColumn.getRemarks() + "\", required = true, allowableValues = \"1-状态1 2-状态2 3-状态3\")");
                    } else {
                        field.addAnnotation("@ApiModelProperty(value = \"" + introspectedColumn.getRemarks() + "\", allowableValues = \"1-状态1 2-状态2 3-状态3\")");
                    }
                } else {
                    StringBuffer buffer = new StringBuffer("@ApiModelProperty(value = \"" + introspectedColumn.getRemarks() + "\"");
                    if (!introspectedColumn.isNullable()) {
                        buffer.append(", required = true");
                    }

                    String defaultValue = introspectedColumn.getDefaultValue();
                    if (StringUtils.isNotEmpty(defaultValue)) {
                        buffer.append(", example = \"" + defaultValue + "\"");
                    }

                    buffer.append(")");
                    field.addAnnotation(buffer.toString());
                }
            }
        }
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    public void addGeneralMethodComment(Method method,
            IntrospectedTable introspectedTable) {
    }

    public void addGetterComment(Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
    }

    public void addSetterComment(Method method,
            IntrospectedTable introspectedTable,
            IntrospectedColumn introspectedColumn) {
    }

    public void addClassComment(InnerClass innerClass,
            IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }
}
