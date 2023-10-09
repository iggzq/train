package com.study.train.generator.server;

import com.study.train.generator.util.FreeMarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerate {

    static String serviceToPath = "[module]/src/main/java/com/study/train/[module]/";
    static String pomPath = "generator/pom.xml";

    static {
        new File(serviceToPath).mkdirs();
    }

    public static void main(String[] args) throws DocumentException, IOException, TemplateException {

        String generatorPath = getGeneratorPath();

        String moduleName = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("moduleName:" + moduleName);
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        //获取表名do_main,实体类名Domain,业务类前缀domain
        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");

        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("组装参数：" + param);

        generateCode(Domain, param, "service");
        generateCode(Domain, param, "controller");

    }

    private static void generateCode(String Domain, Map<String, Object> param, String targetType) throws IOException, TemplateException {
        FreeMarkerUtil.initConfig(targetType + ".ftl");
        String toPath = serviceToPath + targetType + "/";
        new File(toPath).mkdirs();
        String target = targetType.substring(0, 1).toUpperCase() + targetType.substring(1);
        String fileName = toPath + Domain + target + ".java";
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
}
