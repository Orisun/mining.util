package com.orisun.mining.util.logger;

import com.orisun.mining.util.NIC;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.net.SocketException;

/**
 * log4j自带的SocketAppender是TCP方式，这里实现一种UDP的方式。
 * 
 * @Author:orisun
 * @Since:2016-6-2
 * @Version:1.0
 */
public class UdpAppender extends AppenderSkeleton {

	private String host;// 日志发送到这台服务器上
	private int port;// 日志发送到这台服务器的这个端口上
	private String encoding;

	private UDPClient udpClient = null;
	private boolean haveInitUdp = false;

	private void initUdpClients() {
		try {
			udpClient = new UDPClient(host, port, encoding);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		haveInitUdp = true;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (!isAsSevereAsThreshold(event.getLevel())) {
			return;
		}

		StringBuilder builder = new StringBuilder();

		String packet;

		if (layout == null) {
			packet = String.valueOf(event.getMessage());
		} else {
			packet = layout.format(event);
		}
        //获取本机名,本机名有效时加入发送内容，否则不添加
		String hostName=NIC.getLocalHostName();
		if(hostName != null && hostName.length()>0){
		    builder.append(hostName);
		}else{
		    builder.append("Unknown");  //主机名不可知时，补充“Unknown”标记    
		}
		//水平制表符作为间隔符使用
		builder.append("\t"); 
		builder.append(packet);

		if (layout == null || layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				for (int i = 0; i < s.length; i++) {
					builder.append(s[i]).append("\n");
				}
			}
		}
		try {
			if (!haveInitUdp) {
				initUdpClients();
			}
			udpClient.sendMsg(builder.toString());
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (haveInitUdp) {
			udpClient.close();

		}
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
