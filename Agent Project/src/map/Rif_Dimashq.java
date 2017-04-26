package map;

import java.util.HashMap;

public class Rif_Dimashq implements Province{
	private static Rif_Dimashq instance = null;
	private final int unitLimit = 5;
	private int unitNumber = 0;
	private final String provinceName = "Rif_Dimashq";
	private HashMap<Province,Integer> neighbors = new HashMap<Province,Integer>();
	//private List<NazwaKlasyJednostek> unitsList = new ArrayList<Nazwa>();
	   protected Rif_Dimashq() {}
	   public static Rif_Dimashq getInstance() {
	      if(instance == null) {
	         instance = new Rif_Dimashq();
	      }
	      return instance;
	   }
	   public HashMap<Province,Integer> getNeighbors()
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
			   neighbors.put(Dimashq.getInstance(), 5);
			   neighbors.put(Quneitra.getInstance(), 5);
			   neighbors.put(Daraa.getInstance(), 5);
			   neighbors.put(As_Suwayda.getInstance(), 5);
			   neighbors.put(Homs.getInstance(), 5);
		}
		public Province yourPosition(){
			return this;
		}
	}