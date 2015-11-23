package com.celldevourersocket.util;

public class Code {
	/**游戏界面 宽*/
	public static final int GAME_WIDTH = 3000;
	/**游戏界面 高*/
	public static final int GAME_HEIGHT = 3000;
	
	/**游戏开始状态*/
	public static final int GAME_STATUS_START = 10000;
	/**玩家信息*/
	public static final int GAME_STATUS_USER_INFO = 10001;
	/**食物信息*/
	public static final int GAME_STATUS_BALL_INFO = 10002;
	/**个人信息与游戏配置*/
	public static final int GAME_STATUS_MY_INFO = 10003;
	/**gameover*/
	public static final int GAME_STATUS_OVER = 10004;
	
	/** 每 GAME_REFRESH_RATE ms刷新一次*/
	public static final int GAME_REFRESH_RATE = 15;
	
	/** 每 GAME_SENDINFO_RATE ms 向客户端发送服务器信息*/
	public static final int GAME_SENDINFO_RATE = 50;
	/**玩家改变球的状态*/
	public static final int GAME_CHANGEV = 10005;
}
