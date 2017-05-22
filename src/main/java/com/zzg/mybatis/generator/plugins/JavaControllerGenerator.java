package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;

import java.util.List;

/**
 * Created by lutiehua on 2017/5/22.
 */
public class JavaControllerGenerator extends AbstractJavaClientGenerator {

    public JavaControllerGenerator(boolean requiresXMLGenerator) {
        super(requiresXMLGenerator);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        return null;
    }

    @Override
    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return null;
    }
}
