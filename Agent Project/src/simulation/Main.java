package simulation;

import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {
	public static void main(String [] args) throws StaleProxyException{
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		Profile profile = new ProfileImpl(null, 1200, null);
		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
//		ProfileImpl pContainer = new ProfileImpl(null, 1200, null);
//		jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
		AgentController rma = mainContainer.createNewAgent("rma",
		"jade.tools.rma.rma", new Object[0]);
		rma.start();
		
		createDivision(mainContainer, "l1", 800, 100, 100, "ISIS", "Latakia");
		createDivision(mainContainer, "l2", 885, 120, 150, "ISIS", "Latakia");
		createDivision(mainContainer, "l3", 450, 2700, 2000, "USA", "Latakia");
		createDivision(mainContainer, "l4", 4350, 200, 100, "ISIS", "Latakia");
		createDivision(mainContainer, "l5", 2350, 200, 100, "ISIS", "Latakia");
		createDivision(mainContainer, "l6", 1500, 800, 3000, "ASSAD", "Latakia");
		
		createDivision(mainContainer, "d1", 2000, 1000, 1000, "ISIS", "Dimashq");
		createDivision(mainContainer, "d2", 2950, 2200, 1500, "ASSAD", "Dimashq");
		createDivision(mainContainer, "d3", 750, 3700, 4000, "USA", "Dimashq");
		createDivision(mainContainer, "d4", 2350, 200, 100, "ISIS", "Dimashq");
		createDivision(mainContainer, "d5", 1350, 200, 100, "ISIS", "Dimashq");
		createDivision(mainContainer, "d6", 2000, 800, 3000, "ASSAD", "Dimashq");
		
	}
	
	public static void createDivision(jade.wrapper.AgentContainer cont, String name, double manpower, double equipment, double experience, String allignment, String location) throws StaleProxyException{
		cont.createNewAgent(name,"troops.Division", new Object[] {manpower, equipment, experience,allignment,location}).start();
	}
}
