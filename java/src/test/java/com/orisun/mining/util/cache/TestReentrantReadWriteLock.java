package com.orisun.mining.util.cache;

import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReentrantReadWriteLock {

	public static void main(String[] args) {
		final ReadWriteLock lock = new ReentrantReadWriteLock();
		final Random rnd = new Random();
		final int 	THREAD_NUM=1000;
		Thread[] threads = new Thread[THREAD_NUM];
		for (int i = 0; i < THREAD_NUM; i++) {
			Thread thread = new Thread() {
				public void run() {
					for (int j = 0; j < 10000; j++) {

						try {
							lock.readLock().lock();
							Thread.sleep(rnd.nextInt(50));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							lock.readLock().unlock();
						}

						try {
							lock.writeLock().lock();
							Thread.sleep(rnd.nextInt(50));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							lock.writeLock().unlock();
						}

						try {
							lock.readLock().lock();
							Thread.sleep(rnd.nextInt(50));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							lock.readLock().unlock();
						}

						System.out.println(this.getName() + " loop " + j);
					}
				}
			};
			thread.setName("T" + i);
			threads[i] = thread;
		}
		for (int i = 0; i < THREAD_NUM; i++) {
			threads[i].start();
		}
		for (int i = 0; i < THREAD_NUM; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
}
