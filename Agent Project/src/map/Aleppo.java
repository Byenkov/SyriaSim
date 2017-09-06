package map;

import java.util.HashMap;

public class Aleppo implements Province{
	public final Importance importance = Importance.CRITICAL;
	private int population = 4868000;
	private static Aleppo instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Aleppo";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Aleppo() {setNeighbors();}
	   public static Aleppo getInstance() {
	      if(instance == null) {
	         instance = new Aleppo();
	      }
	      return instance;
	   }
	   public HashMap<String,Integer> getNeighbors()
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
			neighbors.put("Ar_Raqqah", 5);
			neighbors.put("Hama", 5);
			neighbors.put("Idlib", 5);	
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