package map;

import java.util.HashMap;

import provinces.Weather;
import troops.Allignment;

public class Rif_Dimashq implements Province{
	private Allignment controller; 
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	private final Importance importance = Importance.CRITICAL;
	private int population = 1711000;
	private static Rif_Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Rif_Dimashq";
	private String currentNews;
	private Weather weather;
	private HashMap<String, Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Rif_Dimashq() {setNeighbors();}
	   public static Rif_Dimashq getInstance() {
	      if(instance == null) {
	         instance = new Rif_Dimashq();
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
			   neighbors.put("Dimashq", 5);
			   neighbors.put("Quneitra", 5);
			   neighbors.put("Daraa", 5);
			   neighbors.put("As_Suwayda", 5);
			   neighbors.put("Homs", 5);
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
			if (assadStrength > usaStrength && assadStrength > isisStrength) controller = Allignment.ASSAD;
			if (usaStrength > assadStrength && usaStrength > isisStrength) controller = Allignment.USA;
			if (isisStrength > usaStrength && isisStrength > assadStrength) controller = Allignment.ISIS;
		}
		public Allignment getController() {
			return controller;
		}
		public void setController(Allignment controller) {
			this.controller = controller;
		}
		public String getCurrentNews() {
			return currentNews;
		}
		public void setCurrentNews(String currentNews) {
			this.currentNews = currentNews;
		}
		public Weather getWeather() {
			return weather;
		}
		public void setWeather(Weather weather) {
			this.weather = weather;
		}
	}