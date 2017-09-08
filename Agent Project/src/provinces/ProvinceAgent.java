package provinces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.domain.introspection.AMSSubscriber.EventHandler;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.Map;
import map.*;
import troops.Allignment;
import troops.Battle;
import troops.DivisionInfo;

public class ProvinceAgent extends Agent {
	private Province properties;
	private ArrayList<AID> localAgents;
	private HashMap<AID, DivisionInfo> divisions;
	private Weather weather;
	private int weatherTime;
	private double usaStrength;
	private double assadStrength;
	private double isisStrength;
	
	protected void setup(){
		Object[] args = getArguments();
		localAgents = new ArrayList<AID>();
		divisions = new HashMap<AID, DivisionInfo>();
		
		usaStrength = 0;
		assadStrength = 0;
		isisStrength = 0;
		
		properties = ProvinceFactory.getProvince((String) args[0]);
        
        addBehaviour(new AddUnit());
        addBehaviour(new RemoveUnit());
        addBehaviour(new SendAIDs());
        addBehaviour(new StrengthReceiver());
        addBehaviour(new ProvinceWeather(this, 0));
        addBehaviour(new BattleSim());
        addBehaviour(new TerrorReceiver());
        addBehaviour(new AirStrikeReceiver());
        addBehaviour(new AgentKill());
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
		        		localAgents.remove(da.getAgent());
		        		for (int i = 0; i < localAgents.toArray().length; i++){
		        			localAgents.remove(da.getAgent());
		        			System.out.println("Removing" + da.getAgent().getLocalName());
		        		}
		        	}
	        	};
	        	handlers.put(IntrospectionVocabulary.DEADAGENT, terminationsHandler);
        	}

			@Override
			protected void installHandlers(java.util.Map arg0) {
			}
        };
        addBehaviour(myAMSSubscriber);
	}
	
	private class ProvinceWeather extends WakerBehaviour{
		public ProvinceWeather(Agent a, long period) {
			super(a, period);
		}

		@Override
		public void onWake() {
		    Weather values[] = Weather.values(); 
		    weather = values[(int) (Math.random() * values.length)];
			Random generator = new Random();
			weatherTime = generator.nextInt(20) * 1000 + 2000;
			properties.setWeather(weather);
			addBehaviour(new ProvinceWeather(myAgent, weatherTime));
			
		}
	}
	
	private class StrengthReceiver extends CyclicBehaviour {
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Strength"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					DivisionInfo newDiv = (DivisionInfo) msg.getContentObject();
					divisions.put(msg.getSender(), newDiv);
					countStrength();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else block();
		}
	}
	
	private class AddUnit extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("ActivateUnit"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				localAgents.add(msg.getSender());
			}
			else block();
		}	
	}
	
	private class RemoveUnit extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("DeactivateUnit"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				for (int i = 0; i < localAgents.size(); i++){
					if (localAgents.get(i).getLocalName().equals(msg.getSender().getLocalName())) localAgents.remove(localAgents.remove(i)); 
					divisions.remove(msg.getSender());
					countStrength();
				}
			}
			else block();
		}	
	}
	
	private class SendAIDs extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Reco"),MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg= receive(mt);
			if (msg != null){
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM_REF);
				try {
					reply.setContentObject(localAgents);
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(reply);
			}
			else block();
		}	
	}
	
	private class AirStrikeReceiver extends CyclicBehaviour {
		MessageTemplate mt;
		AID target;
		
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("AirStrike"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				try {
					target = (AID) msg.getContentObject();
					System.out.println(msg.getSender().getLocalName()+" is bombing "+ target.getLocalName() +" in "+properties.getProvinceName());
					
					Double effectiveness = 1.0;
					switch (weather) {
					case SUNNY: effectiveness = 1.0;
					break;
					case RAINY: effectiveness = 0.85;
					break;
					case SANDSTORM: effectiveness = 0.4;
					break;
					}
					
					ACLMessage msgTarget = new ACLMessage(ACLMessage.INFORM);
					msgTarget.setConversationId("AirStrike");
					msg.setContent(effectiveness.toString());		
					msg.addReceiver(target);
					send(msg);
					
				} catch (UnreadableException e) {}
			} else block();
		}
	}
	
	private class TerrorReceiver extends CyclicBehaviour {
		MessageTemplate mt;
		
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Terror"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				int civillianCasualties = new Random().nextInt(30);
				properties.setPopulation(properties.getPopulation() - civillianCasualties);
				properties.setCurrentNews(msg.getSender().getLocalName()+" is terrorizing local population in "+properties.getProvinceName() + "\nwhich results in " + civillianCasualties + " civillians dying or missing.");
			} else block();
		}
		
	}
	
	private class BattleSim extends CyclicBehaviour{
		MessageTemplate mt;
		
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Battle"), MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg= receive(mt);
			if (msg != null){
				ArrayList<DivisionInfo> divisionList = new ArrayList<DivisionInfo>();
				try {
					Object received = msg.getContentObject();
					if (received instanceof ArrayList) divisionList = (ArrayList<DivisionInfo>) received;
					
					//BATTLE
					if (!divisionList.isEmpty()) {
						DivisionInfo defender = divisionList.get(0);
						DivisionInfo attacker = divisionList.get(1);
						
						Battle battle = new Battle(properties.getProvinceName(), attacker, defender);
						battle.runBattle();
						
						System.out.println(battle.getBattleReport());
						
						attacker = battle.getAttackerResult();
						defender = battle.getDefenderResult();
						
						//send results to battling divisions
						ACLMessage battleMessageAttacker = new ACLMessage(ACLMessage.INFORM);;
						DivisionInfo resultAttacker = attacker;
						battleMessageAttacker.setConversationId("BattleResult");
						battleMessageAttacker.setContentObject(resultAttacker);
						battleMessageAttacker.addReceiver(resultAttacker.getAid());
						send(battleMessageAttacker);
						
						ACLMessage battleMessageDefender = new ACLMessage(ACLMessage.INFORM);;
						DivisionInfo resultDefender = defender;
						battleMessageDefender.setConversationId("BattleResult");
						battleMessageDefender.setContentObject(resultDefender);
						battleMessageDefender.addReceiver(resultDefender.getAid());
						send(battleMessageDefender);
						
						int civillianCasualties = new Random().nextInt(30);
						properties.setPopulation(properties.getPopulation() - civillianCasualties);
						System.out.println("Battle resulted in " + civillianCasualties + " civillians dead or missing");
						properties.setCurrentNews(battle.getBattleReport() + "\n" + "Battle resulted in " + civillianCasualties + " civillians dead or missing\n");
					} else System.out.println("XXX");
					
				} catch (UnreadableException | IOException e) {
					e.printStackTrace();
				}
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
				for (int i = 0; i < localAgents.size(); i++){
					if (localAgents.get(i).getLocalName().equals(msg.getSender().getLocalName())) {
						localAgents.remove(localAgents.remove(i)); 
					}
					divisions.remove(msg.getSender());
				}
				countStrength();
			} else block();
			
		}
	}
	
	private void countStrength() {
		double assadStrength = 0;
		double isisStrength = 0;
		double usaStrength = 0;
		for (DivisionInfo div : divisions.values()){
			switch (div.getAllignment()) {
			case USA:
				usaStrength += div.getStrength();
				break;
			case ASSAD:
				assadStrength += div.getStrength();
				break;
			case ISIS:
				isisStrength += div.getStrength();
				break;
			}
		}
		properties.setStrength("USA", usaStrength);
		properties.setStrength("ASSAD", assadStrength);
		properties.setStrength("ISIS", isisStrength);
	}
}
