package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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
	private double incomeManpower;
	private double incomeEquipment;
	private HashMap<String, Situation> globalSituation;
	private HashMap<AID, Integer> agentCalls;
	private ArrayList<Province> provinces;
	private ArrayList<AID> movedAgents;
	
	Orders currentOrders;
	
	int i = 0;
	
	
	protected void setup(){
		Object[] args = getArguments();
		
		String allignmentString = (String) args[0];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		currentOrders = new Orders();
		currentOrders.setDefault(allignment);
		
		getMapStatus();
		
		manpower = Double.parseDouble((String) args[1]);
		equipment = Double.parseDouble((String) args[2]);
		incomeManpower = manpower;
		incomeEquipment = equipment;
		globalSituation = new HashMap<String, Leadership.Situation>();
		agentCalls = new HashMap<AID, Integer>();
		movedAgents = new ArrayList<AID>();
		
        addBehaviour(new StatusUpdateReceiver());
        addBehaviour(new AgentKill());
        addBehaviour(new CallForHelpReceiver());
        addBehaviour(new Income(this, 10000));
        addBehaviour(new MovableAgents(this, 30000));
	}
	
	protected class Income extends TickerBehaviour {

		public Income(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			manpower += incomeManpower;
			equipment += incomeEquipment;
			addBehaviour(new DecisionMaker());
		}
		
	}
	
	protected class MovableAgents extends TickerBehaviour {
		public MovableAgents(Agent a, long period) {
			super(a, period);
		}
		@Override
		protected void onTick() {
			movedAgents.clear();
		}
		
	}
	
	protected class DecisionMaker extends OneShotBehaviour {

		@Override
		public void action() {
			if (!globalSituation.isEmpty()){
				HashMap<String, Situation> currentGlobalSituation = globalSituation;
				
				ArrayList<String> availableTroops = new ArrayList<String>();
				
				ArrayList<ProvinceSeverity> severity = new ArrayList<ProvinceSeverity>();
				
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
				for (String province : currentGlobalSituation.keySet()) {
					if (currentGlobalSituation.get(province).getSeverity() == 0/* && !currentGlobalSituation.get(province).getTroops().isEmpty()*/) availableTroops.add(province);
				}
				
				for (Province province : provinces) {
					Double severityPoints = (double) currentGlobalSituation.get(province.getProvinceName()).getSeverity();
					if (province.getImportance() == Importance.CRITICAL) severityPoints *= 2.5;
					if (province.getImportance() == Importance.CRUCIAL) severityPoints *= 2.0;
					if (province.getImportance() == Importance.IMPORTANT) severityPoints *= 1.5;
					if (province.getImportance() == Importance.SECONDARY) severityPoints *= 1.0;
					ProvinceSeverity provinceSeverity = new ProvinceSeverity();
					provinceSeverity.setSeverity(severityPoints);
					provinceSeverity.setProvinceName(province.getProvinceName());
					severity.add(provinceSeverity);
				}
				
				Collections.sort(severity, (a, b) -> a.getSeverity() > b.getSeverity() ? -1 : a.getSeverity() == b.getSeverity() ? 0 : 1);
				int i = 0;
				for (ProvinceSeverity province : severity){
					
					Double difference = (double) (currentGlobalSituation.get(province.getProvinceName()).getEnemies() - currentGlobalSituation.get(province.getProvinceName()).getEnemies());
					
					Situation situation = currentGlobalSituation.get(province.getProvinceName());
					
					if (!currentGlobalSituation.get(province.getProvinceName()).getTroops().isEmpty()){
						double allied = (double) (currentGlobalSituation.get(province.getProvinceName()).getAllies());
						double hostile = (double) (currentGlobalSituation.get(province.getProvinceName()).getEnemies());
						if (hostile > 0.8*allied && manpower > 0 && equipment > 0){
							double manpowerSent = Math.round(2*(hostile - allied)/3.5);
							double equipmentSent = Math.round((incomeEquipment/incomeManpower)*manpowerSent);
							
							if (manpowerSent > 0 && equipmentSent > 0) {
								addBehaviour(new SendReinforcements(currentGlobalSituation.get(province.getProvinceName()).getTroops().get(0), manpowerSent, equipmentSent));
								Situation newSituation = currentGlobalSituation.get(province.getProvinceName());
								newSituation.setSeverity(0);
								currentGlobalSituation.put(province.getProvinceName(), newSituation);
							} else {
								Orders newOrders = currentOrders;
								newOrders.setStance(Stance.DEFENSIVE);
								addBehaviour(new SendOrders(newOrders, currentGlobalSituation.get(province.getProvinceName()).getTroops().get(0)));
							}
						}	
					}
						
				}
				
				ArrayList<String> provincesInNeed = new ArrayList<String>();
				ArrayList<AID> freeAgents = new ArrayList<AID>();
				for (String provinceKey : currentGlobalSituation.keySet()){
					if (currentGlobalSituation.get(provinceKey).getSeverity() == 0){
						if (globalSituation.get(provinceKey).getTroops().size() > 1) freeAgents.addAll(1, globalSituation.get(provinceKey).getTroops());
						else if (ProvinceFactory.getProvince(provinceKey).getImportance() == Importance.SECONDARY) freeAgents.addAll(globalSituation.get(provinceKey).getTroops());
					}
				}
				for (String provinceKey : currentGlobalSituation.keySet()){
					if (currentGlobalSituation.get(provinceKey).getSeverity() > 0){
						if (!freeAgents.isEmpty()){
							for (int j = 0; j < freeAgents.size(); j++){
								if (movedAgents.contains(freeAgents.get(j)) || globalSituation.get(provinceKey).getTroops().contains(freeAgents.get(j))) continue;
								else {
									addBehaviour(new ChangeDivisionLocation(freeAgents.get(j), provinceKey));
									movedAgents.add(freeAgents.get(j));
									freeAgents.remove(j);
									break;
								}
							}
						}
					}
				}
				globalSituation = currentGlobalSituation;
			}
		}
		
		private class ProvinceSeverity {
			private String provinceName;
			private Double severity;
			public String getProvinceName() {
				return provinceName;
			}
			public void setProvinceName(String provinceName) {
				this.provinceName = provinceName;
			}
			public Double getSeverity() {
				return severity;
			}
			public void setSeverity(Double severity) {
				this.severity = severity;
			}
		}
	}
	
	protected class SendReinforcements extends OneShotBehaviour {
		private Double sentManpower;
		private Double sentEquipment;
		private AID target;
		private AID milTarget;
		
		public SendReinforcements(AID target, Double sentManpower, Double sendEquipment) {
			if (sentManpower <= manpower) this.sentManpower = sentManpower;
			else this.sentManpower = manpower;
			manpower -= sentManpower;
			if (sendEquipment <= equipment) this.sentEquipment = sentManpower;
			else this.sentEquipment = equipment;
			equipment -= sentEquipment;
			this.target = target;
			this.milTarget = new AID( target.getLocalName() + "MilUnit", AID.ISLOCALNAME);
		}
		
		@Override
		public void action() {
			System.out.println("Sending " + sentManpower + "/" + sentEquipment + " to " + target.getLocalName());
			ACLMessage reinforcements = new ACLMessage(ACLMessage.INFORM);
			Reinforcements reinforcementUnit = new Reinforcements();
			reinforcementUnit.setManpower(sentManpower);
			reinforcementUnit.setEquipment(sentEquipment);
			try {
				reinforcements.setContentObject(reinforcementUnit);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reinforcements.setConversationId("Reinforcements");
			reinforcements.addReceiver(milTarget);
			reinforcements.addReceiver(target);
			send(reinforcements);
		}
		
	}
	
	protected class SituationAssess extends TickerBehaviour {

		public SituationAssess(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			double myStrength = 0;
			double neutralStrength = 0;
			double enemyStrength = 0;
			for (Province province : provinces) {
				myStrength += globalSituation.get(province.toString()).getAllies();
				neutralStrength += globalSituation.get(province.toString()).getNeutrals();
				enemyStrength += globalSituation.get(province.toString()).getEnemies();
			}
			if (myStrength > (neutralStrength + enemyStrength)*1.1) {
				currentOrders.setEnemies(currentOrders.getNeutrals());
				currentOrders.setStance(Stance.MODERATE);
				addBehaviour(new SendOrders(currentOrders, null));
			} else if (myStrength < 0.4*enemyStrength) {
				currentOrders.setStance(Stance.DEFENSIVE);
				addBehaviour(new SendOrders(currentOrders, null));
			} else if (myStrength > enemyStrength) {
				currentOrders.setStance(Stance.OFFENSIVE);
				addBehaviour(new SendOrders(currentOrders, null));
			}
		}
		
	}
	
	protected class SendOrders extends OneShotBehaviour {
		Orders newOrders;
		AID receiver;
		
		public SendOrders(Orders newOrders, AID receiver) {
			this.newOrders = newOrders;
			this.receiver = receiver;
		}

		@Override
		public void action() {
			ACLMessage order = new ACLMessage(ACLMessage.INFORM); 
			order.setConversationId("Orders");
			try {
				order.setContentObject(newOrders);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<AID> receivers = new ArrayList<AID>();
			HashMap<String, Situation> currSit = globalSituation;
			for (Province province : provinces) 
				if (currSit.get(province.toString()) != null) {
					receivers.addAll(currSit.get(province.toString()).getTroops());
				}
			if (receiver == null) for (AID receiver : receivers) {
				order.addReceiver(receiver);
				order.addReceiver(new AID( receiver.getLocalName() + "RecoUnit", AID.ISLOCALNAME));
			}
			else {
				order.addReceiver(receiver);
				order.addReceiver(new AID( receiver.getLocalName() + "RecoUnit", AID.ISLOCALNAME));
			}
			send(order);
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
						localSituation.addSeverity(report.getSituation());
						break;
					case 2:
						localSituation.addSeverity(report.getSituation());
						break;
					case 3:
						localSituation.setSeverity(0);
						break;
					case 4:
						localSituation.setSeverity(0);
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
					if (!globalSituation.isEmpty()){
						CallForHelp cfh = (CallForHelp) msg.getContentObject();
						if (cfh != null){
							Situation current = globalSituation.get(cfh.getLocation());
							current.addSeverity(cfh.getSeverity());
							globalSituation.put(cfh.getLocation(), current);
						}
					}
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
			System.out.println("Moving " + division.getLocalName() + " to " + targetProvince);
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
	
	private class AgentKill extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("KillMSG"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				
				for (String province : globalSituation.keySet()){
					ArrayList<AID> current = globalSituation.get(province).getTroops();
					if (current.contains(msg.getSender())) {
						System.out.println("Removing " + msg.getSender());
						current.remove(msg.getSender());
						Situation currentSituation = globalSituation.get(province);
						currentSituation.setTroops(current);
						globalSituation.put(province, currentSituation);
					}
				}
			} else block();
			
		}
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
		public ArrayList<AID> getTroops() {
			return troops;
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
