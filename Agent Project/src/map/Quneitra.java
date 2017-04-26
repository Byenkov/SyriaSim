package map;

import java.util.HashMap;

public class Quneitra implements Province{
	private static Quneitra instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Quneitra";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Quneitra() {}
	   public static Quneitra getInstance() {
	      if(instance == null) {
	         instance = new Quneitra();
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
			 neighbors.put(Daraa.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}