package com.orisun.mining.util.dao.mongo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.orisun.mining.util.ClassUtil;
import com.orisun.mining.util.dao.Table;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import love.cq.util.StringUtil;

/**
 * 
 * 
 * @author orisun
 * @since 2017年1月12日
 */
public class BaseMongoDao<T extends MongoEntity, K extends Serializable> {

	private static Log logger = LogFactory.getLog(BaseMongoDao.class);
	protected final Class<T> aclass;
	protected final String TABLE;
	protected final String DATABASE;
	private SerializeConfig jsonConfig = new SerializeConfig();

	@SuppressWarnings("unchecked")
	public BaseMongoDao() {
		// 获得超类的泛型参数（即T和PK）的首元素的实际类型（即T在运行时对应的实际类型）
		this.aclass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (aclass.isAnnotationPresent(Table.class)) {
			Table table = (Table) aclass.getAnnotation(Table.class);
			String name = table.name();
			if (name != null) {
				this.TABLE = name;
			} else {
				this.TABLE = "";
			}
		} else {
			this.TABLE = "";
		}
		if (aclass.isAnnotationPresent(MongoDataBase.class)) {
			MongoDataBase database = (MongoDataBase) aclass.getAnnotation(MongoDataBase.class);
			MongoDBName mongoName = database.name();
			if (mongoName != null) {
				this.DATABASE = mongoName.getDbname();
			} else {
				this.DATABASE = "";
			}
		} else {
			this.DATABASE = "";
		}

		if (StringUtil.isBlank(this.TABLE)) {
			logger.fatal("have not specify the table name for " + aclass.getCanonicalName());
		}
		jsonConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
	}

	public String getCollectionName() {
		return TABLE;
	}

	public MongoCollection<Document> getCollection() {
		return MongoClientManager.getMongoClient(aclass).getDatabase(DATABASE).getCollection(TABLE);
	}

