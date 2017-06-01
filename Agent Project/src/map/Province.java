package map;

import java.util.HashMap;

public interface Province{
	public HashMap<String, Integer> getNeighbors();
	public Integer getDistance(Province target);
	public String getProvinceName();
	public void setNeighbors();
	public Province yourPosition();
}
