package map;

import java.util.HashMap;

public class Tartus implements Province{
	private static Tartus instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Tartus";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Tartus() {}
	   public static Tartus getInstance() {
	      if(instance == null) {
	         instance = new Tartus();
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
			   neighbors.put(Hama.getInstance(), 5);
			   neighbors.put(Latakia.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}