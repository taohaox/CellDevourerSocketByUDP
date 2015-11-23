package com.celldevourersocket.entry;

import java.util.Random;

public class Ball  extends Point {
	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	public int alpha = 255;
	public int red = 0;
	public int green = 0;
	public int blue = 0;
	public Random rand = new Random();
	public static final double SIZE =  314.15926;
	public static final double R = 10;
	public Ball(double x, double y,   int alpha, int red,
			int green, int blue) {
		super(x,y);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	public Ball(int x, int y) {
		this(x, y,  0, 0, 0, 0);
		setRandomColor();
	}
	public Ball(Point point) {
		this(point.getX(), point.getY(), 0, 0, 0, 0);
		setRandomColor();
	}
	public Ball() {
		super();
	}
	/**
	 * 设置随机颜色
	 */
	public void setRandomColor(){
		alpha = rand.nextInt(156)+100;
		red = rand.nextInt(256);
		green = rand.nextInt(256);
		blue = rand.nextInt(256);
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green;
	}
	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = blue;
	}
}
