package com.celldevourersocket.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.celldevourersocket.entry.MyBall;
import com.celldevourersocket.entry.Point;
import com.celldevourersocket.util.Code;
/**
 * 球的控制类
 * @author Gongyb 下午3:22:01
 */
public class BallControl {
	/**
	 * 玩家的食物球
	 */
	private List<Point> balls;
	/**
	 * 玩家操作的球
	 */
	private Map<Long,MyBall> mmap;
	private static final BallControl cm = new BallControl();
	private Random rand = new Random();
	private Timer timer_lifecycle;
	private TimerTask task_lifecycle ;
	private Timer timer_sendinfo;
	private TimerTask task_sendinfo ;
	
	public static BallControl getInstance(){
		return cm;
	}
	private BallControl(){
		balls = new ArrayList<Point>();
		mmap = new ConcurrentHashMap<Long,MyBall>();
		for (int i = 0; i < 50; i++) {
			balls.add(new Point(getRandomNumX(), getRandomNumY()));
		}
		startGameLifecycle();
		startSendGameInfo();
	};
	/**
	 * 发送游戏信息至客户端
	 */
	public void startSendGameInfo(){
		if(timer_sendinfo==null){
			timer_sendinfo = new Timer();
			task_sendinfo = new TimerTask() {
				@Override
				public void run() {
					publish();
				}
			};
			timer_sendinfo.schedule(task_sendinfo, 0,Code.GAME_SENDINFO_RATE);
		}
	}
	/**
	 * 开始模拟 小球移动
	 */
	public void startGameLifecycle(){
		if(timer_lifecycle==null){
			timer_lifecycle = new Timer();
			task_lifecycle = new TimerTask() {
				@Override
				public void run() {
					gameWorking();
				}
			};
			timer_lifecycle.schedule(task_lifecycle, 0,Code.GAME_REFRESH_RATE);
		}
	}
	/**
	 * 停止模拟 小球移动
	 */
	public void stopGameLifecycle(){
		if(timer_lifecycle!=null){
			timer_lifecycle.cancel();
			timer_lifecycle = null;
		}
	}
	/**
	 * 游戏运行
	 */
	public void gameWorking(){
		Set<Entry<Long, MyBall>> entrySet = mmap.entrySet();
		for(Entry<Long, MyBall> e :entrySet){
			MyBall mball = e.getValue();
			mball.move();
			eatBall(mball);
			checkPlayerEatOtherPlayer(mball);
		}
	}
	/**
	 * 判断是否被其他玩家吃掉
	 * @param mball
	 */
	private void checkPlayerEatOtherPlayer(MyBall mball) {
		for(Entry<Long,MyBall> e:mmap.entrySet()){
			if(e.getValue()!=mball){
				MyBall m;
				if((m =e.getValue().checkDevour(mball))!=null){
					m.sendMsgToClient(m, Code.GAME_STATUS_OVER);
					if(deleteMyBall(m)){
						System.out.println("吞噬删除成功!");
					}else{
						System.out.println("吞噬删除失败!");
					}
				}
			}
		}
	}
	/**
	 * 玩家吃小球
	 * @param mball
	 */
	public void eatBall(MyBall mball) {
		for (int j = 0; j < balls.size(); j++) {
			if(mball.checkDevour(balls.get(j))){
				balls.remove(j);
			}
		}
	}
	/**
	 * 添加玩家信息
	 * @param ball  玩家
	 */
	public boolean addMyBall(long id,MyBall ball){
		if(!mmap.containsKey(ball.getId())){
			mmap.put(id,ball);
			return true;
		}else{
			addMyBall(System.currentTimeMillis(),ball);
		}
		return false;
	}
	/**
	 * 游戏开始   将服务器的游戏信息发送给 这个玩家
	 * @param m  这个玩家
	 */
	public void publish(MyBall m){
		m.sendMsgToClient(m, Code.GAME_STATUS_MY_INFO); 
//		m.sendMsgToClient(balls, Code.GAME_STATUS_BALL_INFO);
//		m.sendMsgToClient(mmap, Code.GAME_STATUS_USER_INFO);
	}
	/**
	 * 向所有人发送无差别消息
	 * @param m
	 */
	public void publish(){
		Set<Entry<Long, MyBall>> entrySet = mmap.entrySet();
		for(Entry<Long, MyBall> e :entrySet){
			MyBall mball = e.getValue();
			mball.sendMsgToClient(balls, Code.GAME_STATUS_BALL_INFO);
			mball.sendMsgToClient(mmap, Code.GAME_STATUS_USER_INFO);
		}
	}
	public void publish(MyBall m,int codeStatus){
		m.sendMsgToClient(m, codeStatus); 
	}
	/**
	 * 删除这个玩家信息
	 * @param sc   玩家
	 * @return  是否成功
	 */
	public boolean deleteMyBall(MyBall sc){
		if(mmap!=null){
			if(mmap.containsKey(sc.getId())){
				System.out.println("删除的球:"+mmap.remove(sc.getId()));
				return true;
			}
		}
		return false;
	}
	
	public Map<Long,MyBall> getMyBall(){
		return mmap;
	}
	
	/**
	 * 在地图上随机取一个X值
	 * @return
	 */
	public int getRandomNumX(){
		return rand.nextInt(Code.GAME_WIDTH);
	}
	/**
	 * 在地图上随机取一个Y值
	 * @return
	 */
	public int getRandomNumY(){
		return rand.nextInt(Code.GAME_HEIGHT);
	}
	/**
	 * 改变球的速度
	 * @param mball
	 */
	public MyBall changeV(MyBall mball) {
		MyBall m = mmap.get(mball.getId());
		m.setXv(mball.getXv());
		m.setYv(mball.getYv());
		return m;
	}
	
}
