package com.gbi.commons.util.file;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class ExcelTest {
	private static final Map<String, String[]> regex = new HashMap<String, String[]>();
	private static final Map<String, String[]> name = new HashMap<String, String[]>();

	static {
		regex.put("肠溶片", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|单位|万单位)[*×]{1}"
				+ "(?<i2>\\d+)"
				+ "(?<i3>片|粒+)[*×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板|盒*)"});
		name.put("肠溶片", new String[] { "strength value", "strength unit", "tablet value", "tablet unit", "plate value", "plate unit" });
		regex.put("鼻用喷剂", new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>ug|mg|IU)[/]{0,1}(揿|喷){0,1}×"
				+ "(?<i2>\\d+)"
				+ "(?<i3>揿|喷){0,1}"});
		name.put("鼻用喷剂", new String[] { "strength value", "strength unit", "dose value", "dose unit" });
	//	regex.put("搽剂",		new String[] { "(\\d+[.\\d+]*)([umgIU]+)[/×/u\\u4e00-\\u9fa5]+(\\d+)([揿喷]+)[/\\u4e00-\\u9fa5]*" });
	//	name.put("搽剂",			new String[] { "strength value", "strength unit", "一喷 value", "喷 unit" });
		regex.put("肠溶缓释胶囊",new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg)×"
				+ "(?<i2>\\d+)"
				+ "(?<i3>粒)[×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板*)"});
		name.put("肠溶缓释胶囊",	new String[] { "strength value", "strength unit", "capsule value", "capsule unit", "plate value", "plate unit" });
		regex.put("双释肠溶胶囊",new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg)×"
				+ "(?<i2>\\d+)"
				+ "(?<i3>粒)[×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板*)"});
		name.put("双释肠溶胶囊",	new String[] { "strength value", "strength unit", "capsule value", "capsule unit", "plate value", "plate unit" });
		regex.put("肠溶胶囊", new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|万单位|万IU)[*×]{0,1}"
				+ "(?<i2>\\d+)"
				+ "(?<i3>片|粒+)[×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板|盒){0,1}" });
		name.put("肠溶胶囊",	new String[] { "strength value", "strength unit", "capsule value", "capsule unit", "plate value", "plate unit" });
		regex.put("滴鼻剂", new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|ug|单位|万单位|IU|ku|万国际单位|万IU|万博来霉素单位)[×]{0,1}"
				+ "(?<i2>\\d*)"
				+ "(?<i3>支|瓶){0,1}" });
		name.put("滴鼻剂", new String[] { "strength value", "strength unit", "bottle value", "bottle unit" });
		regex.put("滴耳剂", new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|ug|单位|万单位|IU|ku|万国际单位|万IU|万博来霉素单位)[×]{0,1}"
				+ "(?<i2>\\d*)"
				+ "(?<i3>支|瓶){0,1}" });
		name.put("滴耳剂", new String[] { "strength value", "strength unit", "bottle value", "bottle unit" });
		regex.put("滴剂", new String[] {""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|ug|单位|万单位|IU|ku|万国际单位|万IU|万博来霉素单位)[*×]{0,1}"
				+ "(?<i2>\\d*)"
				+ "(?<i3>粒*)[*×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板{0,1})[*×]{0,1}"
				+ "(?<i6>\\d*)"
				+ "(?<i7>支|瓶|ml){0,1}",});
		name.put("滴剂",	 new String[] { "strength value", "strength unit", "capsule value", "capsule unit", "plate value", "plate unit", "bottle value", "bottle unit" });
		regex.put("滴丸剂", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg)×"
				+ "(?<i2>\\d+)"
				+ "(?<i3>丸|粒)" });
		name.put("滴丸剂", new String[] { "strength value", "strength unit", "table value", "tablet unit" });
		regex.put("滴眼剂", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|g|ug|IU)(×|:){0,1}"
				+ "(?<i2>\\d*)"
				+ "(?<i3>支|瓶){0,1}" });
		name.put("滴眼剂", new String[] { "strength value", "strength unit", "bottle value", "bottle unit" });
		regex.put("酊剂", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg)(×|:){0,1}" });
		name.put("酊剂", new String[] { "strength value", "strength unit" });
		regex.put("分散片", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>mg|万单位|g)[×*]{0,1}"
				+ "(?<i2>\\d*)"
				+ "(?<i3>片|粒){0,1}[×]{0,1}"
				+ "(?<i4>\\d*)"
				+ "(?<i5>板|盒){0,1}" });
		name.put("分散片", new String[] { "strength value", "strength unit",	"tablet value", "tablet unit", "plate value", "plate unit" });
		regex.put("灌肠剂", new String[] { ""
				+ "(?<i0>\\d+[.\\d+]*)"
				+ "(?<i1>g)×"
				+ "(?<i2>\\d*)"
				+ "(?<i3>支)" });
		name.put("灌肠剂", new String[] { "strength value", "strength unit", "bottle value", "bottle unit" });
		
	}
	
	/**
	 * translate the given string {@code specification} to a JSONObject with the {@code formulation}
	 * @param formulation 产品类型
	 * @param specification 规格
	 * @return a JSONObject
	 */
	public static JSONObject translate(String formulation, String specification) {
		JSONObject json = new JSONObject();
		String[] names = name.get(formulation);
		
		for (String regex : regex.get(formulation)) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(specification);
			if (matcher.find()) {
				for (int i = 0; i < names.length; ++i) {
					json.put(names[i], matcher.group("i" + i));
				}
				if (matcher.find() == false) {
					return json;
				} else {
					System.out.println("multible");
					return null;
				}
			}
		}
		return null;
	}
	
	private static class Translator implements ExcelRowReader {
		String type = "灌肠剂";
		@Override
		public void getRows(int rowNumber, JSONObject json) {
			if (type.equals(json.getString("formulation_cn"))) {
				JSONObject obj = translate(type, json.getString("specification_raw"));
				if(obj == null) {
					System.out.printf("%5d:" + json.getString("specification_raw") + "\n", rowNumber);
				}
//				else {
//					System.out.println(json);
//				}
			}
		}
	}
	
	private static void test1() throws Exception {
		Excel.readExcel(new Translator(), ResourceUtil.getTestFileAbstractName(Excel.class, "test1.xlsx"), "Sheet0");
	}

	public static void main(String[] args) throws Exception {
		test1();
	}
}
