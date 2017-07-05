package com.orisun.mining.util.monitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

/**
 * KV数据上报
 * 
 * @Author:orisun
 * @Since:2016-1-13
 * @Version:1.0
 */
public class KVreport {

	public static final int DEST_PORT = 22345;
	public static final int SRC_PORT = 0; // 端口号定为0，则系统会随机分配一个可用的端口号
	private DatagramSocket dataSocket;

	private volatile static KVreport instance = null;

	private KVreport() throws SocketException {
		this.dataSocket = new DatagramSocket(SRC_PORT);
	}

	/**
	 * 必须是单例，因为UDP端口不能共用<br>
	 * 
	 * {@see http://crud0906.iteye.com/blog/576321}
	 * 
	 * @return
	 */
	public static KVreport getReporter() {
		if (instance == null) {
			synchronized (KVreport.class) {
				if (instance == null) {
					try {
						instance = new KVreport();
					} catch (SocketException e) {
						try {
							instance = new KVreport();
						} catch (SocketException e2) {
							return null;
						}
					}
				}
			}
		}
		return instance;
	}

	public void send(int key, double value) {
		if (key <= 0) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\"{'ts':");
		sb.append(new Date().getTime());
		sb.append(",'key':'");
		sb.append(key);
		sb.append("','value':");
		sb.append(value);
		sb.append("}\"");
		byte[] sendDataByte = new byte[1024];
		sendDataByte = sb.toString().getBytes();
		try {
			DatagramPacket dataPacket = new DatagramPacket(sendDataByte,
					sendDataByte.length, InetAddress.getByName("localhost"),
					DEST_PORT);
			dataSocket.send(dataPacket);
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
