package com.orisun.mining.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class TestClassUtil {

	enum Color {
		R(1, "红"), G(2, "绿"), B(3, "蓝");
		private int index;
		private String desc;

		private static Map<Integer, Color> indexMap = new HashMap<Integer, Color>();
		private static Map<String, Color> descMap = new HashMap<String, Color>();

		private Color(int i, String desc) {
			this.index = i;
			this.desc = desc;
		}

		static {
			for (Color ele : Color.values()) {
				indexMap.put(ele.getIndex(), ele);
				descMap.put(ele.getDesc(), ele);
			}
		}

		public static Color parseIndex(int i) {
			return indexMap.get(i);
		}

		public static Color parseDesc(String desc) {
			Color rect = descMap.get(desc);
			if (rect == null) {
				rect = Color.R;
			}
			return rect;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}

	@Test
	public void testEnum() {
		Color color = (Color) ClassUtil.reflectEnum(Color.class, "G");
		Assert.assertEquals("G", color.toString());
		Assert.assertEquals(1, color.ordinal());
	}

	@Test
	public void testTransPrimitive() {
		char c = 'A';
		Assert.assertEquals(c, ClassUtil.transPrimitive(char.class, "A"));
	}

	@Test
	public void testTransMap() throws ParseException {
		class A {
			Map<Date, Double> map1 = new HashMap<Date, Double>();
			Map<String, Integer> map2 = new HashMap<String, Integer>();
			Map<Color, Boolean> map3 = new HashMap<Color, Boolean>();
			List<Date> list1 = new ArrayList<Date>();
			List<Float> list2 = new ArrayList<Float>();
			List<Color> list3 = new ArrayList<Color>();

			public void put1(Date date, Double d) {
				this.map1.put(date, d);
			}

			public void put2(String str, Integer i) {
				this.map2.put(str, i);
			}

			public void put3(Color color, Boolean b) {
				this.map3.put(color, b);
			}

			public void add1(Date date) {
				this.list1.add(date);
			}

			public void add2(Float f) {
				this.list2.add(f);
			}

			public void add3(Color color) {
				this.list3.add(color);
			}

			public Map<Date, Double> getMap1() {
				return this.map1;
			}

			public Map<String, Integer> getMap2() {
				return this.map2;
			}

			public Map<Color, Boolean> getMap3() {
				return this.map3;
			}

			public List<Date> getList1() {
				return this.list1;
			}

			public List<Float> getList2() {
				return this.list2;
			}

			public List<Color> getList3() {
				return this.list3;
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		A inst = new A();
		Date date = sdf.parse("2016-12-29 18:39:20");
		Double d = new Double(2.0);
		Float f = new Float(2f);
		String str = "orisun";
		Color color = Color.R;
		Boolean b = Boolean.TRUE;
		int i = 58;
		inst.put1(date, d);
		inst.put2(str, i);
		inst.put3(color, b);
		inst.add1(date);
		inst.add2(f);
		inst.add3(color);
		SerializeConfig mapping = new SerializeConfig();
		mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		String json1 = JSON.toJSONString(inst.getMap1(), mapping);
		System.out.println(json1);
		String json2 = JSON.toJSONString(inst.getMap2(), mapping);
		System.out.println(json2);
		String json3 = JSON.toJSONString(inst.getList1(), mapping);
		System.out.println(json3);
		String json4 = JSON.toJSONString(inst.getList2(), mapping);
		System.out.println(json4);
		String json5 = JSON.toJSONString(inst.getMap3(), mapping);
		System.out.println(json5);
		String json6 = JSON.toJSONString(inst.getList3(), mapping);
		System.out.println(json6);
		Field[] fields = inst.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals("map1")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(Map.class)) {
					Map<Object, Object> map = ClassUtil.deserializeMap(json1, field);
					for (Entry<Object, Object> entry : map.entrySet()) {
						Assert.assertTrue(((Date) entry.getKey()).getTime() == date.getTime());
						Assert.assertTrue(((Double) entry.getValue()).doubleValue() == d.doubleValue());
						System.out.println("check map1");
					}
				}
			} else if (field.getName().equals("map2")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(Map.class)) {
					Map<Object, Object> map = ClassUtil.deserializeMap(json2, field);
					for (Entry<Object, Object> entry : map.entrySet()) {
						Assert.assertEquals(((String) entry.getKey()), str);
						Assert.assertTrue(((Integer) entry.getValue()).intValue() == i);
						System.out.println("check map2");
					}
				}
			} else if (field.getName().equals("map3")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(Map.class)) {
					Map<Object, Object> map = ClassUtil.deserializeMap(json5, field);
					for (Entry<Object, Object> entry : map.entrySet()) {
						Assert.assertEquals(((Color) entry.getKey()), color);
						Assert.assertTrue(((Boolean) entry.getValue()).booleanValue() == b.booleanValue());
						System.out.println("check map3");
					}
				}
			} else if (field.getName().equals("list1")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(List.class)) {
					List<Object> list = ClassUtil.deserializeList(json3, field);
					for (Object ele : list) {
						Assert.assertTrue(((Date) ele).getTime() == date.getTime());
						System.out.println("check list1");
					}
				}
			} else if (field.getName().equals("list2")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(List.class)) {
					List<Object> list = ClassUtil.deserializeList(json4, field);
					for (Object ele : list) {
						Assert.assertTrue(((Float) ele).floatValue() == f);
						System.out.println("check list2");
					}
				}
			} else if (field.getName().equals("list3")) {
				Class<?> fieldClazz = field.getType();
				if (fieldClazz.isAssignableFrom(List.class)) {
					List<Object> list = ClassUtil.deserializeList(json6, field);
					for (Object ele : list) {
						Assert.assertTrue((Color) ele == color);
						System.out.println("check list3");
					}
				}
			}
		}
	}
}
