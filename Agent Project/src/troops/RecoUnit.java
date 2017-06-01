package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class RecoUnit extends Agent {
	private AID myCommand;
	private List<AID> infiltratingAgents; //agents that are infiltrating this
	private List<AID> knownAgents; //known agents this agent sends infiltrators to
	private List<Allignment> enemies;
	private List<Allignment> neutrals; 
	private List<Allignment> allies;
	private List<KnownUnit> knownRecoUnits;
	private List<KnownUnit> knownMilUnits;
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
		
		location = ProvinceFactory.getProvince((String) args[0]);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd  = new ServiceDescription();
		sd.setType("RecoUnit@"+location.getProvinceName());
		sd.setName(getLocalName());
		sd.setOwnership(allignment.toString());
        dfd.addServices(sd);
        System.out.println(getAID());
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
        
        addBehaviour(new Activator());
		addBehaviour(new InfiltratorChecker());
        addBehaviour(new WakerBehaviour(this, 1000) {
        	public void onWake(){
        		myAgent.addBehaviour(new InitialAgentFinder());
        	}
		});
        
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
		        		System.out.println(getLocalName()+" knows: "+knownAgents);
		        		System.out.println(getLocalName()+" removing "+da.getAgent().getLocalName()+" from local knowledge");
		        		System.out.println(getLocalName()+" knows: "+knownAgents);
		        	}
	        	};
	        	handlers.put(IntrospectionVocabulary.DEADAGENT, terminationsHandler);
        	}
        };
        addBehaviour(myAMSSubscriber);
	}
	
	/*
	 * Initial Reco Behaviour
	 * 1. Requests orders from Main
	 * 2. Receives orders
	 * 3. Gets ordered AIDs for further investigation
	 */
	protected class InitialAgentFinder extends SequentialBehaviour{
		private List<AID> searchedMilAgents;
		private List<AID> searchedRecoAgents;
		
		public InitialAgentFinder(){
			searchedMilAgents = new ArrayList<AID>();
			searchedRecoAgents = new ArrayList<AID>();
			
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
							enemies = receivedOrders.getEnemies();
							neutrals = receivedOrders.getNeutrals();
							allies = receivedOrders.getAllies();
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
			addSubBehaviour(new OneShotBehaviour() {
				//get AIDs
				@Override
				public void action() {
					for (Allignment allignment : neutrals){
						searchedMilAgents.addAll(searchAID(myAgent, "MilUnit", allignment));
						searchedRecoAgents.addAll(searchAID(myAgent, "RecoUnit", allignment));
						System.out.println(myCommand.getLocalName());

					}
					for (Allignment allignment : enemies){
						searchedMilAgents.addAll(searchAID(myAgent, "MilUnit", allignment));
						searchedRecoAgents.addAll(searchAID(myAgent, "RecoUnit", allignment));
					}
					knownAgents.addAll(searchedRecoAgents);
					knownAgents.addAll(searchedMilAgents);
				}
			});
			addSubBehaviour(new Infiltrate());
			//TODO start searching new agents
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
	
	private class InfiltratingAgent extends TickerBehaviour{
		private AID aid;
		
		public InfiltratingAgent(Agent a, AID aid, long period) {
			super(a, period);
			this.aid = aid;
		}

		@Override
		protected void onTick() {
			//TODO check efficiency
			//TODO extract info
			//TODO send back info
			System.out.println(aid.getLocalName() + " infiltrating " + myAgent.getAID().getLocalName());
		}
		
	}
	
	
	public ArrayList<AID> searchAID(Agent agent, String type, Allignment ownership){
		ArrayList<AID> returned = new ArrayList<AID>();
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type+"@"+location.getProvinceName());
		sd.setOwnership(ownership.toString());
		template.addServices(sd);
		try {
			List<DFAgentDescription> dfList = (Arrays.asList(DFService.search(agent, template)));
			for (DFAgentDescription df : dfList){
				returned.add(df.getName());
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return returned;
	}
	
	private class Activator extends Behaviour{
		MessageTemplate mt;
		boolean done = false;
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("ActivateUnit");
			ACLMessage msg= receive(mt);
			if (msg != null){
				myCommand = msg.getSender();
				try {
					allignment = (Allignment) msg.getContentObject(); //TODO nie trzeba pobierac allignmenta
				} catch (UnreadableException e){e.printStackTrace();}
		        done = true;
			}
			else block();
		}
		@Override
		public boolean done() {
			return done;
		}	
	}
	
	protected void takeDown() {
		deregister();
	}
	
	public void deregister(){
		System.out.println("Deregistering "+getLocalName());
		try {
			DFService.deregister(this);
			}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}
