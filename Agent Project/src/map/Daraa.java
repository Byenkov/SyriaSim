package map;

import java.util.HashMap;

public class Daraa implements Province{
	public int population = 1027000;
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	private static Daraa instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Daraa";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Daraa() {setNeighbors();}
	   public static Daraa getInstance() {
	      if(instance == null) {
	         instance = new Daraa();
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
			neighbors.put("Rif_Dimashq", 5);
			neighbors.put("Quneitra", 5);
			neighbors.put("As_Suwayda", 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}