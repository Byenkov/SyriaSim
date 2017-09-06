package troops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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
	private HashMap<String, Situation> globalSituation;
	private HashMap<AID, Integer> agentCalls;
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
		agentCalls = new HashMap<AID, Integer>();
        
        addBehaviour(new StatusUpdateReceiver());
        addBehaviour(new CallForHelpReceiver());
	}
	
	protected class DecisionMaker extends OneShotBehaviour {

		@Override
		public void action() {
			ArrayList<String> critical = new ArrayList<String>();
			ArrayList<String> crucial = new ArrayList<String>();
			ArrayList<String> important = new ArrayList<String>();
			ArrayList<String> secondary = new ArrayList<String>();
			for (Province province : provinces) {
				if (province.getImportance() == Importance.CRITICAL) critical.add(province.getProvinceName());
				if (province.getImportance() == Importance.CRUCIAL) crucial.add(province.getProvinceName());
				if (province.getImportance() == Importance.IMPORTANT) important.add(province.getProvinceName());
				if (province.getImportance() == Importance.SECONDARY) secondary.add(province.getProvinceName());
			}
			
			
		}
		
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
					switch (report.getSituation()) {
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					}
					int severity;
//					addBehaviour(new ChangeDivisionLocation(msg.getSender(), "Aleppo"));
					if (globalSituation.containsKey(report.getLocation())) {
						severity = globalSituation.get(report.getLocation()).getSeverity();
						localSituation.setSeverity(severity);
					}
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
				try {
					CallForHelp cfh = (CallForHelp) msg.getContentObject();
					Situation current = globalSituation.get(cfh.getLocation());
					int old = current.getSeverity();
					current.addSeverity(cfh.getSeverity());
					globalSituation.put(cfh.getLocation(), current);
//					if (allignment == Allignment.ASSAD) System.out.println(cfh.getLocation() + ": " + old + " + " + cfh.getSeverity() + " = " + globalSituation.get(cfh.getLocation()).getSeverity());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else block();
		}
		
	}
	
	protected class ChangeDivisionLocation extends OneShotBehaviour {
		private AID division;
		private String targetProvince;
		
		public ChangeDivisionLocation(AID division, String targetProvince){
			this.division = division;
			this.targetProvince = targetProvince;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(division);
			msg.setContent(targetProvince);
			msg.setConversationId("ChangeLocation");
			send(msg);			
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
		Collections.sort(provinces, (a, b) -> a.getImportance().getValue() > b.getImportance().getValue() ? -1 : a.getImportance() == b.getImportance() ? 0 : 1);
	}
	
	protected void takeDown() {
	}
	
	protected class Situation {
		private int allies;
		private int neutrals;
		private int enemies;
		private int severity;
		private ArrayList<AID> troops;
		public Situation() {
			troops = new ArrayList<AID>();
			severity = 0;
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
		public int getSeverity() {
			return severity;
		}
		public void setSeverity(int severity) {
			this.severity = severity;
		}
		public void addSeverity(int added) {
			this.severity += added;
		}
		
	}
}
