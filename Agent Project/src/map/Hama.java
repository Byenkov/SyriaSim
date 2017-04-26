package map;

import java.util.HashMap;

public class Hama implements Province{
	private static Hama instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Hama";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Hama() {}
	   public static Hama getInstance() {
	      if(instance == null) {
	         instance = new Hama();
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
			neighbors.put(Latakia.getInstance(), 5);
			neighbors.put(Tartus.getInstance(), 5);
			neighbors.put(Homs.getInstance(), 5);
			neighbors.put(Ar_Raqqah.getInstance(), 5);
			neighbors.put(Aleppo.getInstance(), 5);
			neighbors.put(Idlib.getInstance(), 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}