package com.orisun.mining.util.thrift;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * TProtocol对象工厂
 * 
 * @author orisun
 * @date 2017年2月13日
 */
public class TProtocolFactory extends BasePooledObjectFactory<TProtocol> {

	private static Log logger = LogFactory.getLog(TProtocolFactory.class);
	private String host;
	private int port;
	private boolean keepAlive = true;

	public TProtocolFactory(String host, int port, boolean keepAlive) {
		this.host = host;
		this.port = port;
		this.keepAlive = keepAlive;
	}

	/**
	 * 创建一个TProtocol
	 * 
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#create()
	 */
	@Override
	public TProtocol create() throws Exception {
		TTransport transport = new TSocket(host, port);
		transport.open();
		return new TBinaryProtocol(transport);
	}

	/**
	 * 一个对象封装为一个对象池
	 * 
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#wrap(java.lang.Object)
	 */
	@Override
	public PooledObject<TProtocol> wrap(TProtocol protocol) {
		return new DefaultPooledObject<TProtocol>(protocol);
	}

	/**
	 * 激活一个对象，borrowObject时触发
	 * 
	 * @throws TTransportException
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#activateObject(org.apache.commons.pool2.PooledObject)
	 */
	@Override
	public void activateObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
		if (!pooledObject.getObject().getTransport().isOpen()) {
			pooledObject.getObject().getTransport().open();
		}
	}

	/**
	 * 让一个对象从激活态变为非激活态，returnObject时触发
	 * 
	 * @throws TTransportException
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#passivateObject(org.apache.commons.pool2.PooledObject)
	 */
	@Override
	public void passivateObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
		if (!keepAlive) {
			pooledObject.getObject().getTransport().flush();
			pooledObject.getObject().getTransport().close();
		}
	}

	/**
	 * 验证对象有效性
	 * 
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#validateObject(org.apache.commons.pool2.PooledObject)
	 */
	@Override
	public boolean validateObject(PooledObject<TProtocol> pooledObject) {
		if (pooledObject.getObject() != null) {
			if (pooledObject.getObject().getTransport().isOpen()) {
				return true;
			}
			try {
				pooledObject.getObject().getTransport().open();
				return true;
			} catch (TTransportException e) {
				logger.error("open thrift transport failed", e);
			}
		}
		return false;
	}

	/**
	 * 销毁对象，clear时触发
	 * 
	 * @throws TTransportException
	 * @see org.apache.commons.pool2.BasePooledObjectFactory#destroyObject(org.apache.commons.pool2.PooledObject)
	 */
	@Override
	public void destroyObject(PooledObject<TProtocol> pooledObject) throws TTransportException {
		passivateObject(pooledObject);
		pooledObject.markAbandoned();
	}
}
