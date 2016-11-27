package com.velocity.coding;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.util.StringUtils;

public class Coding {

	private static VelocityEngine ve;
	private static Template template;
	private static Properties props;
	private static String encoding = "UTF-8";

	static {
		ve = new VelocityEngine();
		ve.setProperty(Velocity.RESOURCE_LOADER, "class");
		ve.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init();

		props = new Properties();
		try {
			props.load(Coding.class.getResourceAsStream("/conf/terminal.properties"));
		} catch (Exception e) {
			System.err.println("不能读取属性文件，请确保属性文件在你的classpath中");
		}
	}

	public static void createTemplate() {
		createTemplate("entity", "");
		createTemplate("data", "Data");
		createTemplate("param", "QueryParam");
		createTemplateByTag("Service", "I");
		createTemplateByTag("Service.Impl", "");
		createTemplateByTag("Dao", "I");
		createTemplateByTag("Dao.Impl", "");
		createTemplateByTag("Mapper", "I");
		createTemplateByTag("Controller", "");
		createTemplateJSP();
	}

	private static void createTemplate(String tag, String suffix) {
		boolean flag = Boolean.valueOf(props.getProperty(tag + ".flag"));
		if (flag) {
			template = ve.getTemplate("template/template." + tag + ".vm", encoding);
			VelocityContext context = new VelocityContext();
			
			context.put("package", props.getProperty("package.name"));
			StringBuilder imports = new StringBuilder("");
			int importNum = Integer.parseInt(props.getProperty(tag + ".imports"));
			for (int i = 1; i <= importNum; i++) {
				imports.append("import ").append(props.getProperty(tag + ".import" + i)).append(";\n");
			}
			context.put("imports", imports.toString());

			String className = props.getProperty("class.name");
			context.put("Entity", className);

			StringBuilder attributes = new StringBuilder("");
			StringBuilder methods = new StringBuilder("");
			int attributeNum = Integer.parseInt(props.getProperty(tag + ".attributes"));
			for (int i = 1; i <= attributeNum; i++) {
				String attribute = props.getProperty(tag + ".attribute" + i);
				String[] attributeNames = attribute.split("[,]");
				if (i != 1) attributes.append("	");
				attributes.append("private ").append(attributeNames[1]).append(" ").append(attributeNames[0]).append(";\n");

				if (i != 1) methods.append("	");
				methods.append("public void ").append(setMethodName(attributeNames[0])).append("(")
						.append(attributeNames[1]).append(" ").append(attributeNames[0]).append(") {\n");
				methods.append("		this.").append(attributeNames[0]).append(" = ").append(attributeNames[0])
						.append(";\n	}\n");
				methods.append("	public ").append(attributeNames[1]).append(" ").append(getMethodName(attributeNames[0]))
						.append("() {\n");
				methods.append("		return ").append(attributeNames[0]).append(";\n").append("	}\n");
			}
			context.put("attributes", attributes.toString());
			context.put("methods", methods.toString());

			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			writeJavaFile(props.getProperty(tag + ".path") + className + suffix + ".java", writer.toString());
		}
	}
	
	private static void createTemplateByTag(String tag, String prefix) {
		String tagName = tag.toLowerCase();
		boolean flag = Boolean.valueOf(props.getProperty(tagName + ".flag"));
		if (flag) {
			template = ve.getTemplate("template/template." + tagName + ".vm", encoding);
			VelocityContext context = new VelocityContext();
			
			context.put("package", props.getProperty("package.name"));
			StringBuilder imports = new StringBuilder("");
			int importNum = Integer.parseInt(props.getProperty(tagName + ".imports"));
			for (int i = 1; i <= importNum; i++) {
				imports.append("import ").append(props.getProperty(tagName + ".import" + i)).append(";\n");
			}
			context.put("imports", imports.toString());

			String className = props.getProperty("class.name");
			context.put("Entity", className);
			context.put("entity", lowerName(className));
			
			//批量
			StringBuilder attributes = new StringBuilder("");
			StringBuilder methods = new StringBuilder("");
			String attribute = "";
			String data = props.getProperty(tagName + ".param.data");
			if (!StringUtils.isEmpty(data)) {
				if (data.contains(",")) {
					String[] datas = data.split("[,]");
					for (int i = 0; i < datas.length; i++) {
						String _data = datas[i];
						String attributeData = props.getProperty("data.attribute" + _data);
						String[] attributeNames = attributeData.split("[,]");
						attributes.append(attributeNames[1]).append(" ").append(attributeNames[0]).append("s").append(" = ")
								.append("data.").append(getMethodName(attributeNames[0])).append("();\n");
						methods.append(lowerName(className)).append(".").append(setMethodName(attributeNames[0]))
								.append("(").append(attributeNames[0]).append("s[i]);\n");
						if (i == 0) attribute = attributeNames[0] + "s";
					}
				} else {
					String attributeData = props.getProperty("data.attribute" + data);
					String[] attributeNames = attributeData.split("[,]");
					attributes.append(attributeNames[1]).append(" ").append(attributeNames[0]).append("s").append(" = ")
							.append("data.").append(getMethodName(attributeNames[0])).append("();\n");
					methods.append(lowerName(className)).append(".").append(setMethodName(attributeNames[0]))
							.append("(").append(attributeNames[0]).append("s[i]);\n");
					attribute = attributeNames[0] + "s";
				}
			}
			context.put("attributes", attributes);
			context.put("methods", methods);
			context.put("attribute", attribute);
			
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			String suffix = "";
			if (tag.contains(".")) {
				String[] tags = tag.split("[.]");
				for (String _tag : tags) {
					suffix += _tag;
				}
			} else {
				suffix = tag;
			}
			writeJavaFile(props.getProperty(tagName + ".path") + prefix + className + suffix + ".java", writer.toString());
		}
	}

	private static void createTemplateJSP() {
		template = ve.getTemplate("template/template.jsp.list.vm", encoding);
		VelocityContext context = new VelocityContext();
		
		String className = props.getProperty("class.name");
		String jspName = lowerName(className);
		context.put("title", props.getProperty("jsp.title"));
		context.put("searchFlag", true);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		writeJavaFile(props.getProperty("jsp.path") + jspName + "List.jsp", writer.toString());
	}

	private static void writeJavaFile(String name, String content) {
		System.out.println(name);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));
			bufferedWriter.write(content, 0, content.length());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getMethodName(String field) {
		return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
	}

	public static String setMethodName(String field) {
		return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
	}
	
	public static String lowerName(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
		/*char[] cs = name.toCharArray();
		cs[0] -= 32;
		return String.valueOf(cs);*/
	}
}
