package map;

import java.util.HashMap;

public class Hama implements Province{
	private static Hama instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Hama";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Hama() {setNeighbors();}
	   public static Hama getInstance() {
	      if(instance == null) {
	         instance = new Hama();
	      }
	      return instance;
	   }
	   public HashMap<String, Integer> getNeighbors()
		{
			return neighbors;
		}
	   public Integer getDistance(Province target){
		   return neighbors.get(target);
	   }
		public String getProvinceName(){
			return this.provinceName;
		}
		public void setNeighbors(){
			neighbors.put("Latakia", 5);
			neighbors.put("Tartus", 5);
			neighbors.put("Homs", 5);
			neighbors.put("Ar_Raqqah", 5);
			neighbors.put("Aleppo", 5);
			neighbors.put("Idlib", 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}