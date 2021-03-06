package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import map.Province;
import map.ProvinceFactory;
import troops.StatusReport;
import troops.RecoUnit.OrderReceiver;

public class MainUnit extends Agent {
	private Allignment allignment;
	private Orders orders;
	private Province location;
	private DivisionInfo divisionInfo;
	
	private ArrayList<DivisionInfo> others;
	private StatusReport report;
	
	private AID leadership;
	private AID recoUnit;
	private AID milUnit;
	private AID province;
	
	private boolean canAttack;
	private boolean airStrikeAvailable;
	
	protected void setup(){
		Object[] args = getArguments();
		
		if (((String) args[1]).equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (((String) args[1]).equals("USA")) this.allignment = Allignment.USA;
		if (((String) args[1]).equals("ISIS")) this.allignment = Allignment.ISIS;
		
		others = new ArrayList<DivisionInfo>();
		
		canAttack = true;
		
		location = map.ProvinceFactory.getProvince((String) args[0]);
		orders = new Orders();
		
		report = new StatusReport();
		report.setLocation(location.getProvinceName());
		
		orders.setDefault(allignment);
		
		airStrikeAvailable = true;
		
		setMyAgents();
		
		addBehaviour(new AgentActivator());
		addBehaviour(new RecoUpdater());
		addBehaviour(new RecoHandler());
        addBehaviour(new OrderReceiver());
        addBehaviour(new CooldownReceiver());
		addBehaviour(new ChangeLocation());
		addBehaviour(new LocalSituationReport());
		addBehaviour(new DivisionStatusUpdater());
		addBehaviour(new AgentKill());
		addBehaviour(new AirStrikeReSupply(this));
		addBehaviour(new CallForHelpReceiver());
		addBehaviour(new Reinforcement());
		addBehaviour(new WakerBehaviour(this, 3000) { 
			@Override
			public void onWake(){
				myAgent.addBehaviour(new OverManager());
			}});
		addBehaviour(new ReportStatus(this));
	}
	
	private class AirStrike extends OneShotBehaviour {
		private AID target;
		
		public AirStrike(AID target){
			this.target = target;
		}

		@Override
		public void action() {
			//Request Air Strike in Province
			if (airStrikeAvailable && allignment != Allignment.ISIS){
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				airStrikeAvailable = false;
				msg.setConversationId("AirStrike");
				msg.addReceiver(province);
				try { msg.setContentObject(target); } catch (IOException e) {e.printStackTrace();}
				send(msg);
			}
		}
		
	}
	
	private class OverManager extends OneShotBehaviour {

		@Override
		public void action() {
			if (orders.getStance() == Stance.DEFENSIVE) addBehaviour(new DefensiveManager());
			if (orders.getStance() == Stance.MODERATE) addBehaviour(new ModerateManager());
			if (orders.getStance() == Stance.OFFENSIVE) addBehaviour(new OffensiveManager());
		}	
	}
	
	private class DefensiveManager extends FSMBehaviour {
		public DefensiveManager(){
			
			this.registerFirstState(new CheckSituation(myAgent), "CheckSituation");	
			this.registerState(new CallHelp(2), "CallForHelp");		
			this.registerState(new Fortify(), "Fortify");		
			this.registerLastState(new OverManager(), "OverManager");
			
			this.registerTransition("CheckSituation", "CallForHelp", 1);
			this.registerTransition("CheckSituation", "Fortify", 2);
			this.registerTransition("CheckSituation", "Fortify", 3);		
			this.registerTransition("CheckSituation", "Fortify", 4);
			
			this.registerDefaultTransition("CallForHelp", "Fortify");
			
			this.registerDefaultTransition("Fortify", "OverManager");
		}
	}
	
	private class ModerateManager extends FSMBehaviour {
		public ModerateManager(){
			
			this.registerFirstState(new CheckSituation(myAgent), "CheckSituation");		
			this.registerState(new CallHelp(1), "CallForHelp1");
			this.registerState(new CallHelp(2), "CallForHelp2");		
			this.registerState(new Fortify(), "Fortify");	
			this.registerState(new RequestTargets(), "RequestTargets");		
			this.registerState(new Terrorize(), "Terrorize");		
			this.registerState(new RequestAttack(orders.getStance()), "RequestAttack");			
			this.registerLastState(new OverManager(), "OverManager");
			
			this.registerTransition("CheckSituation", "CallForHelp1", 1);		
			this.registerTransition("CheckSituation", "CallForHelp2", 2);
			
			if (allignment == Allignment.ISIS){
				this.registerDefaultTransition("CallForHelp2", "Terrorize");
				this.registerDefaultTransition("Terrorize", "OverManager");
			} else {
				this.registerDefaultTransition("CallForHelp2", "Fortify");	
			}
			this.registerDefaultTransition("CallForHelp1", "Fortify");
			
			this.registerDefaultTransition("Fortify", "OverManager");
			
			this.registerTransition("CheckSituation", "RequestTargets", 3);		
			this.registerTransition("CheckSituation", "RequestTargets", 4);
			
			this.registerDefaultTransition("RequestTargets", "RequestAttack");
			
			this.registerDefaultTransition("RequestAttack", "OverManager");
		}
	}
	
	private class OffensiveManager extends FSMBehaviour {
		public OffensiveManager(){
			
			this.registerFirstState(new CheckSituation(myAgent), "CheckSituation");			
			this.registerState(new CallHelp(2), "CallForHelp");			
			this.registerState(new RequestTargets(), "RequestTargets");			
			this.registerState(new RequestAttack(orders.getStance()), "RequestAttack");		
			this.registerState(new Terrorize(), "Terrorize");	
			this.registerLastState(new OverManager(), "OverManager");
			
			this.registerTransition("CheckSituation", "CallForHelp", 1);
			
			if (allignment == Allignment.ISIS){
				this.registerDefaultTransition("CallForHelp", "Terrorize");
				this.registerDefaultTransition("Terrorize", "OverManager");
			} else {			
				this.registerDefaultTransition("CallForHelp", "OverManager");
			}
			
			
			this.registerTransition("CheckSituation", "RequestTargets", 2);
			this.registerTransition("CheckSituation", "RequestTargets", 3);
			this.registerTransition("CheckSituation", "RequestTargets", 4);
			
			this.registerDefaultTransition("RequestTargets", "RequestAttack");
			
			this.registerDefaultTransition("RequestAttack", "OverManager");
		}
	}
	
	private class ReportStatus extends TickerBehaviour {

		public ReportStatus(Agent a) {
			super(a, 5000);
		}

		@Override
		protected void onTick() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("StatusReport");
			msg.addReceiver(leadership);
			try {
				msg.setContentObject(report);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(msg);
		}		
	}
	
	private class CallHelp extends OneShotBehaviour {
		private Integer severity;
		//1 - low
		//2 - high
		
		public CallHelp(int severity){
			this.severity = severity;
		}
		
		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			CallForHelp cfh = new CallForHelp();
			cfh.setLocation(location.getProvinceName());
			cfh.setSeverity(severity);
			try {
				msg.setContentObject(cfh);
			} catch (IOException e) {
				e.printStackTrace();
			}
			msg.setConversationId("CallForHelp");
			msg.addReceiver(leadership);
			if (severity != null) send(msg);
		}	
	}
	
