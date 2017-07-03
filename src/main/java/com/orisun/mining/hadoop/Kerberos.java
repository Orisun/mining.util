package com.orisun.mining.hadoop;

import love.cq.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class Kerberos {

	private static Log logger = LogFactory.getLog(Kerberos.class);
	private static final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
	private static final String USER_NAME_KEY = "hdfs.kerberos.principal";

	/**
	 * 进行kerberos认证。keyFile和userName使用core-site.xml中的默认配置
	 * 
	 * @param conf
	 * @throws IOException
	 */
	public static void login(Configuration conf) throws IOException {
		if (UserGroupInformation.isSecurityEnabled()) {
			try {
				SecurityUtil.login(conf, KEYTAB_FILE_KEY, USER_NAME_KEY);
			} catch (IOException e) {
				logger.fatal("kerberos login failed", e);
				throw e;
			}
		}
	}

	/**
	 * 进行kerberos认证
	 * 
	 * @param conf
	 * @param keyFile
	 * @param userName
	 * @throws IOException
	 */
	public static boolean login(Configuration conf, String keyFile, String userName) {
		if (UserGroupInformation.isSecurityEnabled()) {
			if (!StringUtil.isBlank(keyFile)) {
				conf.set(KEYTAB_FILE_KEY, keyFile);
			}
			if (!StringUtil.isBlank(userName)) {
				conf.set(USER_NAME_KEY, userName);
			}

			int fail = 0;
			boolean success = false;
			Exception e = null;
			while (fail++ < 3) {
				try {
					SecurityUtil.login(conf, KEYTAB_FILE_KEY, USER_NAME_KEY);
					success = true;
					break;
				} catch (IOException e1) {
					e = e1;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e2) {
					}
				}
			}
			if (!success) {
				logger.fatal("kerberos login failed", e);
			}
			return success;
		} else {
			logger.fatal("kerberos auth is not enabled");
			return true;
		}
	}
}
