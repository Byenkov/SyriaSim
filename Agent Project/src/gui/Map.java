package gui;

import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

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
import map.ProvinceFactory;
import troops.Allignment;
import troops.DivisionInfo;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea1;
	private JTextArea textArea2;
	private String provinceClicked = "";
	public Map() {

		setSize(new Dimension(720, 560));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		class Panel extends JPanel implements ActionListener{
			Timer timer=new Timer(1000, this);

			private Country syria;
//			private Timer timer=new Timer(1000, this);
			
			public Panel(Country syria){
				this.syria = syria;
				timer.start();
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
			                if (province.getArea().contains(e.getPoint()) && SwingUtilities.isLeftMouseButton(e)) {
			                	province.empty(g2);
			                	provinceClicked = province.getName();
			                	textArea1.setText("\nProvince: " + province.getName() 
			                						+ "\n\nPopulation: " + ProvinceFactory.getProvince(province.getName()).getPopulation() 
			                						+ "\nController: " + ProvinceFactory.getProvince(province.getName()).getController()
        											+ "\nUSA power: " + ProvinceFactory.getProvince(province.getName()).getStrength("USA")
													+ "\nASSAD power: " + ProvinceFactory.getProvince(province.getName()).getStrength("ASSAD")
			                						+ "\nISIS power: " + ProvinceFactory.getProvince(province.getName()).getStrength("ISIS")
								);

			                	if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ISIS) province.setController("ISIS");
				                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ASSAD) province.setController("Assad");
				                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.USA) province.setController("USA");
				                Map.this.repaint();
			                }
		            	}
		            }
		        });
			}

//			@Override
			public void actionPerformed(ActionEvent ev) {
				if(ev.getSource()==timer){
	                Map.this.repaint();
						if(!provinceClicked.equals("")){
						textArea1.setText("\nProvince: " + provinceClicked 
						+ "\n\nPopulation: " + ProvinceFactory.getProvince(provinceClicked).getPopulation() 
						+ "\nController: " + ProvinceFactory.getProvince(provinceClicked).getController()
						+ "\nUSA power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("USA")
						+ "\nASSAD power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("ASSAD")
						+ "\nISIS power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("ISIS")
	);
//						textArea1.setText("\nProvince: " + provinceClicked );
						}	
						else {
							textArea1.setText("\nSymulacja konfliktu zbrojengo w syrii");
							textArea2.setText("\nSymulacja konfliktu zbrojengo w syrii");

						}
					}
			}
		};
		Country syria = new Country(240.0, 150.0);
		Panel panel = new Panel(syria);
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		textArea1 = new JTextArea("\nSymulacja konfliktu zbrojengo w syrii");
		textArea1.setEditable(false);
		textArea1.setBounds(0,0,200,180);
		textArea1.setBackground(panel.getBackground());
		textArea1.setBorder(border);
		textArea1.setLineWrap(true);
		textArea1.setWrapStyleWord(true);

		textArea2 = new JTextArea("\nSymulacja konfliktu zbrojengo w syrii");
		textArea2.setEditable(false);
		textArea2.setBounds(0,210,200,180);
		textArea2.setBackground(panel.getBackground());
		textArea2.setBorder(border);
		textArea2.setLineWrap(true);
		textArea2.setWrapStyleWord(true);
		
		setTitle("Map");
		add(textArea1);
		add(textArea2);
		add(panel);
		setVisible(true);
	}
	
	public static void main(String arg[]) throws StaleProxyException{
		new Map();
		
//		Runtime rt = Runtime.instance();
//		rt.setCloseVM(true);
//		Profile profile = new ProfileImpl(null, 1200, null);
//		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
//		AgentController rma = mainContainer.createNewAgent("rma",
//		"jade.tools.rma.rma", new Object[0]);
//		
//		rma.start();
//		
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
////		
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
	}
	
	public static void createDivision(jade.wrapper.AgentContainer cont, String name, String province, String allignment, String manpower, String equipmnent, String experience) throws StaleProxyException{
		cont.createNewAgent(name,"troops.MainUnit", new String[]{province, allignment}).start();
		cont.createNewAgent(name+"MilUnit","troops.MilUnit", new String[]{province, allignment, manpower, equipmnent, experience}).start();
		cont.createNewAgent(name+"RecoUnit","troops.RecoUnit", new String[]{province, allignment}).start();	
	}
}