	private class CheckSituation extends WakerBehaviour {
		private int situation;
		//1 - need help ASAP
		//2 - could use some help
		//3 - balanced
		//4 - optimistic

		private double enemyStrength;
		private double alliedStrength;
		private double neutralStrength;
		
		public CheckSituation(Agent a) {
			super(a, 1000);
			addBehaviour(new RequestTargets());
		}

		@Override
		public void onWake() {
			if (!others.isEmpty()){
			enemyStrength = 0;
			alliedStrength = 0;
			neutralStrength = 0;
			
			for (int i = 0; i < others.size(); i++){
				DivisionInfo current = others.get(i);
				if (allignment == current.getAllignment()) alliedStrength += current.getStrength();
				if (orders.getAllies().contains(current.getAllignment())) alliedStrength += current.getStrength();
				if (orders.getEnemies().contains(current.getAllignment())) enemyStrength += current.getStrength();
				if (orders.getNeutrals().contains(current.getAllignment())) neutralStrength += current.getStrength();
			}
			
			if (enemyStrength > 1.15*alliedStrength) {
				if (alliedStrength + neutralStrength > enemyStrength) situation = 3;
				else situation = 1;
			} else if (enemyStrength >= 0.85*alliedStrength && enemyStrength <= 1.15*alliedStrength){
				if (enemyStrength > alliedStrength) situation = 3;
				else situation = 2;
			} else situation = 4;
			
			report.setAllied((int) alliedStrength);
			report.setNeutral((int) neutralStrength);
			report.setEnemy((int) enemyStrength);
			report.setSituation(situation);

//			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//			msg.setConversationId("Status");
//			try {
//				System.out.println(getLocalName() + "sending");
//				msg.setContentObject(new Double(alliedStrength));
//				msg.setEncoding(allignment.toString());
//			} catch (IOException e) { e.printStackTrace(); }
//			msg.addReceiver(province);
//			send(msg);
//			System.out.println(getLocalName() + ": "+  situation + "\nAllied: " + alliedStrength + "\nNeutral: " + neutralStrength + "\nEnemy: " + enemyStrength);
			} else {
				situation = 4;
				enemyStrength = 0;
				if (divisionInfo != null) alliedStrength = divisionInfo.getStrength();
				neutralStrength = 0;
			}
		}
		
