package map;

import java.util.HashMap;

public class Tartus implements Province{
	private static Tartus instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Tartus";
	private HashMap<String, Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Tartus() {setNeighbors();}
	   public static Tartus getInstance() {
	      if(instance == null) {
	         instance = new Tartus();
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
			   neighbors.put("Homs", 5);
			   neighbors.put("Hama", 5);
			   neighbors.put("Latakia", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}