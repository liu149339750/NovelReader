package com.lw.novel.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoolSet {

	private boolean b;
	private Lock mLock = new ReentrantLock();

	public BoolSet(boolean b) {
		this.b = b;
	}

	public void lockThread() {
		synchronized (this) {
			if (!b) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean getValue() {
		return b;
	}

	public void setValue(boolean b) {
		synchronized (this) {
			this.b = b;
			if (b) {
				notify();
			}
		}
	}
}
