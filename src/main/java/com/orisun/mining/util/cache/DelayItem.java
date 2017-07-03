package com.orisun.mining.util.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @Author orisun
 * @Since 2015-10-9
 * @Version 1.0
 */
public class DelayItem<T> implements Delayed {

	private static final long ORIGIN = System.nanoTime();// 记录进入队列的时刻
	private static final AtomicLong sequencer = new AtomicLong(0);
	private final long sequenceNumber;
	private final long time;
	private final T item;

	final static long now() {
		return System.nanoTime() - ORIGIN;
	}

	/**
	 * 
	 * @param submit
	 *            队列中的元素类型
	 * @param timeout
	 *            元素在队列中存活的时间，单位：毫秒
	 */
	public DelayItem(T submit, long timeout) {
		this.time = now() + timeout;// 出队时刻
		this.item = submit;// 入队元素
		this.sequenceNumber = sequencer.getAndIncrement();// 在队列中的编号
	}

	public T getItem() {
		return this.item;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long d = unit.convert(time - now(), TimeUnit.NANOSECONDS);
		return d;
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this)
			return 0;
		if (other instanceof DelayItem) {
			DelayItem<?> x = (DelayItem<?>) other;
			long diff = time - x.time;
			if (diff < 0)
				return -1;
			else if (diff > 0)
				return 1;
			else if (sequenceNumber < x.sequenceNumber) // 如果是同时进入队列的，则先进者先出
				return -1;
			else
				return 1;
		}
		long d = (getDelay(TimeUnit.NANOSECONDS) - other
				.getDelay(TimeUnit.NANOSECONDS));
		return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
	}
}
