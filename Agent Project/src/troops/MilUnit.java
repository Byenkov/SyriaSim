package troops;

import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import map.Province;
import map.ProvinceFactory;

public class MilUnit extends Agent{
	private AID myCommand;
	private List<KnownUnit> knownUnits;
	private Province location;
	private DivisionInfo divisionInfo = new DivisionInfo();
	private Allignment allignment;

	protected void setup(){
		Object[] args = getArguments();
		
		String allignmentString = (String) args[1];
		if (allignmentString.equals("ASSAD")) this.allignment = Allignment.ASSAD;
		if (allignmentString.equals("USA")) this.allignment = Allignment.USA;
		if (allignmentString.equals("ISIS")) this.allignment = Allignment.ISIS;
		
		location = ProvinceFactory.getProvince((String) args[0]);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd  = new ServiceDescription();
		sd.setType("MilUnit@"+location.getProvinceName());
		sd.setName(getLocalName());
		sd.setOwnership(allignment.toString());
        dfd.addServices(sd);
        System.out.println(getAID());
        try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {e.printStackTrace();}
        addBehaviour(new Activator());
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
