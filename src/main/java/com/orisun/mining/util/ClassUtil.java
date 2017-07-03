package com.orisun.mining.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * 
 * @author orisun
 * @date 2016年12月29日
 */
public class ClassUtil {

	private static Log logger = LogFactory.getLog(ClassUtil.class);
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取一个类（包括祖先类）的所有属性
	 * 
	 * @param cls
	 * @param fs
	 * @return
	 */
	public static Field[] getBeanFields(Class<?> cls, Field[] fs) {
		fs = (Field[]) ArrayUtils.addAll(fs, cls.getDeclaredFields());
		Class<?> supCls = cls.getSuperclass();
		if (supCls != null) {
			fs = getBeanFields(supCls, fs);
		}
		return fs;
	}

	/**
	 * 拼接某属性set 方法
	 * 
	 * @param fldname
	 * @return
	 */
	public static String parseSetName(String fldname) {
		if (null == fldname || "".equals(fldname)) {
			return null;
		}
		String pro = "set" + fldname.substring(0, 1).toUpperCase() + fldname.substring(1);
		return pro;
	}

	/**
	 * 拼接某属性get 方法
	 * 
	 * @param fldname
	 * @return
	 */
	public static String parseGetName(String fldname) {
		if (null == fldname || "".equals(fldname)) {
			return null;
		}
		String pro = "get" + fldname.substring(0, 1).toUpperCase() + fldname.substring(1);
		return pro;
	}

	/**
	 * 
	 * 
	 * @param clz
	 *            枚举类型
	 * @param str
	 *            枚举实例toString之后的字条串
	 * @return 枚举实例
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Enum reflectEnum(Class clz, String str) {
		return Enum.valueOf(clz, str);
	}

	/**
	 * 从String转换成把8种基础数据类型--Integer,Double,Long,Boolean,Short,Byte,Float,
	 * Character
	 * 
	 * @param type
	 *            数据类型
	 * @param value
	 * @return
	 */
	public static Object transPrimitive(Class<?> type, String value) {
		if (type == Integer.class || type == int.class) {
			return Integer.parseInt(value);
		} else if (type == Double.class || type == double.class) {
			return Double.parseDouble(value);
		} else if (type == Long.class || type == long.class) {
			return Long.parseLong(value);
		} else if (type == Boolean.class || type == boolean.class) {
			return Boolean.parseBoolean(value);
		} else if (type == Short.class || type == short.class) {
			return Short.parseShort(value);
		} else if (type == Byte.class || type == byte.class) {
			return Byte.parseByte(value);
		} else if (type == Float.class || type == float.class) {
			return Float.parseFloat(value);
		} else if (type == Character.class || type == char.class) {
			return value.charAt(0);
		} else {
			return null;
		}
	}

	/**
	 * 从json转换为List
	 * 
	 * @param json
	 * @param field
	 *            List中元素的类型，，仅支持8种primitive类型外加String、Date和Enum
	 * @return
	 */
	public static List<Object> deserializeList(String json, Field field) {
		List<Object> rect = new ArrayList<Object>();
		Class<?> fieldClazz = field.getType();
		assert fieldClazz.isAssignableFrom(List.class);
		Type fc = field.getGenericType();
		if (fc == null) {
			return null;
		}
		if (fc instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fc;
			Class<?> valueType = (Class<?>) pt.getActualTypeArguments()[0];
			List<?> list = JSON.parseArray(json, valueType);
			if (valueType.isAssignableFrom(String.class)) {
				for (Object ele : list) {
					rect.add(ele.toString());
				}
			} else if (valueType.isAssignableFrom(Date.class)) {
				for (Object ele : list) {
					rect.add(ele);
				}
			} else if (Enum.class.isAssignableFrom(valueType)) {
				for (Object ele : list) {
					rect.add(ClassUtil.reflectEnum(valueType, ele.toString()));
				}
			} else {
				for (Object ele : list) {
					rect.add(transPrimitive(valueType, ele.toString()));
				}
			}
		}
		return rect;
	}

	/**
	 * 从json转换为Map
	 * 
	 * @param json
	 * @param field
	 *            指定了map的key和value的数据类型，仅支持8种primitive类型外加String、Date和Enum
	 * @return
	 */
	public static Map<Object, Object> deserializeMap(String json, Field field) {
		Map<Object, Object> rect = new HashMap<Object, Object>();
		Class<?> fieldClazz = field.getType();
		assert fieldClazz.isAssignableFrom(Map.class);
		Type fc = field.getGenericType();
		if (fc == null) {
			return null;
		}
		if (fc instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fc;
			Type[] types = pt.getActualTypeArguments();
			JSONObject jo = (JSONObject) JSON.parse(json);
			if (types.length == 2) {
				Class<?> keyType = (Class<?>) types[0];
				Class<?> valueType = (Class<?>) types[1];
				Map<Object, Object> tmpRect = new HashMap<Object, Object>();
				if (keyType.isAssignableFrom(String.class)) {
					for (Entry<String, Object> entry : jo.entrySet()) {
						tmpRect.put(entry.getKey(), entry.getValue());
					}
				} else if (keyType.isAssignableFrom(Date.class)) {
					try {
						for (Entry<String, Object> entry : jo.entrySet()) {
							tmpRect.put(sdf.parseObject(entry.getKey()), entry.getValue());
						}
					} catch (ParseException e) {
						logger.error("parse date failed", e);
					}
				} else if (Enum.class.isAssignableFrom(keyType)) {
					for (Entry<String, Object> entry : jo.entrySet()) {
						tmpRect.put(ClassUtil.reflectEnum(keyType, entry.getKey().toString()), entry.getValue());
					}
				} else {
					for (Entry<String, Object> entry : jo.entrySet()) {
						tmpRect.put(transPrimitive(keyType, entry.getKey()), entry.getValue());
					}
				}
				if (valueType.isAssignableFrom(String.class)) {
					for (Entry<Object, Object> entry : tmpRect.entrySet()) {
						rect.put(entry.getKey(), entry.getValue().toString());
					}
				} else if (valueType.isAssignableFrom(Date.class)) {
					try {
						for (Entry<Object, Object> entry : tmpRect.entrySet()) {
							rect.put(entry.getKey(), sdf.parseObject(entry.getValue().toString()));
						}
					} catch (ParseException e) {
						logger.error("parse date failed", e);
					}
				} else if (Enum.class.isAssignableFrom(valueType)) {
					for (Entry<String, Object> entry : jo.entrySet()) {
						tmpRect.put(entry.getKey(), ClassUtil.reflectEnum(valueType, entry.getValue().toString()));
					}
				} else {
					for (Entry<Object, Object> entry : tmpRect.entrySet()) {
						rect.put(entry.getKey(), transPrimitive(valueType, entry.getValue().toString()));
					}
				}
			} else {
				for (Entry<String, Object> entry : jo.entrySet()) {
					rect.put(entry.getKey(), entry.getValue());
				}
			}

		}
		return rect;
	}

}