	/**
	 * 把一个实例转化为Document，以便写入Mongo。
	 * 
	 * @param entity
	 * @return
	 */
	public Document toDocument(T entity) {
		Document doc = null;
		Field[] fileds = aclass.getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			Field field = fileds[i];
			// 忽略静态成员变量
			if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
				field.setAccessible(true);
				Type fc = field.getGenericType();
				if (fc == null) {
					logger.error("map attribute has no generic type, can not serialize");
					return doc;
				}
				if (fc instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fc;
					Type[] types = pt.getActualTypeArguments();
					try {
						Method getMethod = entity.getClass().getMethod(ClassUtil.parseGetName(field.getName()));
						/**
						 * 如果成员是Map类型，则注意3点
						 * <ol>
						 * <li>key不能为null
						 * <li>key不能为Number类型
						 * <li>key为String类型时，需要把String中的半角.转换为全角．因为mongo的field
						 * name中不允许出现半角.
						 */
						if (Map.class.isAssignableFrom(field.getType())) {
							if (types.length == 2) {
								Class<?> keyType = (Class<?>) types[0];
								// key不能为Number类型
								if (Number.class.isAssignableFrom(keyType)) {
									logger.error("if field is map, the key can not be number");
									return doc;
								}
								@SuppressWarnings("unchecked")
								Map<Object, Object> attribute = (Map<Object, Object>) getMethod.invoke(entity);
								Map<String, Object> newAttr = new HashMap<String, Object>();
								if (attribute != null) {
									// 去除null键
									attribute.remove(null);
									if (String.class.isAssignableFrom(keyType)) {
										for (Entry<Object, Object> entry : attribute.entrySet()) {
											String key = entry.getKey().toString();
											if (!key.toLowerCase().equals("null")) {
												// 半角.转换为全角．
												newAttr.put(key.replaceAll("\\.", "\uff0e"), entry.getValue());
											}
										}
										Method setMethod = entity.getClass()
												.getMethod(ClassUtil.parseSetName(field.getName()), field.getType());
										setMethod.invoke(entity, newAttr);
									}
								}
							}
						}
						/**
						 * 如果成员是List类型，则去除null元素
						 */
						else if (List.class.isAssignableFrom(field.getType())) {
							@SuppressWarnings("unchecked")
							List<Object> attribute = (List<Object>) getMethod.invoke(entity);
							List<Object> newAttr = new ArrayList<Object>();
							if (attribute != null) {
								for (Object ele : attribute) {
									if (ele != null) {
										newAttr.add(ele);
									}
								}
								Method setMethod = entity.getClass().getMethod(ClassUtil.parseSetName(field.getName()),
										field.getType());
								setMethod.invoke(entity, newAttr);
							}
						}
						/**
						 * 如果成员是Set类型，则去除null元素
						 */
						else if (Set.class.isAssignableFrom(field.getType())) {
							@SuppressWarnings("unchecked")
							Set<Object> attribute = (Set<Object>) getMethod.invoke(entity);
							if (attribute != null) {
								attribute.remove(null);
							}
						}
					} catch (NoSuchMethodException | SecurityException | IllegalArgumentException
							| InvocationTargetException | IllegalAccessException e) {
						logger.error("transform collection attribute failed", e);
					}
				}
			}
		}
		String json = JSON.toJSONString(entity, jsonConfig);
		doc = Document.parse(json);
		return doc;
	}

	/**
	 * 将一个Document转换为实体。
	 * 
	 * @param document
	 * @return 反序列化失败时返回null
	 */
	public T deserializeDocument(Document document) {
		String json = document.toJson();
		T entity = null;
		try {
			entity = JSON.parseObject(json, aclass);
		} catch (Exception e) {
			logger.warn("parse json failed: " + json);
		}
		if (entity != null) {
			Field[] fileds = aclass.getDeclaredFields();
			for (int i = 0; i < fileds.length; i++) {
				Field field = fileds[i];
				// 忽略静态成员变量
				if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
					field.setAccessible(true);
					// 如果成员是Map类型，且map的key是String类型，则需要把String中的全角．转换为半角.
					if (Map.class.isAssignableFrom(field.getType())) {
						Type fc = field.getGenericType();
						if (fc == null) {
							logger.error("map attribute has no generic type, can not serialize");
							continue;
						}
						if (fc instanceof ParameterizedType) {
							ParameterizedType pt = (ParameterizedType) fc;
							Type[] types = pt.getActualTypeArguments();
							if (types.length == 2) {
								Class<?> keyType = (Class<?>) types[0];
								// map的key是String类型
								if (String.class.isAssignableFrom(keyType)) {
									try {
										Method method = entity.getClass()
												.getMethod(ClassUtil.parseGetName(field.getName()));
										@SuppressWarnings("unchecked")
										Map<Object, Object> attribute = (Map<Object, Object>) method.invoke(entity);
										Map<String, Object> newAttr = new HashMap<String, Object>();
										if (attribute != null) {
											for (Entry<Object, Object> entry : attribute.entrySet()) {
												// 所把全角的．转成半角的.
												newAttr.put(entry.getKey().toString().replaceAll("\uff0e", "\\."),
														entry.getValue());
											}
											Method setMethod = entity.getClass().getMethod(
													ClassUtil.parseSetName(field.getName()), field.getType());
											setMethod.invoke(entity, newAttr);
										}
									} catch (NoSuchMethodException | SecurityException | IllegalArgumentException
											| InvocationTargetException | IllegalAccessException e) {
										logger.error("transform ． in map key name to . failed", e);
									}
								}

							}
						}
					}
				}
			}
		}
		return entity;
	}

	/**
	 * 按照某个索引列进行“相等”查询
	 * 
	 * @param keyName
	 *            索引列的名称
	 * @param keyValue
	 *            索引列的值
	 * @return 读库超时则返回null
	 */
	public List<T> getByKey(final String keyName, final K keyValue) {
		List<T> rect = new ArrayList<T>();
		try {
			MongoCollection<Document> collection = getCollection();
			MongoCursor<Document> cursor = collection.find(Filters.eq(keyName, keyValue)).iterator();
			while (cursor.hasNext()) {
				Document document = cursor.next();
				T entity = deserializeDocument(document);
				if (entity != null) {
					rect.add(entity);
				}
			}
			cursor.close();
		} catch (Exception e) {
			logger.error("read mongo failed", e);
		}
		return rect;
	}

	/**
	 * 按照某个索引列进行in查询
	 * 
	 * @param keyName
	 * @param targets
	 * @return 读库超时则返回null
	 */
	public List<T> getIn(final String keyName, final List<K> targets) {
		List<T> rect = new ArrayList<T>();
		try {
			MongoCollection<Document> collection = getCollection();
			MongoCursor<Document> cursor = collection.find(Filters.in(keyName, targets)).iterator();
			while (cursor.hasNext()) {
				Document document = cursor.next();
				T entity = deserializeDocument(document);
				rect.add(entity);
			}
			cursor.close();
		} catch (Exception e) {
			logger.error("read mongo failed", e);
		}
		return rect;
	}

	/**
	 * 先批量删除，再批量插入
	 * 
	 * @param keyName
	 *            索引列的名称
	 * @param insertRecords
	 *            索引列==insertRecords.keys()的记录都将被先删除
	 */
	public void batchInsert(String keyName, Map<K, T> insertRecords) {
		try {
			MongoCollection<Document> collection = getCollection();
			// 先批量删除
			collection.deleteMany(Filters.in(keyName, insertRecords.keySet()));
			List<Document> documents = new ArrayList<Document>();
			for (Entry<K, T> entry : insertRecords.entrySet()) {
				T entity = entry.getValue();
				// 在写库的时候给updatetime赋值
				entity.setUpdatetime(new Date());
				Document document = toDocument(entity);
				if (document != null) {
					documents.add(document);
				}
			}
			// 再批量插入
			collection.insertMany(documents);
		} catch (Exception e) {
			logger.error("insert record to mongo failed", e);
		}
	}

	/**
	 * 按索引相等删除一些记录
	 * 
	 * @param keyName
	 * @param keyValue
	 */
	public void deleteByKey(String keyName, K keyValue) {
		try {
			MongoCollection<Document> collection = getCollection();
			collection.deleteMany(Filters.eq(keyName, keyValue));
		} catch (Exception e) {
			logger.error("delete record from mongo failed", e);
		}
	}

	/**
	 * 插入一条记录<br>
	 * 注意：在插入之前请自行决定是否要先删除老数据
	 * 
	 * @param insertRecord
	 */
	public void insert(T insertRecord) {
		try {
			MongoCollection<Document> collection = getCollection();
			// 在写库的时候给updatetime赋值
			insertRecord.setUpdatetime(new Date());
			Document document = toDocument(insertRecord);
			if (document != null) {
				collection.insertOne(document);
			}
		} catch (Exception e) {
			logger.error("insert record to mongo failed, document is " + JSON.toJSONString(insertRecord, jsonConfig),
					e);
		}
	}
}
