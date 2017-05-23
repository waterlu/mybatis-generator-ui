package cn.lu.mybatis.generator.codegen;

import org.mybatis.generator.api.dom.java.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lutiehua on 2017/5/23.
 */
public class CreateMethodGenerator extends AbstractControllerMethodGenerator {

    @Override
    public void addControllerElement(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

        FullyQualifiedJavaType parameterType = getParamVOClass();
        FullyQualifiedJavaType returnType = getResultVOClass();
        FullyQualifiedJavaType exceptionType = getBusinessException();

        importedTypes.add(parameterType);
        importedTypes.add(returnType);
        importedTypes.add(exceptionType);


        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(returnType);
        method.setName("create" + getDomainName());
        method.addParameter(new Parameter(parameterType, "param"));
        method.addException(exceptionType);

        method.addAnnotation("@RequestMapping(value = \"\", method = RequestMethod.POST)");
//        context.getCommentGenerator().addGeneralMethodComment(method,
//                introspectedTable);


        topLevelClass.addMethod(method);
        topLevelClass.addImportedTypes(importedTypes);
    }

//    @ApiOperation(value = "创建定期理财产品", httpMethod = "POST", response = CreateFixedProductResultVO.class, notes = "创建定期理财产品")
//    @ApiImplicitParam(value = "产品信息", name = "productParamVO", required = true, dataType = "FixedProductParamVO")
//    @RequestMapping(value = "/fixed", method = RequestMethod.POST)
//    public CreateFixedProductResultVO createFixedProduct(@Valid @RequestBody FixedProductParamVO productParamVO) throws BusinessException {
//        CreateFixedProductResultVO resultVO = new CreateFixedProductResultVO();
//        resultVO.setData("");
//        return resultVO;
//    }
}
