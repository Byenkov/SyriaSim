package map;

import java.util.HashMap;

import provinces.Weather;
import troops.Allignment;

public class Hama implements Province{
	private Allignment controller; 
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	private final Importance importance = Importance.IMPORTANT;
	private int population = 1628000;
	private static Hama instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Hama";
	private String currentNews;
	private Weather weather;
	private HashMap<String,Integer> neighbors = new HashMap<String,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Hama() {setNeighbors();}
	   public static Hama getInstance() {
	      if(instance == null) {
	         instance = new Hama();
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
			neighbors.put("Tartus", 5);
			neighbors.put("Homs", 5);
			neighbors.put("Ar_Raqqah", 5);
			neighbors.put("Aleppo", 5);
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