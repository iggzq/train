package com.study.train.generator.server;

import com.study.train.generator.util.DbUtil;
import com.study.train.generator.util.entity.Field;
import com.study.train.generator.util.FreeMarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerate {

    static boolean readOnly = true;
    static String vuePath = "admin/src/views/main/";


    static String serviceToPath = "[module]/src/main/java/com/study/train/[module]/";
    static String pomPath = "generator/pom.xml";

    static String moduleName = "";

    public static void main(String[] args) throws Exception {

        // 1. 获取generator-config-{}.xml里的{}值
        String generatorPath = getGeneratorPath();
        // 2. 将1获取到的{}值拼接到serviceToPath中，形成完整待生成代码的模块路径
        moduleName = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("moduleName:" + moduleName);
        serviceToPath = serviceToPath.replace("[module]", moduleName);
        new File(serviceToPath).mkdirs();
        System.out.println(serviceToPath);

        // 3.读取加载generator-config-{}.xml文件
        Document document = new SAXReader().read("generator/" + generatorPath);

        // 4.获取generator-config-{}.xml文件里的tableName和domainObjectName两个表参数
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // 5.获取generator-config-{}.xml里的connectionURL、userId、password三个数据库参数
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("DB_URL:" + connectionURL.getText());
        System.out.println("DB_userId:" + userId.getText());
        System.out.println("DB_password:" + password.getText());

        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();


        // 6.获取表名do_main,实体类名Domain,业务类前缀domain
        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");

        // 7.获取表注释,字段信息，以及字段类型集合
        String tableNameComment = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);

        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("module", moduleName);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        param.put("tableNameComment", tableNameComment);
        param.put("readOnly", readOnly);
        System.out.println("组装参数：" + param);

        // 8.生成代码
        generateCode(Domain, param, "service", "service");
        generateCode(Domain, param, "controller/admin", "adminController");
        generateCode(Domain, param, "req", "saveReq");
        generateCode(Domain, param, "req", "queryReq");
        generateCode(Domain, param, "resp", "queryResp");

        genVue(do_main, param);

    }

    private static void generateCode(String Domain, Map<String, Object> param, String packageName, String targetType) throws IOException, TemplateException {
        FreeMarkerUtil.initConfig(targetType + ".ftl");
        String toPath = serviceToPath + packageName + "/";
        new File(toPath).mkdirs();
        String target = targetType.substring(0, 1).toUpperCase() + targetType.substring(1);
        String fileName = toPath + Domain + target + ".java";
        System.out.println("开始生成" + toPath + Domain + target + ".java");
        FreeMarkerUtil.generator(fileName, param);
    }

    private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreeMarkerUtil.initConfig("vue.ftl");
        new File(vuePath).mkdirs();
        String fileName = vuePath + moduleName + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreeMarkerUtil.generator(fileName, param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

    /**
     * 获取所有的Java类型，使用Set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }
}


