package map;

import java.util.HashMap;

public class Idlib implements Province{
	public final Importance importance = Importance.IMPORTANT;
	private int population = 1501000;
	private static Idlib instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Idlib";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Idlib() {setNeighbors();}
	   public static Idlib getInstance() {
	      if(instance == null) {
	         instance = new Idlib();
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
			neighbors.put("Latakia", 5);
			neighbors.put("Hama", 5);
			neighbors.put("Aleppo", 5);
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