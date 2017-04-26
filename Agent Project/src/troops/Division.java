package troops;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import map.Dimashq;
import map.Province;
import map.ProvinceFactory;

public class Division extends Agent{
	private DivisionInfo divisionInfo;
	private Orders orders;
	private HashMap<AID, DivisionInfo> knownDivisions;
	private Flag flag;
	private map.Province location;
	/////////////////////////// ZROBIÆ FLAGI DO WALKI - ZAJETY/WOLNY/ITD I REQUESTY PRZY WALCE
//	private double aggrRecoSkill; //<0.1 - 1> for aggressive reconaissance
//	private double antiRecoSkill; //<0.1 - 1> for anti-reconaissance
	
	protected void setup(){
		Object[] args = getArguments();
		divisionInfo = new DivisionInfo(args[0].toString(), args[1].toString(), args[2].toString(), args[3].toString());
		location = map.ProvinceFactory.getProvince(args[4].toString());
		orders = new Orders();
		orders.setDefault(divisionInfo.getAllignment());
		flag = Flag.DEFAULT;
//		divisionInfo = new DivisionInfo();
		knownDivisions = new HashMap<AID, DivisionInfo>();
		
		registerAgent();
        
        addBehaviour(new Receiver());
        addBehaviour(new DivisionSearcher());
        addBehaviour(new Reconaissance(this, divisionInfo.getSpeed()*5));
        addBehaviour(new AttackSchedule(this, divisionInfo.getSpeed()*15));
        addBehaviour(new LocationChangerSchedule(this, divisionInfo.getSpeed()*30));
	}
	
	private class LocationChangerSchedule extends TickerBehaviour{

		public LocationChangerSchedule(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			if (flag == Flag.DEFAULT){
				addBehaviour(new LocationChanger(myAgent, divisionInfo.getSpeed()*5));
			}
		}
		
	}
	
	private class LocationChanger extends WakerBehaviour{
		
		public LocationChanger(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onWake() {
			myAgent.addBehaviour(new DivisionSearcher());
			if (knownDivisions.isEmpty()){
				flag = Flag.RETREATING;
				System.out.println("Moving "+getLocalName());
				deregister();
//				for (Province neighbor : location.getNeighbors().keySet()){
//					System.out.println(neighbor.getProvinceName());
					Division.this.location = ProvinceFactory.getProvince("Dimashq");
//					break;
//				}
				flag = Flag.DEFAULT;
				registerAgent();
				System.out.print(" to "+location.getProvinceName());
			}
		}
		
	}
	
	protected class AttackSchedule extends TickerBehaviour{

		public AttackSchedule(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			myAgent.addBehaviour(new Target(myAgent, divisionInfo.getSpeed()*7));
		}
		
	}
	
	protected class WakeSender extends WakerBehaviour{
		private ACLMessage msg;
		public WakeSender(ACLMessage msg, Agent a, long timeout) {
			super(a, timeout);
			this.msg = msg;
		}
		
		@Override
		public void onWake(){
			send(msg);
		}
	}
	
	protected class WakeFlagSet extends WakerBehaviour{
		private Flag flag;
		public WakeFlagSet(Flag flag, Agent a, long timeout) {
			super(a, timeout);
			this.flag = flag;
		}
		
		@Override
		public void onWake(){
			if (Division.this.flag != Flag.ATTACKING && Division.this.flag != Flag.DEFENDING){ Division.this.flag = flag;
			System.out.println(myAgent.getLocalName()+" setting flag: "+flag.toString());}
		}
	}
	
	protected class Target extends WakerBehaviour{
		public Target(Agent a, long timeout) {
			super(a, timeout);
		}

		public void onWake(){
			myAgent.addBehaviour(new DivisionSearcher());
			if (!(knownDivisions.isEmpty()) && flag == Flag.DEFAULT){
				AID target = new AID();
				double value = 9999999;
				for (AID agent : knownDivisions.keySet()){
					if (knownDivisions.get(agent).getAllignment() != null){
					if (knownDivisions.get(agent).getStrength() <= value && orders.getEnemies().contains(knownDivisions.get(agent).getAllignment())){ 
						value = knownDivisions.get(agent).getStrength();
						target = agent;
						//sending attack request
						Operation operation = new Operation();
						operation.setType(OperationType.ATTACK);
						operation.setValue(flag);
						ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
						msg.addReceiver(target);
						try {
							msg.setContentObject(operation);
						} 
						catch (IOException e) {e.printStackTrace();}
//						send(msg);
						myAgent.addBehaviour(new WakeSender(msg, myAgent, divisionInfo.getSpeed()));
//						System.out.println(myAgent.getLocalName()+" REQUESTs to attack "+agent.getLocalName());
						break;
					}
				}}
			}
		}
	}
	
	protected class Reconaissance extends TickerBehaviour{

		public Reconaissance(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}
		public void onTick(){
			myAgent.addBehaviour(new DivisionSearcher());
//			System.out.println(myAgent.getLocalName() + "knows " + knownDivisions);
			
		}
	}
	
