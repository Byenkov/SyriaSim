package map;

import java.util.HashMap;
import java.util.Set;

public class Homs implements Province{
	private static Homs instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Homs";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Homs() {setNeighbors();}
	   public static Homs getInstance() {
	      if(instance == null) {
	         instance = new Homs();
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
			neighbors.put("Tartus", 5);
			neighbors.put("Rif_Dimashq", 5);
			neighbors.put("Hama", 5);
			neighbors.put("Ar_Raqqah", 5);
			neighbors.put("Deir_ez_Zor", 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}