package gui;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.util.HashMap;

public class Country {
	private HashMap<String, Province> provinces = new HashMap<String, Province>();
	
	public Country(double pX, double pY){
		provinces.put("Hama", new Province("Hama" , "Maps/Hama.png", "ISIS", pX + 0.0, pY + 0.0));
		provinces.put("Homs", new Province("Homs", "Maps/Homs.png", "ISIS", pX - 11.0, pY + 25.0));
		provinces.put("Rif Dimashq", new Province("Rif_Dimashq", "Maps/Rif Dimashq.png", "USA", pX - 25.0, pY + 133.0));
		provinces.put("As-Suwayda", new Province("As_Suwayda", "Maps/As-Suwayda.png", "Assad", pX + 8.0, pY + 217.0));
		provinces.put("Deir ez-Zor", new Province("Deir_ez_Zor", "Maps/Deir ez-Zor.png", "ISIS", pX + 225.5, pY - 60.5));
		provinces.put("Al-Hasakah", new Province("Al_Hasakah", "Maps/Al-Hasakah.png", "ISIS", pX + 243.0, pY - 133.0));
		provinces.put("Ar-Raqqah", new Province("Ar_Raqqah", "Maps/Ar-Raqqah.png", "Assad", pX + 137.2, pY - 86.9));
		provinces.put("Aleppo", new Province("Aleppo", "Maps/Aleppo.png", "Assad", pX + 27.0, pY - 100.0));
		provinces.put("Idlib", new Province("Idlib", "Maps/Idlib.png", "Assad", pX - 3.0, pY - 50.0));
		provinces.put("Latakia", new Province("Latakia", "Maps/Latakia.png", "Assad", pX - 31.0, pY - 13.0));
		provinces.put("Tartus", new Province("Tartus", "Maps/Tartus.png", "Assad", pX - 23.0, pY + 44.0));
		provinces.put("Dimashq", new Province("Dimashq", "Maps/Dimashq.png", "Assad", pX + 2.0, pY + 188.5));
		provinces.put("Daraa", new Province("Daraa", "Maps/Daraa.png", "USA", pX - 27.5, pY + 212.0));
		provinces.put("Quneitra", new Province("Quneitra", "Maps/Quneitra.png", "USA", pX - 47.0, pY + 206.0));
	}
	
	public void setCountry(String newProvince, String newController){
		if (newProvince != null) this.changeController(newProvince, newController);
	}
	
	public void emptyAll(Graphics2D g2){
		for (Province province : provinces.values()){
			province.empty(g2);
		}
	}
	
	public void paintMap(Graphics2D g2){
		provinces.get("Hama").paintProvince(g2);
		provinces.get("Homs").paintProvince(g2);
		provinces.get("Rif Dimashq").paintProvince(g2);
		provinces.get("As-Suwayda").paintProvince(g2);
		provinces.get("Deir ez-Zor").paintProvince(g2);
		provinces.get("Al-Hasakah").paintProvince(g2);
		provinces.get("Ar-Raqqah").paintProvince(g2);
		provinces.get("Aleppo").paintProvince(g2);
		provinces.get("Idlib").paintProvince(g2);
		provinces.get("Latakia").paintProvince(g2);
		provinces.get("Tartus").paintProvince(g2);
		provinces.get("Dimashq").paintProvince(g2);
		provinces.get("Daraa").paintProvince(g2);
		provinces.get("Quneitra").paintProvince(g2);
	}
	
	public void changeController(String provinceName, String newController){
		Province changed = provinces.get(provinceName);
		changed.setController(newController);
		provinces.replace(provinceName, changed);
	}
	
	public Area getArea(String provinceName){
		return provinces.get(provinceName).getArea();
	}
	
	public HashMap<String, Province> getProvinces(){
		return provinces;
	}
}
