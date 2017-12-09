package provinces;

import java.io.Serializable;
import java.util.ArrayList;

import jade.core.AID;

public class LocalAgents implements Serializable {
	private ArrayList<AID> milUnits;
	private ArrayList<AID> recoUnits;
	public ArrayList<AID> getMilUnits() {
		return milUnits;
	}
	public void setMilUnits(ArrayList<AID> milUnits) {
		this.milUnits = milUnits;
	}
	public ArrayList<AID> getRecoUnits() {
		return recoUnits;
	}
	public void setRecoUnits(ArrayList<AID> recoUnits) {
		this.recoUnits = recoUnits;
	}

}