	protected class DivisionSearcher extends OneShotBehaviour{
		public void action(){
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("division@"+location.getProvinceName());
			template.addServices(sd);
			knownDivisions = searchDF(myAgent, template);
			for (AID agent : knownDivisions.keySet()){
				Operation operation = new Operation();
				operation.setType(OperationType.ALLIGNMENT_CHECK);
				operation.setValue("?");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(agent);
				try {
					msg.setContentObject(operation);
				} 
				catch (IOException e) {e.printStackTrace();}
				send(msg);
//				System.out.println(myAgent.getLocalName()+" requests from "+((AID) msg.getAllReceiver().next()).getLocalName());
			}
		}
	}
	
    private class Receiver extends CyclicBehaviour{
    	public void action(){
    		ACLMessage msg= receive();
    		if (msg!=null) {
    			try {
					Object content = msg.getContentObject();
					Operation chOp = ((Operation) content);
					if (chOp.getValue() instanceof Damage) System.out.println(getLocalName()+" received "+(((Damage) chOp.getValue()).getDamageDealt()));
    			
	    			switch (msg.getPerformative()){
		    			case (ACLMessage.REQUEST):{
			    			if (content instanceof Operation){
			    				Operation opSent = (Operation) content;
			    				switch (opSent.getType()){
				    				case ALLIGNMENT_CHECK:{
				    					Operation rep = new Operation();
				    					rep.setType(OperationType.ALLIGNMENT_CHECK);
				    					rep.setValue(divisionInfo);
					    				ACLMessage reply = msg.createReply();
							            reply.setPerformative( ACLMessage.INFORM );
							            reply.setContentObject(rep);
							            send(reply);
					    				break;}
				    				case ATTACK:{
				    					if (flag == Flag.ATTACKING || flag == Flag.DEFENDING || flag == Flag.HIDDEN || flag == Flag.RETREATING){
				    						Operation rep = new Operation();
					    					rep.setType(OperationType.DEFEND);
					    					rep.setValue(flag);
						    				ACLMessage reply = msg.createReply();
								            reply.setPerformative( ACLMessage.REFUSE );
								            reply.setContentObject(rep);
								            send(reply);
//								            System.out.println(myAgent.getLocalName()+" declines!");	            
				    					} else if (flag == Flag.FORTIFIED || flag == Flag.DEFAULT){
				    						flag = Flag.DEFENDING;
				    						Operation rep = new Operation();
					    					rep.setType(OperationType.DEFEND);
					    					rep.setValue(flag);
						    				ACLMessage reply = msg.createReply();
								            reply.setPerformative( ACLMessage.AGREE );
								            reply.setContentObject(rep);
								            send(reply);
//								            System.out.println(myAgent.getLocalName()+" agrees!");
				    					}
				    					break;}
			    					}
			    				}
		    			break;}
		    			case (ACLMessage.REFUSE):{
		    				if (content instanceof Operation){
		    					Operation opRec = (Operation) content;
			    				switch (opRec.getType()){
			    					case DEFEND:{
			    						flag = Flag.DEFAULT;
			    						break;}
			    					}
		    				}
		    				break;}
		    			case (ACLMessage.AGREE):{
		    				if (content instanceof Operation){
		    					Operation opRec = (Operation) content;
			    				switch (opRec.getType()){
			    					case DEFEND:{
			    						flag = Flag.ATTACKING;
			    						Damage counterAttack = new Damage();
			    						counterAttack.setDamageDealt(Math.round(divisionInfo.getStrength()/20));
				    					Operation rep = new Operation();
					    				rep.setType(OperationType.ATTACK);
					    				rep.setValue(counterAttack);
						    			ACLMessage reply = msg.createReply();
								        reply.setPerformative( ACLMessage.INFORM );
								        reply.setContentObject(rep);
								        send(reply);
							            System.out.println(myAgent.getLocalName()+" starts to attack "+msg.getSender().getLocalName());
					    				break;}
			    					}
		    				}
		    			break;}
		    			case (ACLMessage.INFORM):{
		    				if (content instanceof Operation){
		    					Operation opRec = (Operation) content;
			    				switch (opRec.getType()){
			    					case ALLIGNMENT_CHECK:{
			    						DivisionInfo value = (DivisionInfo) opRec.getValue();
			    						if (value != null) knownDivisions.replace(msg.getSender(), value);	
//			    						System.out.println(myAgent.getLocalName() +" Received "+opRec.getValue()+" from "+msg.getSender().getLocalName());
			    						break;}
			    					case ATTACK:{
			    						Damage damageReceived = (Damage) opRec.getValue();
			    						divisionInfo.setManpower(divisionInfo.getManpower()-damageReceived.getDamageDealt());
			    						System.out.println(msg.getSender().getLocalName() + " ATTACKed " + myAgent.getLocalName() + " for " + damageReceived.getDamageDealt());
			    						System.out.println(myAgent.getLocalName()+"'s manpower is now: "+divisionInfo.getManpower());
			    						if (divisionInfo.getManpower() > 0){
			    							Damage counterAttack = new Damage();
			    							counterAttack.setDamageDealt(Math.round(divisionInfo.getStrength()/20*100)/100);
				    						Operation rep = new Operation();
					    					rep.setType(OperationType.DEFEND);
					    					rep.setValue(counterAttack);
						    				ACLMessage reply = msg.createReply();
								            reply.setPerformative( ACLMessage.INFORM );
								            reply.setContentObject(rep);
//								            send(reply);
								            myAgent.addBehaviour(new WakeSender(reply, myAgent, divisionInfo.getSpeed()));
			    						} else{
			    							Damage counterAttack = new Damage();
				    						counterAttack.setDamageDealt(Math.round(divisionInfo.getStrength()/20*100)/100);
					    					Operation rep = new Operation();
						    				rep.setType(OperationType.KILLED);
						    				rep.setValue(divisionInfo.getManpower());
							    			ACLMessage reply = msg.createReply();
									        reply.setPerformative( ACLMessage.INFORM );
									        reply.setContentObject(rep);
									        send(reply);
			    							myAgent.doDelete();
			    						}
					    				break;}
			    					case DEFEND:{
			    						Damage damageReceived = (Damage) opRec.getValue();
			    						divisionInfo.setManpower(divisionInfo.getManpower()-damageReceived.getDamageDealt());
			    						System.out.println(msg.getSender().getLocalName() + " DEFENDEDed  from " + myAgent.getLocalName() + " for " + damageReceived.getDamageDealt());
			    						System.out.println(getLocalName()+"'s manpower is now: "+divisionInfo.getManpower());
				    					if (divisionInfo.getManpower() > 0){
				    						Damage counterAttack = new Damage();
				    						counterAttack.setDamageDealt(Math.round(divisionInfo.getStrength()/20*100)/100);
					    					Operation rep = new Operation();
						    				rep.setType(OperationType.ATTACK);
						    				rep.setValue(counterAttack);
							    			ACLMessage reply = msg.createReply();
									        reply.setPerformative( ACLMessage.INFORM );
									        reply.setContentObject(rep);
//									        send(reply);
									        myAgent.addBehaviour(new WakeSender(reply, myAgent, divisionInfo.getSpeed()));
				    					} 
				    					if (divisionInfo.getManpower() <= 0){
				    						Damage counterAttack = new Damage();
				    						counterAttack.setDamageDealt(Math.round(divisionInfo.getStrength()/20*100)/100);
					    					Operation rep = new Operation();
						    				rep.setType(OperationType.KILLED);
						    				rep.setValue(divisionInfo.getManpower());
							    			ACLMessage reply = msg.createReply();
									        reply.setPerformative( ACLMessage.INFORM );
									        reply.setContentObject(rep);
									        send(reply);
				    						myAgent.doDelete();
				    					}
				    					break;}
			    					case KILLED:{
			    						flag = Flag.REGROUPING;
			    						knownDivisions.remove(msg.getSender());
			    						System.out.println(myAgent.getLocalName()+" setting flag: REGROUPING");
			    						addBehaviour(new WakeFlagSet(Flag.DEFAULT, myAgent, divisionInfo.getSpeed()*2));
			    						break;}
			    					}
			    				}
		    			break;}
	    			}
    			} catch (UnreadableException | IOException e) {}
    		}
    		else block();
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
	
	public void registerAgent(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sdName  = new ServiceDescription();
		ServiceDescription sdAllignment  = new ServiceDescription();
		sdName.setType("division@"+location.getProvinceName());
		sdAllignment.setType(divisionInfo.getAllignment().toString()+"@"+location.getProvinceName());
		sdName.setName(getLocalName());
		sdAllignment.setName(getLocalName());
        System.out.println("Registering division@"+location.getProvinceName()+" ("+divisionInfo.getAllignment().toString()+") named "+getLocalName());
        dfd.addServices(sdName);
        dfd.addServices(sdAllignment);
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
	}
	
	//Pomocnicza do rejestracji agenta
	void register( ServiceDescription sd)
	{
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

        try {
            dfd.addServices(sd);
			DFService.register(this,dfd);
		}
        catch (FIPAException fe) { fe.printStackTrace(); }
	}
	
	//Pomocnicza do wyszukiwania agentow
	HashMap<AID, DivisionInfo> searchDF(Agent agent, DFAgentDescription template ){
		HashMap<AID, DivisionInfo> agents = new HashMap<AID, DivisionInfo>();
		try {
			DFAgentDescription[] result = DFService.search(agent,
					template);
			//knownEnemies.clear();
			for (int i = 0; i < result.length; ++i) {
				if (!result[i].getName().equals(agent.getAID())) agents.put(result[i].getName(), null);
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return agents;
	}
	
}
