package com.celldevourersocket.entry;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.celldevourersocket.control.BallControl;
import com.celldevourersocket.util.Code;

/**
 * 操纵的球 
 * @author Gongyb 下午3:17:09
 */
public class MyBall extends Ball {

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 出生距离
	 */
	public static final int BIRTH_SAFETY_DISTANCE = 150;
	private long id;
	private String ballName = "";
	private DatagramSocket socket;
	public double v = 5;
	public double xv = 0;
	public double getXv() {
		return xv;
	}
	public void setXv(double xv) {
		this.xv = xv;
	}
	public double getYv() {
		return yv;
	}
	public void setYv(double yv) {
		this.yv = yv;
	}


	public double yv = 0;
	public double size ; //小球的面积
	public double r;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBallName() {
		return ballName;
	}
	public void setBallName(String ballName) {
		this.ballName = ballName;
	}
	public MyBall(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue, long id, String ballName) {
		super(x, y, alpha, red, green, blue);
		this.r = r;
		this.id = id;
		this.ballName = ballName;
		this.xv = xv;
		this.yv = yv;
		this.size = Math.PI*r*r;
	}
	
	public MyBall() {
		super();
	}
	public MyBall(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue, long id, String ballName, InetAddress address,int port,DatagramSocket socket) {
		super(x, y, alpha, red, green, blue);
		this.r = r;
		this.id = id;
		this.ballName = ballName;
		this.socket = socket;
		this.address = address;
		this.port = port;
		this.xv = xv;
		this.yv = yv;
		this.size = Math.PI*r*r;
	}
	/**
	 * 改变球的速度 方向和速度 
	 * @param targetX   将要到达的xy
	 * @param targetY
	 */
	public void chengeV(double targetX,double targetY){
		double a = targetX-x;
		double b =  targetY-y;
		double targetV = Math.sqrt(a*a+b*b);
		double c = targetV/v;
		xv = a/c;
		yv =  b/c;
	}
	/**
	 * 移动  在边界的时候会判断
	 */
	public void move(){
		x += xv;
		y += yv;
		
		if(x+r>Code.GAME_WIDTH){
			x = Code.GAME_WIDTH - r;
			xv = 0;
		}else if(x-r<0){
			x = r;
			xv = 0;
		}
		if(y+r>Code.GAME_HEIGHT){
			y = Code.GAME_HEIGHT - r;
			yv = 0;
		}else if(y-r<0){
			y = r;
			yv = 0;
		}
	}
	/**
	 * 检查是否吞噬  
	 * @param ball  要吞噬的球
	 * @return  true 吞噬  false 未吞噬
	 */
	public boolean checkDevour(Ball ball){
		double a = ball.getX()-x;
		double b =  ball.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s){
			size += Ball.SIZE;
			r = Math.sqrt((size)/Math.PI);
			return true;
		}
		return false;
	}
	/**
	 * 检查玩家吞噬or 被吞噬  
	 * @param ball 另一个玩家
	 * @return  被吞噬的玩家
	 */
	public MyBall checkDevour(MyBall mball){
		double a = mball.getX()-x;
		double b =  mball.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s||mball.r>s){
			if(size>mball.size){
				size += mball.size;
				r = Math.sqrt((size)/Math.PI);
				return mball;
			}else{
				mball.size += size;
				mball.r = Math.sqrt((mball.size)/Math.PI);
				return this;
			}
		}
		return null;
	}
	/**
	 * 检查是否吞噬  
	 * @param ball  要吞噬的球
	 * @return  true 吞噬  false 未吞噬
	 */
	public boolean checkDevour(Point point){
		double a = point.getX()-x;
		double b =  point.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s){
			size += Ball.SIZE;
			r = Math.sqrt((size)/Math.PI);
			return true;
		}
		return false;
	}
	/**
	 * 球刚生成的时候需要保证周围没有球
	 * @return  true 能出生   false 不能出生
	 */
	public boolean checkAroundHaveBall(){
		Map<Long,MyBall> map = BallControl.getInstance().getMyBall();
		if(map.size()==0){
			return true;
		}
		Set<Entry<Long,MyBall>> entrySet = map.entrySet();
		for(Entry<Long,MyBall> e  : entrySet){
			MyBall mball = e.getValue();
			double x = mball.getX();
			double y = mball.getY();
			double c = (this.x-x)*(this.x-x)+(this.y-y)*(this.y-y);
			if(c>(BIRTH_SAFETY_DISTANCE*BIRTH_SAFETY_DISTANCE)){
				return true;
			}
		}
		return false;
	}
	
	
	private InetAddress address;
	private int port;
	
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * 将信息发送到客户端
	 * @param <T>
	 * @param msg
	 */
	public <T> void sendMsgToClient(T t,int status) {
		try {
			String obj = "";
			if(status==Code.GAME_STATUS_BALL_INFO){
				Result<List<Point>> result = new Result<>();
				result.setStatus(Code.GAME_STATUS_BALL_INFO);
				result.setData((List<Point>)t);
				obj = JSON.toJSONString(result);
//				System.out.println("食物信息:"+obj);
			}else if(status==Code.GAME_STATUS_USER_INFO){
				Result<Map<Long,MyBall>> result = new Result<>();
				result.setStatus(Code.GAME_STATUS_USER_INFO);
				result.setData((Map<Long,MyBall>)t);
				obj = JSON.toJSONString(result);
//				System.out.println("玩家信息:"+obj);
			}else if(status==Code.GAME_STATUS_MY_INFO){
				Result<MyBall> result = new Result<>();
				result.setStatus(Code.GAME_STATUS_MY_INFO);
				result.setData((MyBall)t);
				obj = JSON.toJSONString(result);
				JSONObject jsonObj = JSONObject.parseObject(obj);
				jsonObj.put("GAME_WIDTH", Code.GAME_WIDTH);
				jsonObj.put("GAME_HEIGHT", Code.GAME_HEIGHT);
				jsonObj.put("GAME_REFRESH_RATE", Code.GAME_REFRESH_RATE);
				obj = JSON.toJSONString(jsonObj);
//				System.out.println("个人信息:"+obj);
			}else if(status==Code.GAME_CHANGEV){
				Result<MyBall> result = new Result<>();
				result.setStatus(Code.GAME_CHANGEV);
				result.setData((MyBall)t);
				obj = JSON.toJSONString(result);
				System.out.println("玩家速度改变:"+obj);
			}else if(status==Code.GAME_STATUS_OVER){
				Result<MyBall> result = new Result<>();
				result.setStatus(Code.GAME_STATUS_OVER);
				result.setData((MyBall)t);
				obj = JSON.toJSONString(result);
				System.out.println("玩家已被吞噬:"+obj);
			}
			byte data[] = obj.getBytes("UTF-8");
			// 将数据打包
			DatagramPacket packet = new DatagramPacket(data, data.length,address, port);
			// 利用socket发送数据到客户端
			socket.send(packet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
