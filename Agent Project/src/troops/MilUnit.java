package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.tools.introspector.gui.MyDialog;
import map.Province;
import map.ProvinceFactory;

public class MilUnit extends Agent{
	private AID myCommand;
	private AID myProvince;
	private List<KnownUnit> knownUnits;
	private Province location;
	private DivisionInfo divisionInfo = new DivisionInfo();
	private Allignment allignment;
	private boolean busy;
	private boolean canAttack;
	private boolean canTerrorize;
	private List<AID> infiltratingAgents; //agents that are infiltrating this

	protected void setup(){
		Object[] args = getArguments();
		
		canAttack = true;
        busy = false;
        canTerrorize = true;
        divisionInfo.setAid(getAID());
        infiltratingAgents = new ArrayList<AID>();
		
		String allignmentString = (String) args[1];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		divisionInfo.setManpower(Double.parseDouble((String) args[2]));
		divisionInfo.setEquipment(Double.parseDouble((String) args[3]));
		divisionInfo.setExperience(Double.parseDouble((String) args[4]));
		divisionInfo.setAllignment(allignment);
		
		location = ProvinceFactory.getProvince((String) args[0]);
        
       	myProvince = new AID( location.getProvinceName(), AID.ISLOCALNAME);
        
        addBehaviour(new Activator());
        addBehaviour(new Deactivator());
        addBehaviour(new DefenseBehaviour());
        addBehaviour(new AttackOrderReceiver());
        addBehaviour(new InfiltratorChecker());
        addBehaviour(new BattleResultManager());
        addBehaviour(new AirStrikeReceiver());
        addBehaviour(new FortifyOrderReceiver());
        addBehaviour(new TerrorizeOrderReceiver());
        addBehaviour(new Reinforcement());
        addBehaviour(new WakerBehaviour(this, 3000){
	        @Override	
        	public void onWake() {
	            addBehaviour(new StatusUpdate());
	        }
        });
	}
	
