package map;

import java.util.HashMap;

public class Dimashq implements Province{
	private static Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Dimashq";
	private HashMap<String, Integer> neighbors = new HashMap<String, Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Dimashq() {setNeighbors();}
	   public static Dimashq getInstance() {
	      if(instance == null) {
	    	  instance = new Dimashq();
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
			 neighbors.put("Rif_Dimashq", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}