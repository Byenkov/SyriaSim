package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.tools.DummyAgent.DummyAgent;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import map.MakeMap;
import map.ProvinceFactory;
import provinces.ProvinceAgent;
import troops.DivisionInfo;

public class Map extends JFrame {

	private ProvinceFactory  provinceFactory= new ProvinceFactory() ;
	private static final long serialVersionUID = 1L;
	protected JTextArea textArea1;
	protected JTextArea textArea2;

	public Map() {

		setSize(new Dimension(720, 560));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		class Panel extends JPanel{
			private Country syria;
			
			public Panel(Country syria){
				this.syria = syria;
			}
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D) g;
				syria.paintMap(g2);
				/*
				 * Mouse Listener
				 * pozwala zaprezentowaæ dzia³anie mapy
				 * PPM - nastêpny kontroler
				 * LPM - poprzedni kontroler
				 */
				addMouseListener(new MouseAdapter() {
		            @Override
		            public void mousePressed(MouseEvent e) {
		            	for (Province province : syria.getProvinces().values()){
			                if (province.getArea().contains(e.getPoint()) && SwingUtilities.isRightMouseButton(e)) {
			                	province.empty(g2);
			                	textArea1.setText("Nazwa prowincji: " + province.getName() + "\n Liczba ludnoœci: "
			                			+ provinceFactory.getProvince(province.getName()).getPopulation());
			                	System.out.print(province.getName() + ": " + province.getController());
			                	if(province.getController() == "USA") province.setController("ISIS");
				                else if(province.getController() == "ISIS") province.setController("Assad");
				                else if(province.getController() == "Assad") province.setController("USA");
				                System.out.print(" -> " + province.getController() + "\n");
				                Map.this.repaint();
			                } else if (province.getArea().contains(e.getPoint())&& SwingUtilities.isLeftMouseButton(e)) {
			                	province.empty(g2);
			                	System.out.print(province.getName() + ": " + province.getController());
			                	if(province.getController() == "USA") province.setController("Assad");
				                else if(province.getController() == "Assad") province.setController("ISIS");
				                else if(province.getController() == "ISIS") province.setController("USA");
				                System.out.print(" -> " + province.getController() + "\n");
				                Map.this.repaint();
			                }
			                	
		            	}
		            }
		        });
			}

//			@Override
//			public void actionPerformed(ActionEvent ev) {
//				if(ev.getSource()==timer){
//					repaint();// this will call at every 1 second}
//				}
//			}
		};
		textArea1 = new JTextArea("DUPA");
		textArea1.setEditable(false);
		textArea1.setBounds(0,0,200,180);
		textArea2 = new JTextArea("DUPA2");
		textArea2.setEditable(false);
		textArea2.setBounds(0,210,200,180);
		Country syria = new Country(240.0, 150.0);
		Panel panel = new Panel(syria);
		setTitle("Map");
		add(textArea1);
		add(textArea2);
		add(panel);
		setVisible(true);
	}
	
	public static void main(String arg[]) throws StaleProxyException{
		MakeMap mapa = new MakeMap(); 
//		Runtime rt = Runtime.instance();
//		rt.setCloseVM(true);
//		Profile profile = new ProfileImpl(null, 1200, null);
//		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
//		AgentController rma = mainContainer.createNewAgent("rma","jade.tools.rma.rma", new Object[0]);
//		
//		rma.start();
//		mainContainer.createNewAgent("ASSAD", "troops.Leadership", new String[]{"ASSAD", "1000", "1000"}).start();
//		mainContainer.createNewAgent("USA", "troops.Leadership", new String[]{"USA", "1000", "1000"}).start();
//		mainContainer.createNewAgent("ISIS", "troops.Leadership", new String[]{"ISIS", "1000", "1000"}).start();
//
//		mainContainer.createNewAgent("Dimashq", "provinces.ProvinceAgent", new String[]{"Dimashq"}).start();
//		
//		createDivision(mainContainer, "us1dim", "Dimashq", "USA", "25", "83", "0");
//		createDivision(mainContainer, "us2dim", "Dimashq", "USA", "25", "83", "0");
//		createDivision(mainContainer, "as1dim", "Dimashq", "ASSAD", "40", "90", "0");
//		createDivision(mainContainer, "is1dim", "Dimashq", "ISIS", "90", "39", "0");
//		createDivision(mainContainer, "is10dim", "Dimashq", "ISIS", "90", "39", "0");
//		createDivision(mainContainer, "is3dim", "Dimashq", "ISIS", "50", "30", "50");
//		createDivision(mainContainer, "as2dim", "Dimashq", "ASSAD", "15", "20", "50");
//		createDivision(mainContainer, "us3dim", "Dimashq", "USA", "38", "73", "0");
//		
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new Map();
				}
			});
	}
	public static void createDivision(jade.wrapper.AgentContainer cont, String name, String province, String allignment, String manpower, String equipmnent, String experience) throws StaleProxyException{
		cont.createNewAgent(name,"troops.MainUnit", new String[]{province, allignment}).start();
		cont.createNewAgent(name+"MilUnit","troops.MilUnit", new String[]{province, allignment, manpower, equipmnent, experience}).start();
		cont.createNewAgent(name+"RecoUnit","troops.RecoUnit", new String[]{province, allignment}).start();	
	}
}