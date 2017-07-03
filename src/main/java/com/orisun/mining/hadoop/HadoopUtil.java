package com.orisun.mining.hadoop;

import com.orisun.mining.util.FileUtil;
import com.orisun.mining.util.Md5Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HadoopUtil {

	private static Log logger = LogFactory.getLog(HadoopUtil.class);
	private static Configuration configuration = null;
	private static ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	private static String confpath = null;
	private static String keyFile = null;
	private static String user = null;
	private static final String KEYTAB_FILE_KEY = "hdfs.keytab.file";
	private static final String USER_NAME_KEY = "hdfs.kerberos.principal";

	static {
		String basepath = com.orisun.mining.util.Path.getCurrentPath();
		confpath = basepath + "/../conf/";
		String dataPath = basepath + "/../data/";
		keyFile = dataPath + "dm.keytab";
		String keyFileMd5 = dataPath + "dm.keytab.md5";
		String digest = Md5Util.md5File(keyFile);
		if (digest != null) {
			List<String> cont = new ArrayList<String>();
			FileUtil.readLines(keyFileMd5, cont);
			if (digest == null || cont == null || cont.size() == 0 || !cont.get(0).equals(digest)) {
				logger.fatal("kerberos key file md5 not correct");
			}
			user = "dm";
			exec.scheduleAtFixedRate(new Thread() {
				@Override
				public void run() {
					createConf();
				}
			}, 0, 1, TimeUnit.HOURS);// 每隔1小时进行一次kerberos认证，每次认证后24小时内有效
		}
	}

	/**
	 * 返回经过kerberos认证的Configuration
	 * 
	 * @return
	 */
	public static Configuration getConf() {
		if (configuration == null) {
			createConf();
		}
		return configuration;
	}

	private static void createConf() {
		Configuration conf = new Configuration(false);
		conf.addResource(new Path(confpath + "core-site.xml"));
		conf.addResource(new Path(confpath + "hdfs-site.xml"));
		conf.addResource(new Path(confpath + "hbase-site.xml"));
		conf.set(KEYTAB_FILE_KEY, keyFile);
		conf.set(USER_NAME_KEY, user);
		UserGroupInformation.setConfiguration(conf);
		boolean success = Kerberos.login(conf, keyFile, user);
		if (success) {
			logger.info("kerberos key file=" + keyFile + ", user=" + user + ", login success");
			configuration = conf;
		} else {
			logger.info("kerberos key file=" + keyFile + ", user=" + user + ", login failed");
		}
	}

}
