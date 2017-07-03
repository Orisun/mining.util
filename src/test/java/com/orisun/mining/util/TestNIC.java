package com.orisun.mining.util;

import org.junit.Test;

public class TestNIC {

	@Test
	public void testIP() {
		String ip = NIC.getLocalIP();
		System.out.println(ip);
	}

	@Test
	public void testMAC() {
		String mac = NIC.getMacAddr();
		System.out.println(mac);
	}

	@Test
	public void testHostName() {
		String host = NIC.getLocalHostName();
		System.out.println(host);
	}
}