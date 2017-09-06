package troops;

import java.util.ArrayList;
import java.util.List;

public class Orders implements java.io.Serializable{
	private Stance stance;
	private List<Allignment> allies = new ArrayList<Allignment>();
	private List<Allignment> neutrals = new ArrayList<Allignment>();
	private List<Allignment> enemies = new ArrayList<Allignment>();
	
	public void setDefault(Allignment allignment){
		switch (allignment){
			case ASSAD:
				setDefaultASSAD();
				break;
			case USA:
				setDefaultUSA();
				break;
			case ISIS:
				setDefaultISIS();
				break;
		}
	}
	
	public void setDefaultUSA(){
		stance = Stance.OFFENSIVE;
		addNeutral(Allignment.ASSAD);
		addEnemy(Allignment.ISIS);
	}
	
	public void setDefaultASSAD(){
		stance = Stance.OFFENSIVE;
		addNeutral(Allignment.USA);
		addEnemy(Allignment.ISIS);
	}
	
	public void setDefaultISIS(){
		stance = Stance.OFFENSIVE;
		addEnemy(Allignment.USA);
		addEnemy(Allignment.ASSAD);
	}
	
	public Stance getStance() {
		return stance;
	}
	public void setStance(Stance stance) {
		this.stance = stance;
	}
	
	public void addAlly(Allignment allignment){
		allies.add(allignment);
	}
	public void removeAlly(Allignment allignment){
		allies.remove(allignment);
	}
	
	public void addNeutral(Allignment allignment){
		neutrals.add(allignment);
	}
	public void removeNeutral(Allignment allignment){
		neutrals.remove(allignment);
	}
	
	public void addEnemy(Allignment allignment){
		enemies.add(allignment);
	}
	public void removeEnemy(Allignment allignment){
		enemies.remove(allignment);
	}
	
	public List<Allignment> getAllies(){
		return allies;
	}
	
	public List<Allignment> getNeutrals(){
		return neutrals;
	}
	
	public List<Allignment> getEnemies(){
		return enemies;
	}
}
