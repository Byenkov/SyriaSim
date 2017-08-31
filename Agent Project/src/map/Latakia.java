package map;

import java.util.HashMap;

public class Latakia implements Province{
	public int population = 1008000;
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	private static Latakia instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Latakia";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Latakia() {setNeighbors();}
	   public static Latakia getInstance() {
	      if(instance == null) {
	         instance = new Latakia();
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
			neighbors.put("Tartus", 5);
			neighbors.put("Hama", 5);
			neighbors.put("Idlib", 5);
		}
		public Province yourPosition(){
			return this;
		}
	}