	private class Activator extends CyclicBehaviour{
		MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("ActivateUnit");
			ACLMessage msg= receive(mt);
			if (msg != null){
				String newLocation = msg.getContent();
				myProvince = new AID( newLocation, AID.ISLOCALNAME);
				location = ProvinceFactory.getProvince(newLocation);
				ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
				msg1.setConversationId("ActivateUnit");
				msg1.addReceiver(myProvince);
				msg1.setContent("Mil");
				send(msg1);
				myCommand = msg.getSender();
				addBehaviour(new ProvinceStrengthUpdate());
				addBehaviour(new Cooldown(false));
			}
			else block();
		}
	}
	
	private class Deactivator extends CyclicBehaviour{
		MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("DeactivateUnit");
			ACLMessage msg= receive(mt);
			if (msg != null){
				ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
				msg1.setConversationId("DeactivateUnit");
				msg1.addReceiver(myProvince);
				msg1.setContent("Mil");
				send(msg1);
				addBehaviour(new Cooldown(true));
			}
			else block();
		}
	}
	
	
	private class Cooldown extends OneShotBehaviour {
		private String cooldown;
		public Cooldown(boolean cooldown) {
			this.cooldown = (cooldown) ? "On" : "Off";
		}
		
		@Override
		public void action() {
			ACLMessage info = new ACLMessage(ACLMessage.INFORM);
			info.setConversationId("Cooldown");
			info.setContent(cooldown);
			info.addReceiver(myCommand);
			send(info);
		}
		
	}
	
	private class StatusUpdate extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("Status");
			try {
				msg.setContentObject(divisionInfo);
			} catch (IOException e) { e.printStackTrace(); }
			msg.addReceiver(myCommand);
			send(msg);
		}
	}
	
	private class ProvinceStrengthUpdate extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg= new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("Strength");
			try {
				msg.setContentObject(divisionInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg.addReceiver(myProvince);
			send(msg);
		}
		
	}
	
	private class Reinforcement extends CyclicBehaviour {
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Reinforcements"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					Reinforcements reinforcements = (Reinforcements) msg.getContentObject();
					divisionInfo.setManpower(divisionInfo.getManpower() + reinforcements.getManpower());
					divisionInfo.setEquipment(divisionInfo.getEquipment() + reinforcements.getEquipment());
					addBehaviour(new ProvinceStrengthUpdate());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else block();
		}
	}
	
	private class AttackOrderReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("AttackOrder"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				Object received;
				try {
					received = msg.getContentObject();
					if (received instanceof DivisionInfo) {
						DivisionInfo targetInfo = (DivisionInfo) received;
						AID target = targetInfo.getAid();
						if (canAttack) myAgent.addBehaviour(new AttackBehaviour(myAgent, target, targetInfo.getPhases()));
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}	
			}
			else block();
		}		
	}
	
	private class FortifyOrderReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Fortify"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				if (!divisionInfo.isFortified()) {
					System.out.println(getLocalName() + "is fortifying");
					addBehaviour(new Fortify(myAgent));
					addBehaviour(new Cooldown(true));
				}
			}	
			else block();
		}		
	}
	
	private class AttackBehaviour extends WakerBehaviour{
		private AID targetID;
		private int phases;
		
		public AttackBehaviour(Agent a, AID targetID, int phases) {
			super(a, 1000);
			this.targetID = targetID;
			this.phases = phases;
		}

		@Override
		public void onWake() {
			if (canAttack){
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setConversationId("Battle");
				try {
					divisionInfo.setPhases(phases);
					msg.setContentObject(divisionInfo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				msg.addReceiver(targetID);
				send(msg);
				canAttack = false;
				busy = true;
				addBehaviour(new Cooldown(true));
				myAgent.addBehaviour(new WakerBehaviour(myAgent, 3000){
					//Regrouping
					@Override
					public void onWake(){
						canAttack = true;
						busy = false;
						addBehaviour(new Cooldown(false));
					}
				});
			}
		}
		
	}
	
	private class Regroup extends WakerBehaviour {
		private long time;

		public Regroup(Agent a, long time) {
			super(a, 1500);
			this.time = time;
		}
		
		@Override
		public void onWake(){
			canAttack = false;
			busy = true;
			addBehaviour(new Cooldown(true));
			myAgent.addBehaviour(new WakerBehaviour(myAgent, time){
				@Override
				public void onWake(){
					addBehaviour(new Cooldown(false));
					canAttack = true;
					busy = false;
				}
			});
		}
	}
	
	private class DefenseBehaviour extends CyclicBehaviour{
		MessageTemplate mt;
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Battle"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null ){
				try {
					if (!busy){
						System.out.println(getLocalName() + " has been attacked by " + msg.getSender().getLocalName());
						busy = true;
						DivisionInfo enemyArmy = (DivisionInfo) msg.getContentObject();
						
							//Compare own power to attacker's
							boolean chance = (0.85*enemyArmy.getStrength() < divisionInfo.getStrength());
							
							//Fight
							if (chance){
								//Battle
								//inform province agent about the battle
								ACLMessage battleMessage = new ACLMessage(ACLMessage.INFORM);;
								ArrayList<DivisionInfo> battling = new ArrayList<DivisionInfo>();			
								battling.add(divisionInfo);
								battling.add(enemyArmy);
								battleMessage.setConversationId("Battle");
								try {
									battleMessage.setContentObject(battling);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								battleMessage.addReceiver(myProvince);
								send(battleMessage);
							//Try to escape
							} else {
								//can't escape
								if (new Random().nextInt(10)+1 < 10){
									//Battle
									//inform province agent about the battle
									ACLMessage battleMessage = new ACLMessage(ACLMessage.INFORM);;
									ArrayList<DivisionInfo> battling = new ArrayList<DivisionInfo>();			
									battling.add(divisionInfo);
									battling.add(enemyArmy);
									battleMessage.setConversationId("Battle");
									try {
										battleMessage.setContentObject(battling);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									battleMessage.addReceiver(myProvince);
									send(battleMessage);
								}
								else if (!divisionInfo.isFortified()){
									//Escaped
									System.out.println(myAgent.getLocalName()+" escaped from " + msg.getSender().getLocalName());
									busy = true;
									canAttack = false;
									addBehaviour(new Cooldown(true));
									myAgent.addBehaviour(new WakerBehaviour(myAgent, 10000){
										//Regrouping
										@Override
										public void onWake(){
											canAttack = true;
											busy = false;
											addBehaviour(new Cooldown(false));
										}
									});
								}
							}
					}
				} catch (UnreadableException | NullPointerException e1) {e1.printStackTrace();}
			}
			else block();
		}
	}
	
	private class BattleResultManager extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("BattleResult"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				Object received;
				try {
					received = msg.getContentObject();
					if (received instanceof DivisionInfo) divisionInfo = (DivisionInfo) received;
					canAttack = false;
					addBehaviour(new StatusUpdate());
					addBehaviour(new ProvinceStrengthUpdate());
					if (divisionInfo.getManpower() <= 0) addBehaviour(new KillAgent());
					myAgent.addBehaviour(new Regroup(myAgent, 8000));
				} catch (UnreadableException e) {e.printStackTrace();}
				
			}
			else block();
		}
		
	}
	
	private class TerrorizeOrderReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Terrorize"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				addBehaviour(new Terrorize());
			} else block();
		}
		
	}
	
	private class Terrorize extends SequentialBehaviour{
	
		public Terrorize(){
			if (canTerrorize){
				//inform province about terrorizing
				addSubBehaviour(new OneShotBehaviour(){
	
					@Override
					public void action() {
						canAttack = true;
						canTerrorize = false;
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						msg.setConversationId("Terror");
						msg.addReceiver(myProvince);
						send(msg);
					}
					
				});
				
				addSubBehaviour(new WakerBehaviour(myAgent, 30000){
					//Add waker with end effects of terrorizing
					@Override
					public void onWake() {
						canAttack = true;
						busy = false;
						double gainedManpower = new Random().nextInt(20)+20;
						double gainedEquipment = new Random().nextInt(10)+10;
						double gainedExperience = new Random().nextInt(9)+1;
						location.setCurrentNews("\n---------TERROR---------\n"
								+ getLocalName() + " has finished terrorizing local\npopulation and gained:\n" + 
								"Manpower: " + gainedManpower
								+ "\nEquipment: " + gainedEquipment
								+ "\nExperience: " + gainedExperience
								+"\n------------------------\n");
						divisionInfo.setManpower(divisionInfo.getManpower() + gainedManpower);
						divisionInfo.setEquipment(divisionInfo.getEquipment() + gainedEquipment);
						divisionInfo.setExperience(divisionInfo.getExperience() + gainedExperience);
						canTerrorize = true;
						addBehaviour(new ProvinceStrengthUpdate());
						addBehaviour(new StatusUpdate());
					}
					
				});
			}
		}
	}
	
	private class AirStrikeReceiver extends CyclicBehaviour {
		MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("AirStrike"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				Double effectiveness;
				Double percentageLost;
				effectiveness = Double.valueOf(msg.getContent());
					
				percentageLost = (double) (new Random().nextInt(25)+15) / 100;
					
				Double manLosses = (percentageLost * divisionInfo.getManpower());
				Double equipmentLosses = (manLosses * (new Random().nextDouble()+1));
				
				divisionInfo.setManpower(divisionInfo.getManpower() - manLosses);
				divisionInfo.setEquipment(divisionInfo.getEquipment() - equipmentLosses);
				
				addBehaviour(new StatusUpdate());
					
				location.setCurrentNews("\n----------AIRSTRIKE----------\n"+
						myAgent.getLocalName()+" has been bombed and lost:"
						+"\nManpower: " + manLosses.shortValue()
						+"\nEquipment: " + equipmentLosses.shortValue()
						+"\n-----------------------------\n");
				addBehaviour(new ProvinceStrengthUpdate());
				if (divisionInfo.getManpower() <= 0) addBehaviour(new KillAgent());
			} else block();
		}
	}

	
	private class Fortify extends WakerBehaviour {

		public Fortify(Agent a) {
			super(a, 20000);
			divisionInfo.setFortified(true);
			canAttack = false;
			location.setCurrentNews(getLocalName() + " is foritying while waiting for reinforcements\n");
			addBehaviour(new StatusUpdate());
			addBehaviour(new Cooldown(true));
		}
		
		@Override
		public void onWake(){
			divisionInfo.setFortified(false);
			canAttack = true;
			addBehaviour(new StatusUpdate());
			addBehaviour(new Cooldown(false));
		}
	}
	
	/*
	 * Checks for incoming infiltrators
	 * Starts infiltration against this
	 */
	private class InfiltratorChecker extends CyclicBehaviour {
		MessageTemplate mt;	
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Infiltration"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				//TODO check efficiency
				float efficiency = 1f; //TODO add weather and skill conditions
				//TODO randomize  bit
				//TODO send back info
				ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
				msg1.setConversationId("Infiltration");
				msg1.addReceiver(msg.getSender());
				try {
					msg1.setContentObject(divisionInfo);
				} catch (IOException e) {e.printStackTrace();}
				send(msg1);
			}
			else block();
		}		
	}
	
	private class KillAgent extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(myCommand);
			msg.addReceiver(myProvince);
			msg.setConversationId("KillMSG");
			send(msg);
			doDelete();
		}
	}
	
	protected void takeDown() {
		deregister();
	}
	
	public void deregister(){
	}
}
