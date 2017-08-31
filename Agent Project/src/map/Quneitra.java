package map;

import java.util.HashMap;

public class Quneitra implements Province{
	public int population = 90000;
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	private static Quneitra instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Quneitra";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Quneitra() {setNeighbors();}
	   public static Quneitra getInstance() {
	      if(instance == null) {
	         instance = new Quneitra();
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
			 neighbors.put("Rif_Dimashq", 5);
			 neighbors.put("Daraa", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}