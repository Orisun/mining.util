package com.orisun.mining.util.monitor;

import com.orisun.mining.util.UdpServer;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestKVreport {

	@BeforeClass
	public static void setup() {
		UdpServer server = new UdpServer(KVreport.DEST_PORT);
		server.start();
	}

	@Test
	public void send() {
		KVreport sender1 = KVreport.getReporter();
		sender1.send(1, 58.9);
		sender1.send(1, 58.9);
		sender1.send(1, 58.9);
		sender1.send(1, 58.9);
		KVreport sender2 = KVreport.getReporter();
		sender2.send(2, 58.9);
		sender2.send(2, 58.9);
		sender2.send(2, 58.9);
		sender2.send(2, 58.9);
	}
}
