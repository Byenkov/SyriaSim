package map;

import java.util.HashMap;

public class Ar_Raqqah implements Province{
	private static Ar_Raqqah instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Ar_Raqqah";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Ar_Raqqah() {setNeighbors();}
	   public static Ar_Raqqah getInstance() {
	      if(instance == null) {
	         instance = new Ar_Raqqah();
	      }
	      return instance;
	   }
	   public HashMap<String,Integer> getNeighbors()
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
			neighbors.put("Deir_ez_Zor", 5);
			neighbors.put("Al_Hasakah", 5);
			neighbors.put("Aleppo", 5);
			neighbors.put("Hama", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}