		@Override
		public int onEnd(){		
			
			return situation;
		}
		
	}
	
	private class Terrorize extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId("Terrorize");
			msg.addReceiver(milUnit);
			send(msg);
		}
		
	}
	
	private class RequestTargets extends OneShotBehaviour {

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			msg.setConversationId("Target");
			msg.addReceiver(recoUnit);
			send(msg);
		}
	}
	
	private class DivisionStatusUpdater extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Status"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				try {
					divisionInfo = (DivisionInfo) msg.getContentObject();
				} catch (UnreadableException e) { e.printStackTrace(); }
			} else block();
		}
		
	}
	
	private class CooldownReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Cooldown"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				String msgContent = msg.getContent();
				canAttack = (msgContent.equals("On")) ? false : true;
			} else block();
		}
		
	}
	
	private class RecoUpdater extends CyclicBehaviour {

		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("RecoReport"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				try {
					others = (ArrayList<DivisionInfo>) msg.getContentObject();
				} catch (UnreadableException e) { e.printStackTrace(); }
			} else block();
		}
		
	}
	
	private class ChangeLocation extends CyclicBehaviour {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ChangeLocation"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				String target = msg.getContent();
				location.setCurrentNews(getLocalName() + " is being relocated to " + province.getLocalName() + "\n");
				location = ProvinceFactory.getProvince(target);
				ACLMessage oldProvince = new ACLMessage(ACLMessage.INFORM);
				oldProvince.addReceiver(milUnit);
				oldProvince.addReceiver(recoUnit);
				oldProvince.addReceiver(province);
				oldProvince.setConversationId("DeactivateUnit");
				send(oldProvince);
				province = new AID( target, AID.ISLOCALNAME);
				ACLMessage newProvince = new ACLMessage(ACLMessage.INFORM);
				newProvince.setContent(target);
				newProvince.addReceiver(milUnit);
				newProvince.addReceiver(recoUnit);
				newProvince.addReceiver(province);
				newProvince.setConversationId("ActivateUnit");
				others.clear();
				send(newProvince);
				location.setCurrentNews(getLocalName() + " has just arrived\n");
			} else block();	
		}
	}
	
	private class LocalSituationReport extends CyclicBehaviour {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Target"), MessageTemplate.MatchPerformative(ACLMessage.AGREE));
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					Object[] targetsArray = (Object[]) msg.getContentObject();
					others = (ArrayList<DivisionInfo>) new ArrayList(Arrays.asList(targetsArray));
				} catch(UnreadableException e) {e.printStackTrace();}  
			} else block();	
		}
	}
	
	protected class CallForAssistance extends OneShotBehaviour {
		private DivisionInfo target;
		private int phases;
		private boolean neutrals; //true - call neutrals too
		
		public  CallForAssistance(DivisionInfo target, int phases, boolean neutrals) {
			this.target = target;
			this.phases = phases;
		}

		@Override
		public void action() {
			ArrayList<DivisionInfo> receivers = others;
			for (int i = 0; i < receivers.size(); i++){
				if (orders.getEnemies().contains(receivers.get(i).getAllignment())) receivers.remove(i);
			}
			for (int i = 0; i < receivers.size(); i++){
				if (!neutrals){
					if (orders.getNeutrals().contains(receivers.get(i).getAllignment())) receivers.remove(i);
				}
			}
			
			if (receivers.isEmpty()) {
				addBehaviour(new SendAttackOrder(target, 1));
			}
			
			ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
			target.setPhases(phases);
			order.setConversationId("CallForAssistance");
			for (DivisionInfo di : receivers) {
				addBehaviour(new AssisstanceReceiver(di.getAid()));
				order.addReceiver(di.getAid());				
			}
		    try {
				order.setContentObject(target);
			} catch (IOException e) {e.printStackTrace();}
		    send(order);			
		}	
	}
	
	protected class AssisstanceReceiver extends Behaviour {
		private boolean finished = false;
		private AID sender;
		
		public AssisstanceReceiver(AID sender) {
			this.sender = sender;
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(sender),MessageTemplate.or(
					(MessageTemplate.and(
							MessageTemplate.MatchConversationId("CallForAssistance"), 
							MessageTemplate.MatchPerformative(ACLMessage.AGREE))),
					(MessageTemplate.and(
							MessageTemplate.MatchConversationId("CallForAssistance"), 
							MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))));		
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					DivisionInfo receivedTarget = (DivisionInfo) msg.getContentObject();
					addBehaviour(new SendAttackOrder(receivedTarget, 1));
				} catch (UnreadableException e) {e.printStackTrace();}
				finished = true;
			}
			else block();
		}

		@Override
		public boolean done() {
			return finished;
		}
	}
	
	protected class CallForHelpReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt =MessageTemplate.and(
					MessageTemplate.MatchConversationId("CallForAssistance"), 
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg = receive(mt); 
			if (msg != null){
				ACLMessage reply = msg.createReply();
				
				DivisionInfo target;
				try {
					target = (DivisionInfo) msg.getContentObject();
					double targetStr = target.getStrength();
					double myStrength = divisionInfo.getStrength();
					int situation;
					double enemyStrength = 0;
					double alliedStrength = 0;
					double neutralStrength = 0;
					
					for (int i = 0; i < others.size(); i++){
						DivisionInfo current = others.get(i);
						if (allignment == current.getAllignment()) alliedStrength += current.getStrength();
						if (orders.getAllies().contains(current.getAllignment())) alliedStrength += current.getStrength();
						if (orders.getEnemies().contains(current.getAllignment())) enemyStrength += current.getStrength();
						if (orders.getNeutrals().contains(current.getAllignment())) neutralStrength += current.getStrength();
					}
					
					enemyStrength -= targetStr;
				

					if (orders.getStance() == Stance.DEFENSIVE) {
						addBehaviour(new SendAttackOrder(target, 2));
						reply.setPerformative(ACLMessage.AGREE);
						reply.setContentObject(target);
					}
					else if (neutralStrength > 1.4*alliedStrength){
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContentObject(target);					
					} 
					else if (neutralStrength > alliedStrength && neutralStrength <= 1.4 * alliedStrength){
						if (targetStr > 1.2*myStrength) {
							reply.setPerformative(ACLMessage.REFUSE);
							reply.setContentObject(target);
						}
						else if (targetStr > 0.8*myStrength && targetStr <= 1.2*myStrength) {
							addBehaviour(new SendAttackOrder(target, 1));
							reply.setPerformative(ACLMessage.AGREE);
							reply.setContentObject(target);
						}
						else {
							addBehaviour(new SendAttackOrder(target, 1));
							reply.setPerformative(ACLMessage.AGREE);
							reply.setContentObject(target);
						}
					}
					else {
						if (targetStr > 1.2*myStrength) {
							reply.setPerformative(ACLMessage.REFUSE);
							reply.setContentObject(target);
						}
						else if (targetStr > 0.8*myStrength && targetStr <= 1.2*myStrength) {
							addBehaviour(new SendAttackOrder(target, 2));
							reply.setPerformative(ACLMessage.AGREE);
							reply.setContentObject(target);
						}
						else {
							addBehaviour(new SendAttackOrder(target, 2));
							reply.setPerformative(ACLMessage.AGREE);
							reply.setContentObject(target);
						}
					}
				} catch (IOException | UnreadableException e) {e.printStackTrace();}		
				send(reply);
			} else block();		
		}		
	}
	
	private class RequestAttack extends OneShotBehaviour {
		Stance stance;
		DivisionInfo target;
		
		RequestAttack(Stance stance){
			this.stance = stance;
		}
		@Override
		public void action() {
			ArrayList<DivisionInfo> targets = others;
			ArrayList<DivisionInfo> possibleTargets = new ArrayList<DivisionInfo>();
			for (int i = 0; i < targets.size(); i++){
				if (orders.getEnemies().contains(targets.get(i).getAllignment())){
					possibleTargets.add(targets.get(i));
				}
			}
			
			if (!possibleTargets.isEmpty()){
					
				 sortDivisions(possibleTargets);
					 
				 for (int i = 0; i < possibleTargets.size(); i++){
					 DivisionInfo currentTarget = possibleTargets.get(i);
					 if (stance == Stance.OFFENSIVE){
						 if (currentTarget.getStrength() > 1.2*divisionInfo.getStrength()) {
							 addBehaviour(new AirStrike(currentTarget.getAid()));
							 addBehaviour(new CallForAssistance(currentTarget, 1, true));
							 break;
						 }
						 else if (currentTarget.getStrength() <= 1.2*divisionInfo.getStrength() && currentTarget.getStrength() > 0.9*divisionInfo.getStrength()){
							 addBehaviour(new AirStrike(currentTarget.getAid()));
							 addBehaviour(new CallForAssistance(currentTarget, 2, false));
							 break;
						 }
						 else{
							 addBehaviour(new SendAttackOrder(currentTarget, 3));
						 }
					 }
					
					 if (stance == Stance.MODERATE){
						 if (currentTarget.getStrength() > 1.5*divisionInfo.getStrength()) continue;
						 if (currentTarget.getStrength() > 1.2*divisionInfo.getStrength()) {
							 addBehaviour(new AirStrike(currentTarget.getAid()));
							 addBehaviour(new CallForAssistance(currentTarget, 1, true));
							 break;
						 }
						 else if (currentTarget.getStrength() <= 1.2*divisionInfo.getStrength() && currentTarget.getStrength() > 0.9*divisionInfo.getStrength()){
							 addBehaviour(new AirStrike(currentTarget.getAid()));
							 addBehaviour(new CallForAssistance(currentTarget, 1, false));
							 break;
						 }
						 else{
							 addBehaviour(new SendAttackOrder(currentTarget, 2));
						 }
					 }
				 }
			 }	
		}		
		
		private void sortDivisions(ArrayList<DivisionInfo> divisions) {
			Collections.sort(divisions, (a, b) -> a.getStrength() > b.getStrength() ? -1 : a.getStrength() == b.getStrength() ? 0 : 1);
		}
		
		private DivisionInfo findTarget(ArrayList<DivisionInfo> arrDiv){
			DivisionInfo target = arrDiv.get(0);
			for (int i = 0; i < arrDiv.size(); i++){
				if (arrDiv.get(i).getStrength() > target.getStrength()) target = arrDiv.get(i);
			}
			return target;
		}
		
		private boolean checkAirStrike(DivisionInfo target, double partMin, double partMax){
			return (partMin*target.getStrength() < divisionInfo.getStrength() && target.getStrength() > partMax*divisionInfo.getStrength()) ? true : false;
		}
		
	}
	
	private class SendAttackOrder extends OneShotBehaviour {
		private DivisionInfo target;
		private int phases;
		
		public SendAttackOrder(DivisionInfo target, int phases) {
			this.target = target;
			this.phases = phases;
		}

		@Override
		public void action() {
			if (target != null) {
				ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
				target.setPhases(phases);
				order.setConversationId("AttackOrder");
				order.addReceiver(milUnit);
			    try {
					order.setContentObject(target);
				} catch (IOException e) {e.printStackTrace();}
			    send(order);
			}
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
					location.setCurrentNews(getLocalName() + " has received reinforcements: "
							+ "\nManpower: " + reinforcements.getManpower()
							+ "\nEquipment: " + reinforcements.getEquipment() + "\n");
					divisionInfo.setManpower(divisionInfo.getManpower() + reinforcements.getManpower());
					divisionInfo.setEquipment(divisionInfo.getEquipment() + reinforcements.getEquipment());
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else block();
		}
	}
	
	private class Fortify extends OneShotBehaviour {

		@Override
		public void action() {
			if (getLocalName().equals("us1tar")) System.out.println("Fortify");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST );
			msg.setConversationId("Fortify");
			msg.addReceiver(milUnit);
			send(msg);
		}
		
	}
	
	private class RecoHandler extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Reco"), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				ACLMessage reply = msg.createReply();
                reply.setPerformative( ACLMessage.INFORM );
                try {
					reply.setContentObject(orders);
				} catch (IOException e) {e.printStackTrace();}
                send(reply);
			}
			else block();
		}
	}
	
	protected void setMyAgents(){
		leadership = new AID( allignment.toString(), AID.ISLOCALNAME);
		recoUnit = new AID( getLocalName() + "RecoUnit", AID.ISLOCALNAME);
		milUnit = new AID( getLocalName() + "MilUnit", AID.ISLOCALNAME);
		province = new AID( location.getProvinceName(), AID.ISLOCALNAME);
	}
	
	private class AirStrikeReSupply extends TickerBehaviour {
		public AirStrikeReSupply(Agent a) {
			super(a, 30000);
		}
		@Override
		protected void onTick() {
			airStrikeAvailable = true;			
		}	
	}
	
	protected class OrderReceiver extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Orders"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				try {
					orders = (Orders) msg.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			} else block();
			
		}
		
	}
	
	protected class AgentActivator extends OneShotBehaviour{

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("ActivateUnit");
			msg.addReceiver(milUnit);
			msg.addReceiver(recoUnit);
			msg.addReceiver(province);
			msg.setContent(location.getProvinceName());
			send(msg);
		}
	}
	
	private class AgentKill extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("KillMSG"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				ArrayList<DivisionInfo> allies = new ArrayList<DivisionInfo>();
				for (int i = 0; i < others.size(); i++){
					if (others.get(i).getAllignment() != allignment) others.remove(i);
				}
				ACLMessage inf = new ACLMessage(ACLMessage.INFORM);
				inf.setConversationId("KillMSG");
				inf.addReceiver(province);
				inf.addReceiver(recoUnit);
				inf.addReceiver(leadership);
				send(inf);
				doDelete();
			} else block();
			
		}
	}
	
	protected void takeDown() {
	}

}