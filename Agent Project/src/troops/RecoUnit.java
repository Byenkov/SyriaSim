package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;
import map.Province;
import map.ProvinceFactory;
import provinces.LocalAgents;

public class RecoUnit extends Agent {
	private AID myCommand;
	private AID province;
	private List<AID> infiltratingAgents; //agents that are infiltrating this
	private List<AID> knownAgents; //known agents this agent sends infiltrators to
	private List<KnownUnit> allies;
	private List<KnownUnit> neutrals;
	private List<KnownUnit> enemies;
	private Orders orders;
	private Map<AID, KnownRecoUnit> knownRecoUnits;
	private Map<AID, DivisionInfo> knownMilUnits;
	private Province location;
	private Allignment allignment;
	
	protected void setup(){
		Object[] args = getArguments();
		String allignmentString = (String) args[1];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		knownAgents = new ArrayList<AID>();
		infiltratingAgents = new ArrayList<AID>();
		
		knownRecoUnits = new HashMap<AID, KnownRecoUnit>();
		knownMilUnits = new HashMap<AID, DivisionInfo>();
		
		location = ProvinceFactory.getProvince((String) args[0]);
		
		province = new AID( location.getProvinceName(), AID.ISLOCALNAME);
        
		addBehaviour(new Activator());
        addBehaviour(new Deactivator());
        addBehaviour(new OrderReceiver());
		addBehaviour(new InfiltratorChecker());
		addBehaviour(new InfiltrationInfoUpdater());
        addBehaviour(new InitialAgentFinder());
        addBehaviour(new OrderHandler());
        AMSSubscriber myAMSSubscriber = new AMSSubscriber() {
        	protected void installHandlers(Map handlers) {
	        	// Associate an handler to born-agent events
	        	EventHandler creationsHandler = new EventHandler() {
		        	public void handle(Event ev) {
		        		BornAgent ba = (BornAgent) ev;
		        	}
	        	};
	        	handlers.put(IntrospectionVocabulary.BORNAGENT, creationsHandler);
	        	// Associate an handler to dead-agent events
	        	EventHandler terminationsHandler = new EventHandler() {
		        	public void handle(Event ev) {
		        		DeadAgent da = (DeadAgent) ev;
		        		knownAgents.remove(da.getAgent());
		        		for (int i = 0; i < knownMilUnits.keySet().toArray().length; i++){
		        			knownMilUnits.remove(da.getAgent());
		        			knownAgents.remove(da.getAgent());
		        			knownRecoUnits.remove(da.getAgent());
		        		}
		        	}
	        	};
	        	handlers.put(IntrospectionVocabulary.DEADAGENT, terminationsHandler);
        	}
        };
        addBehaviour(myAMSSubscriber);
        addBehaviour(new TickerBehaviour(this, 3000) {
			
			@Override
			protected void onTick() {
				addBehaviour(new InitialAgentFinder());
			}
		});
        addBehaviour(new AgentKill());
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
	
	protected class OrderHandler extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Target"), MessageTemplate.MatchPerformative(ACLMessage.CFP));		
			ACLMessage msg= receive(mt);
			if (msg != null){
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.AGREE);
				try {
					reply.setContentObject(knownMilUnits.values().toArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(reply);
			}
			else block();
		}
		
	}
	
	protected class StatusReport extends OneShotBehaviour {

