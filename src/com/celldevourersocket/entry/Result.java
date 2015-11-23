package com.celldevourersocket.entry;

public class Result<T> {
	private int status;
	private T data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T t) {
		this.data = t;
	}

}
