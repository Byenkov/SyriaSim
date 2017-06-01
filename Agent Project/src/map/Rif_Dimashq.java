package map;

import java.util.HashMap;

public class Rif_Dimashq implements Province{
	private static Rif_Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Rif_Dimashq";
	private HashMap<String, Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Rif_Dimashq() {setNeighbors();}
	   public static Rif_Dimashq getInstance() {
	      if(instance == null) {
	         instance = new Rif_Dimashq();
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
			   neighbors.put("Dimashq", 5);
			   neighbors.put("Quneitra", 5);
			   neighbors.put("Daraa", 5);
			   neighbors.put("As_Suwayda", 5);
			   neighbors.put("Homs", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}