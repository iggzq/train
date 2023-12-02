package com.study.train.generator.server;

import com.study.train.generator.util.DbUtil;
import com.study.train.generator.util.Field;
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

    static boolean readOnly = false;
    static String vuePath = "web/src/views/main/";


    static String serviceToPath = "[module]/src/main/java/com/study/train/[module]/";
    static String pomPath = "generator/pom.xml";

    public static void main(String[] args) throws Exception {

        String generatorPath = getGeneratorPath();

        String moduleName = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("moduleName:" + moduleName);
        serviceToPath = serviceToPath.replace("[module]", moduleName);
        new File(serviceToPath).mkdirs();
        System.out.println(serviceToPath);
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("DB_URL:" + connectionURL.getText());
        System.out.println("DB_userId:" + userId.getText());
        System.out.println("DB_password:" + password.getText());

        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();


        //获取表名do_main,实体类名Domain,业务类前缀domain
        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");

        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);

        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("module", moduleName);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("fieldList",fieldList);
        param.put("typeSet",typeSet);
        param.put("tableNameCn",tableNameCn);
        param.put("readOnly", readOnly);
        System.out.println("组装参数：" + param);

//        generateCode(Domain, param, "service", "service");
//        generateCode(Domain, param, "controller", "controller");
//        generateCode(Domain, param, "req", "saveReq");
        genVue(do_main,param);

    }

    private static void generateCode(String Domain, Map<String, Object> param, String packageName, String targetType) throws IOException, TemplateException {
        FreeMarkerUtil.initConfig(targetType + ".ftl");
        String toPath = serviceToPath + packageName + "/";
        new File(toPath).mkdirs();
        String target = targetType.substring(0, 1).toUpperCase() + targetType.substring(1);
        String fileName = toPath + Domain + target + ".java";
        FreeMarkerUtil.generator(fileName, param);
    }

    private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreeMarkerUtil.initConfig("vue.ftl");
        new File(vuePath).mkdirs();
        String fileName = vuePath + do_main + ".vue";
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


