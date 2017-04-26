package map;

import java.util.HashMap;

public class Latakia implements Province{
	private static Latakia instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Latakia";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Latakia() {}
	   public static Latakia getInstance() {
	      if(instance == null) {
	         instance = new Latakia();
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
			neighbors.put(Tartus.getInstance(), 5);
			neighbors.put(Hama.getInstance(), 5);
			neighbors.put(Idlib.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}