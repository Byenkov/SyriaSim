package map;

import java.util.HashMap;

import troops.Allignment;

public class Daraa implements Province{
	private Allignment controller; 
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	private final Importance importance = Importance.CRUCIAL;
	private int population = 1027000;
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
		public int getPopulation() {
			return population;
		}
		public void setPopulation(int population) {
			this.population = population;
		}
		public Importance getImportance() {
			return importance;
		}
		public double getStrength(String allignment) {
			switch (allignment){
			case "ASSAD":
				return assadStrength;
			case "USA":
				return usaStrength;
			case "ISIS":
				return isisStrength;
			}
			return 0.0;
		}
		public void setStrength(String allignment, double strength) {
			switch (allignment){
			case "ASSAD":
				assadStrength = strength;
				break;
			case "USA":
				usaStrength = strength;
				break;
			case "ISIS":
				isisStrength = strength;
				break;
			}
		}
		public Allignment getController() {
			return controller;
		}
		public void setController(Allignment controller) {
			this.controller = controller;
		}
	}