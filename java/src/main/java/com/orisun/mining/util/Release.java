package com.orisun.mining.util;

/**
 ** JVM退出时会执行ShutdownHook中的代码，进行一些清理工作，比如释放资源。以下情况会导致执行hook:
 * <ol>
 * <li>程序正常退出
 * <li>使用System.exit()
 * <li>终端使用Ctrl+C触发的中断
 * <li>系统关闭
 * <li>使用Kill pid命令干掉进程
 * </ol>
 * 注意：在使用kill -9 pid是不会JVM注册的hook不会被调用
 * 
 * @Author:orisun
 * @Since:2015-9-25
 * @Version:1.0
 */
public abstract class Release {

	/**
	 * JVM退出时释放资源
	 */
	public abstract void releaseResource();

	public void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				releaseResource();
			}
		});
	}
}
