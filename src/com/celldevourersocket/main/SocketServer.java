package com.celldevourersocket.main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.celldevourersocket.control.BallControl;
import com.celldevourersocket.entry.MyBall;
import com.celldevourersocket.util.Code;

public class SocketServer implements Runnable {
	public static final int PORT = 12345;
	public BallControl cm = BallControl.getInstance();
	
	@Override
	public void run() {
		try {
			// 创建一个DatagramSocket对象，并指定监听的端口号
			DatagramSocket socket = new DatagramSocket(PORT);
			while(true){
				System.out.println("成功监听"+PORT+"socket"+socket);
				byte data[] = new byte[2048];
				// 创建一个空的DatagramPacket对象
				DatagramPacket packet = new DatagramPacket(data, data.length);
				// 使用receive方法接收客户端所发送的数据，
				// 如果客户端没有发送数据，该进程就停滞在这里
				socket.receive(packet);
				String result = new	String(packet.getData(), packet.getOffset(),packet.getLength());
				int port = packet.getPort();
				InetAddress address = packet.getAddress();
				String sendMessage132 = "host:" + address.getHostAddress() + ",port:" + port;
				System.out.println("result--->" + result+"  sendMessage132--->"+sendMessage132);
				//解析返回的数据
				JSONObject json = JSONObject.parseObject(result);
				if(json.containsKey("status")){
					int status = json.getInteger("status");
					if(Code.GAME_STATUS_START==status){
						MyBall mball = null;
						boolean flag = true;
						while(flag){
							mball = new MyBall(cm.getRandomNumX(), cm.getRandomNumY(), 30, 0, 0, 255, 12, 158, 255, System.currentTimeMillis(), "GYB"+System.currentTimeMillis(),InetAddress.getByName(address.getHostAddress()),port,socket);
							if(mball.checkAroundHaveBall()){
								flag = false;
								cm.addMyBall(mball.getId(),mball);
								cm.publish(mball);
							}
						}
					}else if(Code.GAME_STATUS_OVER==status){//接收到游戏结束
						MyBall mball =JSON.parseObject(json.getString("data"), MyBall.class);
						if(mball!=null){
							if(cm.deleteMyBall(mball)){
								System.out.println("删除信息成功");
							}else{
								System.out.println("删除信息失败");
							}
						}else{
							System.out.println("删除信息失败 客户端未传递用户信息过来");
						}
					}else if(Code.GAME_CHANGEV==status){
						MyBall mball =JSON.parseObject(json.getString("data"), MyBall.class);
						if(mball!=null){
							mball = cm.changeV(mball);
							cm.publish(mball, Code.GAME_CHANGEV);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
