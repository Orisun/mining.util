package com.orisun.mining.util.logger;

import com.orisun.mining.util.Md5Util;
import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.cache.TimeoutCache;
import com.orisun.mining.util.monitor.KVreport;
import com.orisun.mining.util.monitor.SendMail;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import java.util.concurrent.TimeUnit;

public class MonitorAppender extends DailyRollingFileAppender {

	private KVreport errorReporter = KVreport.getReporter();
	private int logErrorKey = SystemConfig.getIntValue("log_error_key", -1);
	private TimeoutCache<String, Integer> alarmLocation = new TimeoutCache<String, Integer>();

	@Override
	public void append(LoggingEvent event) {
		if (errorReporter != null
				&& event.getLevel().toInt() >= Priority.ERROR_INT) {
			errorReporter.send(logErrorKey, 1);
			if (event.getLevel().toInt() >= Priority.FATAL_INT) {
				LocationInfo location = event.getLocationInformation();
				String line = location.getClassName() + ":"
						+ location.getMethodName() + "-"
						+ location.getLineNumber();
				String digest = Md5Util.md5(line);
				// 来自同一个文件同一行的报警10分钟内只发一次
				if (alarmLocation.get(digest) == null) {
					SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
							SystemConfig.getValue("mail_receiver"), event
									.getMessage().toString());
					alarmLocation.put(digest, 1, 10, TimeUnit.MINUTES);
				}
			}
		}
		super.append(event);
	}
}