		@Override
		public void action() {
				ACLMessage report = new ACLMessage(ACLMessage.INFORM);
				report.setConversationId("RecoReport");
				try {
					report.setContentObject(knownMilUnits.values().toArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(report);
		}
	}
	
	protected class InitialAgentFinder extends SequentialBehaviour{
		
		public InitialAgentFinder(){
			
			//Request orders if none are available
			addSubBehaviour(new OneShotBehaviour() {
				@Override
				public void action() {
					//TODO check if orders are up to date
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setConversationId("Reco");
					msg.addReceiver(myCommand);
					send(msg);
				}
			});
			//Get info from orders
			addSubBehaviour(new Behaviour() {
				boolean finished = false;
				@Override
				public void action() {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Reco"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));		
					ACLMessage msg= receive(mt);
					if (msg != null){
						Orders receivedOrders;
						try {
							receivedOrders = (Orders) msg.getContentObject();
							orders = receivedOrders;
						} catch (UnreadableException e) {e.printStackTrace();}
						finished = true;
					}
					else block();
				}

				@Override
				public boolean done() {
					return finished;
				}
			});
			
			//Request AIDs from Province
			addSubBehaviour(new OneShotBehaviour() {
				@Override
				public void action() {
					//TODO check if orders are up to date
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setConversationId("Reco");
					msg.addReceiver(province);
					send(msg);
				}
			});
			//get local agents
			addSubBehaviour(new Behaviour() {
				boolean finished = false;
				@SuppressWarnings("unchecked")
				@Override
				public void action() {
					MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Reco"), MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF));		
					ACLMessage msg= receive(mt);
					if (msg != null){
						try {
							knownAgents = (ArrayList<AID>) msg.getContentObject();
						} catch (UnreadableException e) {e.printStackTrace();}
						finished = true;
					}
					else block();
				}

				@Override
				public boolean done() {
					return finished;
				}
			});
			
			addSubBehaviour(new Infiltrate());
		}
		
		//true - agent is not mine
		protected boolean checkID(AID aid){
			return !(aid.getLocalName().charAt(0) == allignment.toString().charAt(0));
		}
	}
	
	/*
	 * Starts infiltration
	 * infiltrates every known non-allied agent
	 */
	private class Infiltrate extends OneShotBehaviour {
		@Override
		public void action() {
			if (!knownAgents.isEmpty()){
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setConversationId("Infiltration");
				for (AID aid : knownAgents){
					msg.addReceiver(aid);
				}
				send(msg);
			}
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
				infiltratingAgents.add(msg.getSender());
				addBehaviour(new InfiltratingAgent(myAgent, msg.getSender(), 5000));
			}
			else block();
		}
		
	}
	
	private class InfiltrationInfoUpdater extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Infiltration"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					Object content = msg.getContentObject();
					if (content instanceof KnownRecoUnit){
						KnownRecoUnit addedReco = (KnownRecoUnit) content;
						knownRecoUnits.put(msg.getSender(), addedReco);
					}
					if (content instanceof DivisionInfo){
						DivisionInfo addedMil = (DivisionInfo) content;
						knownMilUnits.put(msg.getSender(), addedMil);
						addBehaviour(new StatusReport());
					}
				} catch (UnreadableException e) {e.printStackTrace();}
			}
			else block();
		}
		
	}
	
	private class InfiltratingAgent extends TickerBehaviour{
		private AID aid;
		
		public InfiltratingAgent(Agent a, AID aid, long period) {
			super(a, period);
			this.aid = aid;
		}

		@Override
		protected void onTick() {
			//TODO check efficiency
			float efficiency = 1f; //TODO add weather and skill conditions
			//TODO randomize  bit
			KnownRecoUnit thisReco = new KnownRecoUnit();
			thisReco.setAid(myAgent.getAID());
			thisReco.setAllies(allies);
			thisReco.setEnemies(enemies);
			thisReco.setNeutrals(neutrals);
			thisReco.setAllignment(allignment);
			thisReco.setOrders(orders);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("Infiltration");
			msg.addReceiver(aid);
			try {
				msg.setContentObject(thisReco);
			} catch (IOException e) {e.printStackTrace();}
			send(msg);
		}
		
	}
	
	private class Activator extends CyclicBehaviour{
		MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("ActivateUnit");
			ACLMessage msg= receive(mt);
			if (msg != null){
				knownMilUnits.clear();
				knownRecoUnits.clear();
				knownAgents.clear();
				String newLocation = msg.getContent();
				province = new AID( newLocation, AID.ISLOCALNAME);
				location = ProvinceFactory.getProvince(newLocation);
				ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
				msg1.setConversationId("ActivateUnit");
				msg1.addReceiver(province);
				msg1.setContent("Reco");
				send(msg1);
				myCommand = msg.getSender();
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
				msg1.addReceiver(province);
				msg1.setContent("Reco");
				send(msg1);
			}
			else block();
		}
	}
	
	private class AgentKill extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("KillMSG"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = receive(mt);
			if (msg != null){
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.addReceiver(province);
				message.setConversationId("KillMSG");
				send(message);
				doDelete();
			} else block();
			
		}
	}
	
	protected void takeDown() {
	}
}
