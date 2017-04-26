package map;

import java.util.HashMap;

public class As_Suwayda implements Province{
	private static As_Suwayda instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "As_Suwayda";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected As_Suwayda() {}
	   public static As_Suwayda getInstance() {
	      if(instance == null) {
	         instance = new As_Suwayda();
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