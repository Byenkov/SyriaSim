package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class Province{
	private String name;
	private String controller;
	private Area area;
	private double pX, pY;
	private String path;
	
	public Province(String name, String path, Double pX, Double pY){
		this.setName(name);
		this.area = new ImageOutline(path).getArea_FastHack();
		this.setpX(pX);
		this.setpY(pY);
		this.path = path;
	}
	
	public Province(String name, String path, String controller, Double pX, Double pY){
		this.setName(name);
		this.setController(controller);
		this.area = new ImageOutline(path).getArea_FastHack();
		this.setpX(pX);
		this.setpY(pY);
		this.path = path;
	}
	
	public void paintProvince(Graphics2D g2){
		AffineTransform at = new AffineTransform(1,0,0,1,0,0);
		g2.setTransform(at);
		at.translate(getpX(),getpY());
		area = new ImageOutline(path).getArea_FastHack().createTransformedArea(at);
		g2.setColor(getColor());
		g2.fill(area);
	}
	
	public void empty(Graphics2D g2){
		AffineTransform at = new AffineTransform(0,0,0,0,0,0);
		g2.setTransform(at);
		area = area.createTransformedArea(at);
	}
	
	public void moveProvince(Graphics2D g2){

			AffineTransform at = new AffineTransform(1,0,0,1,0,0);
			g2.setTransform(at);
			at.translate(getpX(), getpY());
			area = area.createTransformedArea(at);

	}
	
	public Color getColor(){
		if (controller == "USA") return Color.blue;
		if (controller == "Assad") return Color.red;
		if (controller == "ISIS") return Color.black;
		return Color.gray;
	}

	public String getController() {
		return controller;
	}

	public Province setController(String controller) {
		this.controller = controller;
		return this;

	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getpX() {
		return pX;
	}

	public void setpX(double pX) {
		this.pX = pX;
	}

	public double getpY() {
		return pY;
	}

	public void setpY(double pY) {
		this.pY = pY;
	}
}
