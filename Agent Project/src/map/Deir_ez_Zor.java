package map;

import java.util.HashMap;

public class Deir_ez_Zor implements Province{
	public final Importance importance = Importance.SECONDARY;
	private int population = 1239000;
	private static Deir_ez_Zor instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Deir_ez_Zor";
	private HashMap<String, Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Deir_ez_Zor() {setNeighbors();}
	   public static Deir_ez_Zor getInstance() {
	      if(instance == null) {
	         instance = new Deir_ez_Zor();
	      }
	      return instance;
	   }
	   public HashMap<String, Integer> getNeighbors()
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
			neighbors.put("Homs", 5);
			neighbors.put("Ar_Raqqah", 5);
			neighbors.put("Al_Hasakah", 5);
		}
		public Province yourPosition(){
			return this;
		}
		public int getPopulation() {
			return population;
		}
		public void setPopulation(int population) {
			this.population = population;
		}
	}