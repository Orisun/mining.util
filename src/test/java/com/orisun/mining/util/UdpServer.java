package com.orisun.mining.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer extends Thread {

	private int port;

	public UdpServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			DatagramSocket dataSocket = new DatagramSocket(port);
			byte[] receiveByte = new byte[1024];
			DatagramPacket dataPacket = new DatagramPacket(receiveByte,
					receiveByte.length);
			String receiveStr = "";
			int i = 0;
			while (i == 0)// 无数据，则循环
			{
				dataSocket.receive(dataPacket);
				i = dataPacket.getLength();
				// 接收数据
				if (i > 0) {
					// 指定接收到数据的长度,可使接收数据正常显示,开始时很容易忽略这一点
					receiveStr = new String(receiveByte, 0,
							dataPacket.getLength());
					System.out.print(receiveStr);
					i = 0;// 循环接收
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}