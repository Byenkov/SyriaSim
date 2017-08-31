package provinces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import map.*;

public class ProvinceAgent extends Agent {
	private Province properties;
	private List<AID> localAgents;
	private Weather weather;
	private int weatherTime;
	
	protected void setup(){
		Object[] args = getArguments();
		localAgents = new ArrayList<AID>();
		properties = ProvinceFactory.getProvince((String) args[0]);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sdName  = new ServiceDescription();
		sdName.setType("Province");
		sdName.setName(properties.getProvinceName());
        dfd.addServices(sdName);
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
        addBehaviour(new ProvinceWeather());

        addBehaviour(new AddUnit());
        addBehaviour(new RemoveUnit());
	}
	
	private class ProvinceWeather extends CyclicBehaviour{
		@Override
		public void action() {
		    Weather values[] = Weather.values(); 
		    weather = values[(int) (Math.random() * values.length)];
			Random generator = new Random();
			weatherTime = generator.nextInt(20) * 1000 + 2000;
			System.out.println(weather + ": Duration: " + weatherTime/1000 + " hours.");
			block( weatherTime );
		}
	}
	private class AddUnit extends CyclicBehaviour{
		MessageTemplate mt;
		@Override
		public void action() {
			mt = MessageTemplate.MatchConversationId("ActivateUnit");
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
			mt = MessageTemplate.MatchConversationId("DeactivateUnit");
			ACLMessage msg= receive(mt);
			if (msg != null){
				localAgents.remove(msg.getSender());
				System.out.println("Removing "+msg.getSender().getLocalName());
			}
			else block();
		}	
	}
	
	protected void takeDown(){
		System.out.println("Deregistering "+getLocalName());
		try {
			DFService.deregister(this);
			}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	

}
