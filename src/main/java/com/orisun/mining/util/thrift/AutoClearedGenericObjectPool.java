package com.orisun.mining.util.thrift;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * 对象池
 * 
 * @author orisun
 * @date 2017年2月13日
 */
public class AutoClearedGenericObjectPool<T> extends GenericObjectPool<T> {

	public AutoClearedGenericObjectPool(PooledObjectFactory<T> factory) {
		super(factory);
	}

	public AutoClearedGenericObjectPool(PooledObjectFactory<T> factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}

	/**
	 * 归还对象时，如果发现空闲数>=激活数，则清理掉当前要归还的对象
	 * 
	 * @see org.apache.commons.pool2.impl.GenericObjectPool#returnObject(java.lang.Object)
	 */
	@Override
	public void returnObject(T obj) {
		super.returnObject(obj);
		if (getNumIdle() >= getNumActive()) {
			clear();
		}
	}

}
