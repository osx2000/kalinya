package com.kalinya.exception;

public class FileLockedException extends RuntimeException {
	private static final long serialVersionUID = 3500498045800821839L;
	public FileLockedException(Exception e) {
		super(e);
	}
	public FileLockedException(String s) {
		super(s);
	}
	public FileLockedException() {
		super();
	}
}
