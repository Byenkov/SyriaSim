package map;

import java.util.HashMap;

public class As_Suwayda implements Province{
	public int population = 370000;
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	private static As_Suwayda instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "As_Suwayda";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected As_Suwayda() {setNeighbors();}
	   public static As_Suwayda getInstance() {
	      if(instance == null) {
	         instance = new As_Suwayda();
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
			neighbors.put("Daraa", 5);	
		}
		public Province yourPosition(){
			return this;
		}
	}