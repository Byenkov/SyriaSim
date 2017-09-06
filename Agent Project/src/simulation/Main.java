package simulation;

import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.SwingUtilities;

import gui.Map;
import jade.core.Profile;
import jade.core.ProfileImpl;

public class Main {
	public static void main(String [] args) throws StaleProxyException{
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		Profile profile = new ProfileImpl(null, 1200, null);
		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
		AgentController rma = mainContainer.createNewAgent("rma",
		"jade.tools.rma.rma", new Object[0]);
		
		rma.start();
		mainContainer.createNewAgent("ASSAD", "troops.Leadership", new String[]{"ASSAD", "1000", "1000"}).start();
		mainContainer.createNewAgent("USA", "troops.Leadership", new String[]{"USA", "1000", "1000"}).start();
		mainContainer.createNewAgent("ISIS", "troops.Leadership", new String[]{"ISIS", "1000", "1000"}).start();

		mainContainer.createNewAgent("Dimashq", "provinces.ProvinceAgent", new String[]{"Dimashq"}).start();
		
		createDivision(mainContainer, "us1dim", "Dimashq", "USA", "25", "83", "0");
		createDivision(mainContainer, "us2dim", "Dimashq", "USA", "25", "83", "0");
		createDivision(mainContainer, "as1dim", "Dimashq", "ASSAD", "40", "90", "0");
		createDivision(mainContainer, "is1dim", "Dimashq", "ISIS", "90", "39", "0");
		createDivision(mainContainer, "is10dim", "Dimashq", "ISIS", "90", "39", "0");
		createDivision(mainContainer, "is3dim", "Dimashq", "ISIS", "50", "30", "50");
		createDivision(mainContainer, "as2dim", "Dimashq", "ASSAD", "15", "20", "50");
		createDivision(mainContainer, "us3dim", "Dimashq", "USA", "38", "73", "0");
		
//		mainContainer.createNewAgent("Aleppo", "provinces.ProvinceAgent", new String[]{"Aleppo"}).start();
//		
//		createDivision(mainContainer, "us1ale", "Aleppo", "USA", "25", "83", "0");
//		createDivision(mainContainer, "us2ale", "Aleppo", "USA", "25", "83", "0");
//		createDivision(mainContainer, "as1ale", "Aleppo", "ASSAD", "40", "90", "0");
//		createDivision(mainContainer, "is1ale", "Aleppo", "ISIS", "90", "39", "0");
//		createDivision(mainContainer, "is2ale", "Aleppo", "ISIS", "30", "2", "0");
//		createDivision(mainContainer, "as3ale", "Aleppo", "ASSAD", "50", "30", "0");
//		createDivision(mainContainer, "is4ale", "Aleppo", "ISIS", "15", "20", "0");
//		createDivision(mainContainer, "is5ale", "Aleppo", "ISIS", "38", "73", "0");
//		
//		mainContainer.createNewAgent("Tartus", "provinces.ProvinceAgent", new String[]{"Tartus"}).start();
//		
//		createDivision(mainContainer, "us1tar", "Tartus", "USA", "25", "83", "0");
//		createDivision(mainContainer, "us2tar", "Tartus", "USA", "25", "83", "0");
//		createDivision(mainContainer, "as1tar", "Tartus", "ASSAD", "40", "90", "0");
//		createDivision(mainContainer, "is1tar", "Tartus", "ISIS", "90", "39", "0");
//		createDivision(mainContainer, "is2tar", "Tartus", "ISIS", "30", "2", "0");
//		createDivision(mainContainer, "is3tar", "Tartus", "ISIS", "50", "30", "50");
//		createDivision(mainContainer, "is4tar", "Tartus", "ISIS", "15", "20", "50");
//		createDivision(mainContainer, "is5tar", "Tartus", "ISIS", "38", "173", "0");
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				new Map();
//			}
//		});
		
	}
	
	
	
	public static void createDivision(jade.wrapper.AgentContainer cont, String name, String province, String allignment, String manpower, String equipmnent, String experience) throws StaleProxyException{
		cont.createNewAgent(name,"troops.MainUnit", new String[]{province, allignment}).start();
		cont.createNewAgent(name+"MilUnit","troops.MilUnit", new String[]{province, allignment, manpower, equipmnent, experience}).start();
		cont.createNewAgent(name+"RecoUnit","troops.RecoUnit", new String[]{province, allignment}).start();	
	}
}
