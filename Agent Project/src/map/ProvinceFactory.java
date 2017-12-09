package map;

public final class ProvinceFactory {
	public static Province getProvince(String provinceName){
		switch (provinceName){
			case "Al_Hasakah": return Al_Hasakah.getInstance();
			case "Aleppo": return Aleppo.getInstance();
			case "Ar_Raqqah": return Ar_Raqqah.getInstance();
			case "As_Suwayda": return As_Suwayda.getInstance();
			case "Daraa": return Daraa.getInstance();
			case "Deir_ez_Zor": return Deir_ez_Zor.getInstance();
			case "Dimashq": return Dimashq.getInstance();
			case "Hama": return Hama.getInstance();
			case "Homs": return Homs.getInstance();
			case "Latakia": return Latakia.getInstance();
			case "Idlib": return Idlib.getInstance();
			case "Quneitra": return Quneitra.getInstance();
			case "Rif_Dimashq": return Rif_Dimashq.getInstance();
			case "Tartus": return Tartus.getInstance();
			default: return null;
		}
	}
}
