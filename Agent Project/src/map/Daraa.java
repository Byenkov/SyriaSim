package map;

import java.util.HashMap;

public class Daraa implements Province{
	private static Daraa instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Daraa";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Daraa() {}
	   public static Daraa getInstance() {
	      if(instance == null) {
	         instance = new Daraa();
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
			neighbors.put(Quneitra.getInstance(), 5);
			neighbors.put(As_Suwayda.getInstance(), 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}