package map;

import java.util.HashMap;

import provinces.Weather;
import troops.Allignment;

public class Dimashq implements Province{
	private Allignment controller; 
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	private final Importance importance = Importance.CRITICAL;
	private int population = 2836000;
	private static Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Dimashq";
	private Weather weather;
	private String currentNews;
	private HashMap<String, Integer> neighbors = new HashMap<String, Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Dimashq() {setNeighbors();}
	   public static Dimashq getInstance() {
	      if(instance == null) {
	    	  instance = new Dimashq();
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
		@Override
		public String getCurrentNews() {
			return currentNews;
		}
		@Override
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