package com.orisun.mining.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 
 * @Author:orisun
 * @Since:2015-6-10
 * @Version:1.0
 */
public class NIC {

	private static Log logger = LogFactory.getLog(NIC.class);

	/**
	 * 获取本机MAC
	 * 
	 * @return
	 */
	public static String getMacAddr() {
		String MacAddr = "";
		String str = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (ni.isLoopback()) {
					continue;
				} else {
					byte[] buf = ni.getHardwareAddress();
					if (buf != null) {
						for (int i = 0; i < buf.length; i++) {
							str = str + DataTransform.byteHEX(buf[i]) + ":";
						}
						MacAddr = str.substring(0, str.length() - 1).toUpperCase();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return MacAddr;
	}

	/**
	 * 获取本机的机器名
	 * 
	 * @return
	 */
	public static String getLocalHostName() {
		InetAddress addr = null;
		String hostName = "";
		try {
			addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hostName;
	}

	/**
	 * 获取本机的内网IP
	 * 
	 * @return
	 */
	public static String getLocalIP() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) allNetInterfaces.nextElement();
				// lo是Local Loopback，TUN/TAP设备是linux内核中实现的虚拟网卡
				if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia != null && ia instanceof Inet4Address) {
							ip = ia.getHostAddress();
							break;
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		logger.info("self ip is " + ip);
		return ip;
	}

	/**
	 * 判断本机是否为开发机
	 * 
	 * @return
	 */
	public static boolean devMachine() {
		String selfIP = getLocalIP();
		return "10.9.5.115".equals(selfIP) || "10.7.0.174".equals(selfIP);
	}

	/**
	 * 判断本机是否为测试机
	 * 
	 * @return
	 */
	public static boolean testMachine() {
		String selfIP = getLocalIP();
		return selfIP.startsWith("10.1.200");
	}

	/**
	 * 判断本机是否为线上机器
	 * 
	 * @return
	 */
	public static boolean onlineMachine() {
		String selfIP = getLocalIP();
		return !("10.9.5.115".equals(selfIP) || "10.7.0.174".equals(selfIP) || selfIP.startsWith("10.1.200"));
	}

	public static void main(String[] args) {
		System.out.println("selfIP:" + getLocalIP());
		System.out.println("selfName:" + getLocalHostName());
		System.out.println("selfMAC:" + getMacAddr());
		System.out.println("this is develop machine:" + NIC.devMachine());
		System.out.println("this is test machine:" + NIC.testMachine());
		System.out.println("this is online machine:" + NIC.onlineMachine());
	}
}
