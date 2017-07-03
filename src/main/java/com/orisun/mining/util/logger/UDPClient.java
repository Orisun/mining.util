package com.orisun.mining.util.logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * 
 * @Author:orisun
 * @Since:2016-6-2
 * @Version:1.0
 */
public class UDPClient {
	private String encoding;
	private DatagramSocket socket = null;
	private InetSocketAddress address = null;

	public UDPClient(String ip, int port, String encoding)
			throws SocketException {
		this.encoding = encoding;
		this.socket = new DatagramSocket();
		// port是目标机的端口，非本地端口。每次new InetSocketAddress会从本地随机选择一个未使用的端口
		this.address = new InetSocketAddress(ip, port);
	}

	public void close() {
		socket.close();
	}

	/**
	 * 
	 * @param msg
	 * @throws Exception
	 *             普通局域网(以太网)环境下，应用层发送的最大数据长度(MTU)为1472字节，大于该长度时会自动进行分片(
	 *             fragmentation),在接收方对分片进行重组。由于UDP不保证顺序性，所以可能会重组失败从而导致整个数据包丢失。<br>
	 *             报文太长时在发送端对其进行拆分是没有意义的，因为到接收方一条日志被放到在了不同的行内，而且不保证顺序性，日志解析时会出错。
	 */
	public void sendMsg(String msg) throws Exception {
		byte[] buffer = msg.getBytes(encoding);
		if (buffer.length > 1472) {
			System.err.println("udp data length is " + buffer.length
					+ ", more than mtu 1472, and will be truncated.");
			msg=msg.substring(0,400);
			buffer = msg.getBytes(encoding);
		}
		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, address);
		socket.send(dp);
	}

	public static void main(String[] args) throws Exception {
		String msg = "";
		for (int i = 0; i < 64; i++) {
			msg += "UDPClient send message test.";
		}
		for (int i = 0; i < 10; i++) {
			UDPClient client = new UDPClient("192.168.119.96", 17058, "UTF-8");
			client.sendMsg("l:" + i + ":" + msg + "\r\n");
			Thread.sleep(100);
			System.out.println(i);
		}
	}
}
