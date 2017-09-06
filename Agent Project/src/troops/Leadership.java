package troops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import map.*;
import map.Province;

public class Leadership extends Agent {
	private Allignment allignment;
	private double manpower;
	private double equipment;
	private Map<String, Situation> globalSituation;
	private ArrayList<Province> provinces;
	
	protected void setup(){
		Object[] args = getArguments();
		
		String allignmentString = (String) args[0];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		getMapStatus();
		
		manpower = Double.parseDouble((String) args[1]);
		equipment = Double.parseDouble((String) args[2]);
		globalSituation = new HashMap<String, Leadership.Situation>();
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd  = new ServiceDescription();
		sd.setType("Leadership");
		sd.setName(allignmentString);
        dfd.addServices(sd);
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
        
        addBehaviour(new StatusUpdateReceiver());
        addBehaviour(new CallForHelpReceiver());
	}
	
	protected class StatusUpdateReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			getMapStatus();
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("StatusReport"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt); 
			if (msg != null){
				try {
					StatusReport report = (StatusReport) msg.getContentObject();
					Situation localSituation = new Situation();
					localSituation.setAllies(report.getAllied());
					localSituation.setNeutrals(report.getNeutral());
					localSituation.setEnemies(report.getEnemy());
					localSituation.addTroops(msg.getSender());
					globalSituation.put(report.getLocation(), localSituation);
				} catch (UnreadableException e) { e.printStackTrace(); }
			}else block();
		}
		
	}
	
	protected class CallForHelpReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("CallForHelp"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg = receive(mt);
			if (msg != null) {
				double severity = Double.parseDouble(msg.getContent());
			} else block();
		}
		
	}
	
	protected void getMapStatus(){
		provinces = new ArrayList<Province>();
		provinces.add(Al_Hasakah.getInstance());
		provinces.add(Aleppo.getInstance());
		provinces.add(Ar_Raqqah.getInstance());
		provinces.add(As_Suwayda.getInstance());
		provinces.add(Daraa.getInstance());
		provinces.add(Deir_ez_Zor.getInstance());
		provinces.add(Dimashq.getInstance());
		provinces.add(Hama.getInstance());
		provinces.add(Homs.getInstance());
		provinces.add(Idlib.getInstance());
		provinces.add(Latakia.getInstance());
		provinces.add(Quneitra.getInstance());
		provinces.add(Rif_Dimashq.getInstance());
		provinces.add(Tartus.getInstance());
	}
	
	protected void takeDown() {
		try {
			DFService.deregister(this);
			}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	protected class Situation {
		private int allies;
		private int neutrals;
		private int enemies;
		private ArrayList<AID> troops;
		public Situation() {
			troops = new ArrayList<AID>();
		}
		
		public int getAllies() {
			return allies;
		}
		public void setAllies(int allies) {
			this.allies = allies;
		}
		public int getNeutrals() {
			return neutrals;
		}
		public void setNeutrals(int neutrals) {
			this.neutrals = neutrals;
		}
		public int getEnemies() {
			return enemies;
		}
		public void setEnemies(int enemies) {
			this.enemies = enemies;
		}
		public void addTroops(AID troop) {
			if (!troops.contains(troop)) troops.add(troop);
		}
		public void removeTroops(AID troop) {
			troops.remove(troop);
		}
		public void setTroops(ArrayList<AID> troops) {
			this.troops = troops;
		}
	}
}
