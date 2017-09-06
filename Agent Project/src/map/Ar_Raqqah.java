package map;

import java.util.HashMap;

import troops.Allignment;

public class Ar_Raqqah implements Province{
	private Allignment controller; 
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	private final Importance importance = Importance.CRUCIAL;
	private int population = 944000;
	private static Ar_Raqqah instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Ar_Raqqah";
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Ar_Raqqah() {setNeighbors();}
	   public static Ar_Raqqah getInstance() {
	      if(instance == null) {
	         instance = new Ar_Raqqah();
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
			neighbors.put("Homs", 5);
			neighbors.put("Deir_ez_Zor", 5);
			neighbors.put("Al_Hasakah", 5);
			neighbors.put("Aleppo", 5);
			neighbors.put("Hama", 5);
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