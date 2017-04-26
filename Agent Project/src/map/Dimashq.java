package map;

import java.util.HashMap;

public class Dimashq implements Province{
	private static Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Dimashq";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Dimashq() {}
	   public static Dimashq getInstance() {
	      if(instance == null) {
	    	  instance = new Dimashq();
	      }
	      return instance;
	   }
	   public HashMap<Province,Integer> getNeighbors()
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
			 neighbors.put(Rif_Dimashq.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}