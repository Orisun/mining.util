package com.orisun.mining.util.serializer;

import java.io.*;

public class JavaSerializer {

	/**
	 * 通过ObjectStream实现java对象的深拷贝。<br>
	 * 注意：要拷贝的object必须实现Serializable接口
	 * 
	 * @param obj
	 *            必须实现Serializable接口。如果obj是List，则它不是由通过List.subList()得来的
	 *            。因为List.subList()返回的是一个RandomAccessSubList实例
	 *            ,在反序列化时ObjectOutputStream.writeObject(RandomAccessSubList)会出错
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deepCopy(Object obj) throws IOException, ClassNotFoundException {
		// 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);// 要写入ObjectOutputStream的话必须实现Serializable接口
		// 将流序列化成对象
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		return ois.readObject();
	}
}
