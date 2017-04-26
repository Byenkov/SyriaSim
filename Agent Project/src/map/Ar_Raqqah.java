package map;

import java.util.HashMap;

public class Ar_Raqqah implements Province{
	private static Ar_Raqqah instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Ar_Raqqah";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Ar_Raqqah() {}
	   public static Ar_Raqqah getInstance() {
	      if(instance == null) {
	         instance = new Ar_Raqqah();
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
			neighbors.put(Deir_ez_Zor.getInstance(), 5);
			neighbors.put(Al_Hasakah.getInstance(), 5);
			neighbors.put(Aleppo.getInstance(), 5);
			neighbors.put(Hama.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}