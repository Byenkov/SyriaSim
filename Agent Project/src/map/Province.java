package map;

import java.util.HashMap;

import jade.core.AID;

public interface Province{
	public HashMap<String, Integer> getNeighbors();
	public Integer getDistance(Province target);
	public String getProvinceName();
	public void setNeighbors();
	public Province yourPosition();
	public int getPopulation();
	public void setPopulation(int population);
}
