package com.celldevourersocket.main;


public class SocketServerTest {
	

	public static void main(String[] args) {
		new Thread(new SocketServer()).start();
	}
}
