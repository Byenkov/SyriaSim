package map;

import java.util.HashMap;

public class Al_Hasakah implements Province{
	private static Al_Hasakah instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Al_Hasakah";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Al_Hasakah() {}
	   public static Al_Hasakah getInstance() {
	      if(instance == null) {
	         instance = new Al_Hasakah();
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
			  neighbors.put(Deir_ez_Zor.getInstance(), 5);
			   neighbors.put(Ar_Raqqah.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}