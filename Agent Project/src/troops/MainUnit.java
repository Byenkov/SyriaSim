package troops;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import map.Province;

public class MainUnit extends Agent {
	private Allignment allignment;
	private Orders orders;
	private List<KnownUnit> allies;
	private List<KnownUnit> neutrals;
	private List<KnownUnit> enemies;
	private Province location;
	
	private AID recoUnit;
	private AID milUnit;
	private AID province;
	
	protected void setup(){
		Object[] args = getArguments();
		
		String allignmentString = (String) args[1];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		location = map.ProvinceFactory.getProvince((String) args[0]);
		orders = new Orders();
		orders.setDefault(allignment);
		allies = new ArrayList<KnownUnit>();
		enemies = new ArrayList<KnownUnit>();
		neutrals = new ArrayList<KnownUnit>();
		
		registerAgent();
		setMyAgents();
		
		addBehaviour(new RecoHandler());
		addBehaviour(new AgentActivator());
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
		DFAgentDescription templateReco = new DFAgentDescription();
		ServiceDescription sdReco = new ServiceDescription();
		sdReco.setType("RecoUnit@"+location.getProvinceName());
		sdReco.setName(getLocalName()+"RecoUnit");
		templateReco.addServices(sdReco);
		DFAgentDescription[] result; //TODO - '5' jest tylko zastêpczo
		try {
			result = DFService.search(this, templateReco);
			recoUnit = result[0].getName();
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		DFAgentDescription templateMil = new DFAgentDescription();
		ServiceDescription sdMil = new ServiceDescription();
		sdMil.setType("MilUnit@"+location.getProvinceName());
		sdMil.setName(getLocalName()+"MilUnit");
		templateMil.addServices(sdMil);
		try {
			result = DFService.search(this, templateMil);
			milUnit = result[0].getName();
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		DFAgentDescription templateProv = new DFAgentDescription();
		ServiceDescription sdProv = new ServiceDescription();
		sdProv.setType("Province");
		sdProv.setName(location.getProvinceName());
		templateProv.addServices(sdProv);
		try {
			result = DFService.search(this, templateProv);
			province = result[0].getName();
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	protected class AgentActivator extends OneShotBehaviour{

		@Override
		public void action() {
			Operation operation = new Operation();
			operation.setType(OperationType.ACTIVATE);
			operation.setValue(allignment);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("ActivateUnit");
			msg.addReceiver(recoUnit);
			msg.addReceiver(milUnit);
			msg.addReceiver(province);
			try {
				msg.setContentObject(allignment);
			}
			catch (IOException e) {e.printStackTrace();}
			send(msg);
		}
		
	}
	
	protected void takeDown() {
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
		ServiceDescription sd = new ServiceDescription();
		sd.setType("MainUnit@"+location.getProvinceName());
		sd.setName(getLocalName());
		sd.setOwnership(allignment.toString());;
        dfd.addServices(sd);
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
	}
}