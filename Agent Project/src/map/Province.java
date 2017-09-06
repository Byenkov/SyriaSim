package map;

import java.util.HashMap;

import jade.core.AID;
import troops.Allignment;

public interface Province{
	public HashMap<String, Integer> getNeighbors();
	public Integer getDistance(Province target);
	public String getProvinceName();
	public void setNeighbors();
	public Province yourPosition();
	public int getPopulation();
	public void setPopulation(int population);
	public Importance getImportance();
	public void setStrength(String allignment, double strength);
	public double getStrength(String allignment);
	public Allignment getController();
	public void setController(Allignment controller);
}
