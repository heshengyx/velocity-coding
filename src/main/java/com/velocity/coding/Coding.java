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

import com.velocity.coding.enums.JdbcTypeEnum;

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
		createTemplateMapperXml();
		createTemplateJsp();
		createTemplateJspList();
	}

	private static void createTemplateMapperXml() {
		boolean flag = Boolean.valueOf(props.getProperty("mapper.xml.flag"));
		if (flag) {
			template = ve.getTemplate("template/template.mapper.xml.vm", encoding);
			VelocityContext context = new VelocityContext();
			
			context.put("package", props.getProperty("package.name"));
			String className = props.getProperty("class.name");
			context.put("Entity", className);
			context.put("entity", lowerName(className));
			context.put("table", props.getProperty("table.name").toUpperCase());
			
			StringBuilder results = new StringBuilder("");
			StringBuilder conditions = new StringBuilder("");
			StringBuilder columns = new StringBuilder("");
			StringBuilder values = new StringBuilder("");
			StringBuilder sets = new StringBuilder("");
			
			int attributeNum = Integer.parseInt(props.getProperty("entity.attributes"));
			for (int i = 1; i <= attributeNum; i++) {
				String attribute = props.getProperty("entity.attribute" + i);
				String[] attributeNames = attribute.split("[,]");
				String column = "";
				if (attributeNames.length > 2) {
					column = attributeNames[2];
				} else {
					column = attributeNames[0];
				}
				
				results.append("<result column=\"")
						.append(column.toUpperCase()).append("\" property=\"")
						.append(attributeNames[0]).append("\" jdbcType=\"")
						.append(JdbcTypeEnum.getJdbcType(attributeNames[1]))
						.append("\" />\n");

				columns.append(column.toUpperCase()).append(",\n");
				
				values.append("#{param.").append(attributeNames[0])
						.append(", jdbcType=")
						.append(JdbcTypeEnum.getJdbcType(attributeNames[1]))
						.append("},\n");
				
				sets.append("<if test=\"param.").append(attributeNames[0])
						.append(" != null and param.")
						.append(attributeNames[0]).append(" != ''\">\n")
						.append(" a.").append(column.toUpperCase())
						.append(" = #{param.").append(attributeNames[0])
						.append(", jdbcType=")
						.append(JdbcTypeEnum.getJdbcType(attributeNames[1]))
						.append("},\n").append("</if>\n");
			}
			
			attributeNum = Integer.parseInt(props.getProperty("param.attributes"));
			for (int i = 1; i <= attributeNum; i++) {
				String attribute = props.getProperty("param.attribute" + i);
				String[] attributeNames = attribute.split("[,]");
				if (Integer.parseInt(attributeNames[3]) != 0) {
					conditions.append("<if test=\"param.")
							.append(attributeNames[0])
							.append(" != null and param.")
							.append(attributeNames[0]).append(" != ''\">\n");
					switch (Integer.parseInt(attributeNames[3])) {
					case 1:
						//字符串=
						conditions.append(" AND a.")
								.append(attributeNames[2].toUpperCase())
								.append(" = #{param.")
								.append(attributeNames[0]).append("}\n")
								.append("</if>\n");
						break;
					case 2:
						//日期(yyyy-MM-dd)=
						conditions.append(" AND to_char(a.")
								.append(attributeNames[2].toUpperCase())
								.append(", 'YYYY-MM-DD') = #{param.")
								.append(attributeNames[0]).append("}\n")
								.append("</if>\n");
						break;
					case 3:
						//日期(yyyy-MM-dd)>=
						conditions.append(" AND to_char(a.")
								.append(attributeNames[2].toUpperCase())
								.append(", 'YYYY-MM-DD') <![CDATA[>=]]> #{param.")
								.append(attributeNames[0]).append("}\n")
								.append("</if>\n");
						break;
					case 4:
						//日期(yyyy-MM-dd)<=
						conditions
								.append(" AND to_char(a.")
								.append(attributeNames[2].toUpperCase())
								.append(", 'YYYY-MM-DD') <![CDATA[<=]]> #{param.")
								.append(attributeNames[0]).append("}\n")
								.append("</if>\n");
						break;
					default:
						break;
					}
				}
			}
			
			context.put("results", results.toString());
			context.put("conditions", conditions.toString());
			context.put("columns", columns.toString());
			context.put("values", values.toString());
			context.put("sets", sets.toString());
			
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			writeJavaFile(props.getProperty("mapper.xml.path") + className + "Mapper.xml", writer.toString());
		}
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
			
			//批量新增
			StringBuilder attributes = new StringBuilder("");
			StringBuilder methods = new StringBuilder("");
			String attribute = "";
			String data = props.getProperty(tagName + ".savebatch.data");
			if (!StringUtils.isEmpty(data)) {
				if (data.contains(",")) {
					String[] datas = data.split("[,]");
					for (int i = 0; i < datas.length; i++) {
						String attributeData = batchAdd(attributes, methods, datas[i], lowerName(className));
						if (i == 0) attribute = attributeData + "s";
					}
				} else {
					attribute = batchAdd(attributes, methods, data, lowerName(className));
				}
			}
			context.put("attributes", attributes);
			context.put("methods", methods);
			context.put("attribute", attribute);
			
			//新增验证
			StringBuilder saveValidates = new StringBuilder("");
			String saveValidate = props.getProperty(tagName + ".save.validate.entity");
			if (!StringUtils.isEmpty(saveValidate)) {
				if (saveValidate.contains(",")) {
					String[] datas = saveValidate.split("[,]");
					for (int i = 0; i < datas.length; i++) {
						validates(saveValidates, datas[i], lowerName(className));
					}
				} else {
					validates(saveValidates, saveValidate, lowerName(className));
				}
			}
			context.put("saveValidates", saveValidates);
			
			//修改验证
			StringBuilder updateValidates = new StringBuilder("");
			String updateValidate = props.getProperty(tagName + ".update.validate.entity");
			if (!StringUtils.isEmpty(updateValidate)) {
				if (updateValidate.contains(",")) {
					String[] datas = updateValidate.split("[,]");
					for (int i = 0; i < datas.length; i++) {
						validates(updateValidates, datas[i], lowerName(className));
					}
				} else {
					validates(updateValidates, updateValidate, lowerName(className));
				}
			}
			context.put("updateValidates", updateValidates);
			
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

	/**
	 * 批量新增
	 * @param attributes
	 * @param methods
	 * @param data
	 * @param lowerName
	 */
	private static String batchAdd(StringBuilder attributes,
			StringBuilder methods, String data, String className) {
		String attributeData = props.getProperty("data.attribute" + data);
		String[] attributeNames = attributeData.split("[,]");
		attributes.append(attributeNames[1]).append(" ")
				.append(attributeNames[0]).append("s").append(" = ")
				.append("data.").append(getMethodName(attributeNames[0]))
				.append("();\n");
		methods.append(lowerName(className)).append(".")
				.append(setMethodName(attributeNames[0])).append("(")
				.append(attributeNames[0]).append("s[i]);\n");
		return attributeNames[0] + "s";
	}

	/**
	 * 设置验证
	 * @param validates
	 * @param validate
	 * @param className
	 */
	private static void validates(StringBuilder validates,
			String validate, String className) {
		String attributeData = props.getProperty("entity.attribute" + validate);
		String[] attributeNames = attributeData.split("[,]");
		validates.append(attributeNames[1]).append(" ")
				.append(attributeNames[0]).append(" = ")
				.append(className).append(".")
				.append(getMethodName(attributeNames[0]))
				.append("();\n");
		JdbcTypeEnum key = JdbcTypeEnum.valueOf(attributeNames[1].toUpperCase());
		switch (key) {
		case STRING:
			validates
					.append("if (StringUtils.isEmpty(")
					.append(attributeNames[0])
					.append(")) {\n")
					.append("throw new DataAccessResourceFailureException(\"")
					.append(attributeNames[3])
					.append("不能为空\");\n}\n");
			break;
		default:
			validates
					.append("if (null != ")
					.append(attributeNames[0])
					.append(") {\n")
					.append("throw new DataAccessResourceFailureException(\"")
					.append(attributeNames[3])
					.append("不能为空\");\n}\n");
			break;
		}
	}
	
	private static void createTemplateJsp() {
		template = ve.getTemplate("template/template.jsp.vm", encoding);
		VelocityContext context = new VelocityContext();
		
		String className = props.getProperty("class.name");
		String jspName = lowerName(className);
		context.put("title", props.getProperty("jsp.title"));
		context.put("controller", jspName);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		writeJavaFile(props.getProperty("jsp.path") + jspName + ".jsp", writer.toString());
	}

	private static void createTemplateJspList() {
		template = ve.getTemplate("template/template.jsp.list.vm", encoding);
		VelocityContext context = new VelocityContext();
		
		String className = props.getProperty("class.name");
		String jspName = lowerName(className);
		context.put("title", props.getProperty("jsp.title"));
		context.put("searchFlag", props.getProperty("jsp.search.flag"));
		context.put("controller", jspName);
		
		StringBuilder searchInputs = new StringBuilder("");
		StringBuilder searchParams = new StringBuilder("");
		String search = props.getProperty("jsp.search.param");
		if (!StringUtils.isEmpty(search)) {
			if (search.contains(",")) {
				String[] names = search.split("[,]");
				for (String name : names) {
					searchs(searchInputs, searchParams, name);
				}
			} else {
				searchs(searchInputs, searchParams, search);
			}
		}
		context.put("searchInputs", searchInputs);
		context.put("searchParams", searchParams);
		
		StringBuilder columns = new StringBuilder("");
		String column = props.getProperty("jsp.column.entity");
		if (!StringUtils.isEmpty(column)) {
			if (column.contains(",")) {
				String[] names = column.split("[,]");
				for (String name : names) {
					columns(columns, name);
				}
			} else {
				columns(columns, column);
			}
		}
		context.put("columns", columns);
		
		StringBuilder edits = new StringBuilder("");
		String edit = props.getProperty("jsp.edit.entity");
		if (!StringUtils.isEmpty(edit)) {
			if (edit.contains(",")) {
				String[] names = edit.split("[,]");
				for (String name : names) {
					edits(edits, name);
				}
			} else {
				edits(edits, edit);
			}
		}
		context.put("edits", edits);
		
		StringBuilder batchColumns = new StringBuilder("");
		StringBuilder batchInputs = new StringBuilder("");
		String batchColumn = props.getProperty("jsp.batch.column.data");
		if (!StringUtils.isEmpty(batchColumn)) {
			if (batchColumn.contains(",")) {
				String[] names = batchColumn.split("[,]");
				for (String name : names) {
					batchColumns(batchColumns, batchInputs, name);
				}
			} else {
				batchColumns(batchColumns, batchInputs, batchColumn);
			}
		}
		context.put("batchColumns", batchColumns);
		context.put("batchInputs", batchInputs);
		
		StringBuilder updates = new StringBuilder("");
		String update = props.getProperty("jsp.update.entity");
		if (!StringUtils.isEmpty(update)) {
			if (update.contains(",")) {
				String[] names = update.split("[,]");
				for (String name : names) {
					updates(updates, name);
				}
			} else {
				updates(updates, update);
			}
		}
		context.put("updates", updates);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		writeJavaFile(props.getProperty("jsp.path") + jspName + "List.jsp", writer.toString());
	}

	private static void updates(StringBuilder updates, String index) {
		String attributeData = props.getProperty("entity.attribute" + index);
		String[] attributeNames = attributeData.split("[,]");
		updates.append("$('#").append(attributeNames[0]).append("Edit').val(result.data.").append(attributeNames[0])
				.append(");\n");
	}

	private static void batchColumns(StringBuilder batchColumns, StringBuilder batchInputs, String index) {
		String attributeData = props.getProperty("data.attribute" + index);
		String[] attributeNames = attributeData.split("[,]");
		batchColumns.append("<th data-options=\"field:'").append(attributeNames[0]).append("',editor:{type:'text'}\">")
				.append(attributeNames[2]).append("</th>\n");
		batchInputs.append("form.append('<input type=\"hidden\" name=\"").append(attributeNames[0])
				.append("\" value=\"' + inserted[i].").append(attributeNames[0]).append(" + '\">');\n");
	}

	private static void columns(StringBuilder columns, String index) {
		String attributeData = props.getProperty("entity.attribute" + index);
		String[] attributeNames = attributeData.split("[,]");
		columns.append("{field: '").append(attributeNames[0]).append("', title: '").append(attributeNames[3])
				.append("'},\n");
	}

	private static void edits(StringBuilder edits, String index) {
		String attributeData = props.getProperty("entity.attribute"
				+ index);
		String[] attributeNames = attributeData.split("[,]");
		edits.append("<tr>\n");
		edits.append("<td align=\"right\">").append(attributeNames[3]).append("：</td>\n");
		edits.append("<td>\n<input class=\"easyui-validatebox\" type=\"text\" name=\"")
				.append(attributeNames[0]).append("\" id=\"").append(attributeNames[0]).append("Edit")
				.append("\" style=\"width:150px;\" data-options=\"required:true\"></td>\n");
		edits.append("</tr>\n");
	}

	private static void searchs(StringBuilder searchInputs, StringBuilder searchParams, String search) {
		StringBuilder inputs = new StringBuilder("<td>");
		String[] searchs = search.split("[:]");
		if (searchs[0].contains("-")) {
			String[] param = searchs[0].split("[-]");
			for (int i = 0; i < param.length; i++) {
				String attributeData = props.getProperty("param.attribute" + param[i]);
				String[] attributeNames = attributeData.split("[,]");
				inputs.append("<input type=\"text\" class=\"easyui-datebox\" id=\"").append(attributeNames[0])
						.append("Search\" style=\"width:100px;\">");
				if (i == 0)
					inputs.append("~\n");
				searchParams.append(attributeNames[0]).append(": $('#").append(attributeNames[0])
						.append("Search').val(),\n");
			}
		} else {
			String attributeData = props.getProperty("param.attribute" + searchs[0]);
			String[] attributeNames = attributeData.split("[,]");
			inputs.append("<input type=\"text\" id=\"").append(attributeNames[0])
					.append("Search\" style=\"width:150px;\">");
			searchParams.append(attributeNames[0]).append(": $('#").append(attributeNames[0])
					.append("Search').val(),\n");
		}
		inputs.append("</td>\n");
		searchInputs.append("<td class=\"td-right\">").append(searchs[1]).append("：</td>\n").append(inputs.toString());
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
