package map;

import java.util.HashMap;

public class Deir_ez_Zor implements Province{
	private static Deir_ez_Zor instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Deir_ez_Zor";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Deir_ez_Zor() {}
	   public static Deir_ez_Zor getInstance() {
	      if(instance == null) {
	         instance = new Deir_ez_Zor();
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
			neighbors.put(Homs.getInstance(), 5);
			neighbors.put(Ar_Raqqah.getInstance(), 5);
			neighbors.put(Al_Hasakah.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}