package com.github.octopus.aggregator.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

/**
 * Provides functional wrappers around a {@link ReadWriteLock}
 * @author Eric Deandrea
 */
public final class FunctionalReadWriteLockGuard {
	private final ReadWriteLock readWriteLock;

	public FunctionalReadWriteLockGuard(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	/**
	 * Invokes a read operation within a {@link ReadWriteLock#readLock()}
	 * @param readBlock The {@link Supplier} to supply a value during a {@link ReadWriteLock#readLock()}
	 * @param <T> The type of value read
	 * @return The value read
	 */
	public <T> T doInReadLock(Supplier<T> readBlock) {
		Lock readLock = this.readWriteLock.readLock();
		readLock.lock();

		try {
			return readBlock.get();
		}
		finally {
			readLock.unlock();
		}
	}

	/**
	 * Invokes a read operation which does not return a value
	 * @param readBlock The block to execute during a {@link ReadWriteLock#readLock()}
	 */
	public void doInReadLock(Runnable readBlock) {
		Lock readLock = this.readWriteLock.readLock();
		readLock.lock();

		try {
			readBlock.run();
		}
		finally {
			readLock.unlock();
		}
	}

	/**
	 * Invokes a write operation within a {@link ReadWriteLock#writeLock()}
	 * @param writeBlock The {@link Supplier} to supply a value during a {@link ReadWriteLock#writeLock()}
	 * @param <T> The type of value written
	 * @return The value written
	 */
	public <T> T doInWriteLock(Supplier<T> writeBlock) {
		Lock writeLock = this.readWriteLock.writeLock();
		writeLock.lock();

		try {
			return writeBlock.get();
		}
		finally {
			writeLock.unlock();
		}
	}

	/**
	 * Invokes a write operation which does not return a value
	 * @param writeBlock The block to execute during a {@link ReadWriteLock#writeLock()}
	 */
	public void doInWriteLock(Runnable writeBlock) {
		Lock writeLock = this.readWriteLock.writeLock();
		writeLock.lock();

		try {
			writeBlock.run();
		}
		finally {
			writeLock.unlock();
		}
	}
}
