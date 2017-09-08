package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import map.ProvinceFactory;
import troops.Allignment;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea1;
	private JTextArea textArea2;
	private String provinceClicked = "";
	
	private static ArrayList<AgentController> agents = new ArrayList<AgentController>();
	private static ArrayList<AgentController> leaderAgents = new ArrayList<AgentController>();
	private static ArrayList<AgentController> provinceAgents = new ArrayList<AgentController>();
	
	private static boolean started;
	public Map() {

		setSize(new Dimension(750, 540));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		JScrollPane scroll;
		
		class Panel extends JPanel implements ActionListener{
			Timer timer=new Timer(250, this);

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
													+ "\nValue: " + ProvinceFactory.getProvince(provinceClicked).getImportance()
			        								+ "\n\n Weather: " + ProvinceFactory.getProvince(provinceClicked).getWeather()
			                						+ "\n\nPopulation: " + ProvinceFactory.getProvince(province.getName()).getPopulation() 
			                						+ "\nController: " + ProvinceFactory.getProvince(province.getName()).getController()
        											+ "\nUSA power: " + ProvinceFactory.getProvince(province.getName()).getStrength("USA")
													+ "\nASSAD power: " + ProvinceFactory.getProvince(province.getName()).getStrength("ASSAD")
			                						+ "\nISIS power: " + ProvinceFactory.getProvince(province.getName()).getStrength("ISIS")
								);
			                	textArea2.setText("\nNews: \n\n" +
										ProvinceFactory.getProvince(provinceClicked).getCurrentNews());
			                	if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ISIS) province.setController("ISIS");
				                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ASSAD) province.setController("Assad");
				                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.USA) province.setController("USA");
				                Map.this.repaint();
			                }
		            	}
		            }
		        });
			}

			public void actionPerformed(ActionEvent ev) {
				if(ev.getSource()==timer){
	                for (Province province : syria.getProvinces().values()) {
	                	if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ISIS) province.setController("ISIS");
		                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.ASSAD) province.setController("Assad");
		                else if(ProvinceFactory.getProvince(province.getName()).getController() == Allignment.USA) province.setController("USA");
	                }
	                Map.this.repaint();
						if(!provinceClicked.equals("")){
						textArea1.setText("\nProvince: " + provinceClicked 
								+ "\nValue: " + ProvinceFactory.getProvince(provinceClicked).getImportance()
								+ "\n\n Weather: " + ProvinceFactory.getProvince(provinceClicked).getWeather()
								+ "\n\nPopulation: " + ProvinceFactory.getProvince(provinceClicked).getPopulation() 
								+ "\nController: " + ProvinceFactory.getProvince(provinceClicked).getController()
								+ "\nUSA power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("USA")
								+ "\nASSAD power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("ASSAD")
								+ "\nISIS power: " + ProvinceFactory.getProvince(provinceClicked).getStrength("ISIS")
								);	
						textArea2.setText("\nNews: \n\n" +
								ProvinceFactory.getProvince(provinceClicked).getCurrentNews());
						}	
						else {
							textArea1.setText("\nSyriaSim");
							textArea2.setText("\nSyriaSim");

						}
					}
			}
		};
		
		Country syria = new Country(270.0, 150.0);
		Panel panel = new Panel(syria);
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		textArea1 = new JTextArea("\nSyriaSimi");
		textArea1.setEditable(false);
		textArea1.setBounds(15,30,200,180);
		textArea1.setBackground(panel.getBackground());
		textArea1.setBorder(border);
		textArea1.setLineWrap(true);
		textArea1.setWrapStyleWord(true);

		textArea2 = new JTextArea("\nSyriaSimi");
		textArea2.setEditable(false);
		textArea2.setBounds(15,240,200,180);
		textArea2.setBackground(panel.getBackground());
		textArea2.setBorder(border);
		textArea2.setLineWrap(true);
		textArea2.setWrapStyleWord(true);
		
		scroll = new JScrollPane (textArea2, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBounds(15,240,200,180);
		
		JButton b2 = new JButton("Start");
	    b2.setVerticalTextPosition(AbstractButton.CENTER);
	    b2.setHorizontalTextPosition(AbstractButton.CENTER);
	    b2.setBounds(70, 450, 90, 30);
	    b2.setMnemonic(KeyEvent.VK_M);
	    
	    b2.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	        	 try{
		             if (!started){
		            	started = true;
		         		for (AgentController agent : leaderAgents) agent.start();
		         		for (AgentController agent : provinceAgents) agent.start();
		         		for (AgentController agent : agents) agent.start();
		             }
	        	 } catch (StaleProxyException e1) {
					e1.printStackTrace();
				}
	          }          
	       });
		
	    
		setTitle("Map");
		add(textArea1);
		add(scroll);
		add(b2);
		add(panel);
		setVisible(true);
	}
	
	public static void main(String arg[]) throws StaleProxyException{
		
		started = false;
		
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		Profile profile = new ProfileImpl(null, 1200, null);
		jade.wrapper.AgentContainer mainContainer = rt.createMainContainer(profile);
		AgentController rma = mainContainer.createNewAgent("rma",
		"jade.tools.rma.rma", new Object[0]);
		
		rma.start();
		
		leaderAgents.add(mainContainer.createNewAgent("ASSAD", "troops.Leadership", new String[]{"ASSAD", "300", "250"}));
		leaderAgents.add(mainContainer.createNewAgent("USA", "troops.Leadership", new String[]{"USA", "175", "300"}));
		leaderAgents.add(mainContainer.createNewAgent("ISIS", "troops.Leadership", new String[]{"ISIS", "350", "100"}));

		provinceAgents.add(mainContainer.createNewAgent("Dimashq", "provinces.ProvinceAgent", new String[]{"Dimashq"}));
		
		agents.addAll(createDivision(mainContainer, "us1dim", "Dimashq", "USA", "250", "55", "0"));
		agents.addAll(createDivision(mainContainer, "us2dim", "Dimashq", "USA", "250", "122", "0"));
		agents.addAll(createDivision(mainContainer, "as1dim", "Dimashq", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1dim", "Dimashq", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "is3dim", "Dimashq", "ISIS", "500", "30", "50"));
		agents.addAll(createDivision(mainContainer, "as2dim", "Dimashq", "ASSAD", "150", "80", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Aleppo", "provinces.ProvinceAgent", new String[]{"Aleppo"}));
		
		agents.addAll(createDivision(mainContainer, "us1ale", "Aleppo", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2ale", "Aleppo", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1ale", "Aleppo", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1ale", "Aleppo", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3ale", "Aleppo", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5ale", "Aleppo", "ISIS", "308", "73", "0"));
		
		provinceAgents.add(mainContainer.createNewAgent("Tartus", "provinces.ProvinceAgent", new String[]{"Tartus"}));
		
		agents.addAll(createDivision(mainContainer, "us1tar", "Tartus", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2tar", "Tartus", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1tar", "Tartus", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is4tar", "Tartus", "ISIS", "1050", "1200", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Ar_Raqqah", "provinces.ProvinceAgent", new String[]{"Ar_Raqqah"}));
		
		agents.addAll(createDivision(mainContainer, "us1arr", "Ar_Raqqah", "USA", "250", "55", "0"));
		agents.addAll(createDivision(mainContainer, "us2arr", "Ar_Raqqah", "USA", "250", "122", "0"));
		agents.addAll(createDivision(mainContainer, "as1arr", "Ar_Raqqah", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1arr", "Ar_Raqqah", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "is3arr", "Ar_Raqqah", "ISIS", "500", "30", "50"));
		agents.addAll(createDivision(mainContainer, "as2arr", "Ar_Raqqah", "ASSAD", "150", "80", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Al_Hasakah", "provinces.ProvinceAgent", new String[]{"Al_Hasakah"}));
		
		agents.addAll(createDivision(mainContainer, "us1alh", "Al_Hasakah", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2alh", "Al_Hasakah", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1alh", "Al_Hasakah", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1alh", "Al_Hasakah", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3alh", "Al_Hasakah", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5alh", "Al_Hasakah", "ISIS", "308", "73", "0"));
		
		provinceAgents.add(mainContainer.createNewAgent("As_Suwayda", "provinces.ProvinceAgent", new String[]{"As_Suwayda"}));
		
		agents.addAll(createDivision(mainContainer, "us1ass", "As_Suwayda", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2ass", "As_Suwayda", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1ass", "As_Suwayda", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is4ass", "As_Suwayda", "ISIS", "105", "200", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Daraa", "provinces.ProvinceAgent", new String[]{"Daraa"}));
		
		agents.addAll(createDivision(mainContainer, "us1dar", "Daraa", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2dar", "Daraa", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1dar", "Daraa", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1dar", "Daraa", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3dar", "Daraa", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5dar", "Daraa", "ISIS", "308", "73", "0"));
		
		provinceAgents.add(mainContainer.createNewAgent("Deir_ez_Zor", "provinces.ProvinceAgent", new String[]{"Deir_ez_Zor"}));
		
		agents.addAll(createDivision(mainContainer, "us1dei", "Deir_ez_Zor", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2dei", "Deir_ez_Zor", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1dei", "Deir_ez_Zor", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is4dei", "Deir_ez_Zor", "ISIS", "105", "200", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Hama", "provinces.ProvinceAgent", new String[]{"Hama"}));
		
		agents.addAll(createDivision(mainContainer, "us1ham", "Hama", "USA", "250", "55", "0"));
		agents.addAll(createDivision(mainContainer, "us2ham", "Hama", "USA", "250", "122", "0"));
		agents.addAll(createDivision(mainContainer, "as1ham", "Hama", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1ham", "Hama", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "is3ham", "Hama", "ISIS", "500", "30", "50"));
		agents.addAll(createDivision(mainContainer, "as2ham", "Hama", "ASSAD", "150", "80", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Homs", "provinces.ProvinceAgent", new String[]{"Homs"}));
		
		agents.addAll(createDivision(mainContainer, "us1hom", "Homs", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2hom", "Homs", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1hom", "Homs", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1hom", "Homs", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3hom", "Homs", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5hom", "Homs", "ISIS", "308", "73", "0"));
		
		provinceAgents.add(mainContainer.createNewAgent("Idlib", "provinces.ProvinceAgent", new String[]{"Idlib"}));
		
		agents.addAll(createDivision(mainContainer, "us1idl", "Idlib", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2idl", "Idlib", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1idl", "Idlib", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1idl", "Idlib", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3idl", "Idlib", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5idl", "Idlib", "ISIS", "308", "73", "0"));
		
		provinceAgents.add(mainContainer.createNewAgent("Latakia", "provinces.ProvinceAgent", new String[]{"Latakia"}));
		
		agents.addAll(createDivision(mainContainer, "us1lat", "Latakia", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2lat", "Latakia", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1lat", "Latakia", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is4lat", "Latakia", "ISIS", "105", "200", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Quneitra", "provinces.ProvinceAgent", new String[]{"Quneitra"}));
		
		agents.addAll(createDivision(mainContainer, "us1qun", "Quneitra", "USA", "250", "55", "0"));
		agents.addAll(createDivision(mainContainer, "us2qun", "Quneitra", "USA", "250", "122", "0"));
		agents.addAll(createDivision(mainContainer, "as1qun", "Quneitra", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1qun", "Quneitra", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "is3qun", "Quneitra", "ISIS", "500", "30", "50"));
		agents.addAll(createDivision(mainContainer, "as2qun", "Quneitra", "ASSAD", "150", "80", "50"));
		
		provinceAgents.add(mainContainer.createNewAgent("Rif_Dimashq", "provinces.ProvinceAgent", new String[]{"Rif_Dimashq"}));
		
		agents.addAll(createDivision(mainContainer, "us1rif", "Rif_Dimashq", "USA", "205", "83", "0"));
		agents.addAll(createDivision(mainContainer, "us2rif", "Rif_Dimashq", "USA", "250", "83", "0"));
		agents.addAll(createDivision(mainContainer, "as1rif", "Rif_Dimashq", "ASSAD", "400", "90", "0"));
		agents.addAll(createDivision(mainContainer, "is1rif", "Rif_Dimashq", "ISIS", "900", "39", "0"));
		agents.addAll(createDivision(mainContainer, "as3rif", "Rif_Dimashq", "ASSAD", "500", "30", "0"));
		agents.addAll(createDivision(mainContainer, "is5rif", "Rif_Dimashq", "ISIS", "308", "73", "0"));

		new Map();
	}
	
	public static ArrayList<AgentController> createDivision(jade.wrapper.AgentContainer cont, String name, String province, String allignment, String manpower, String equipmnent, String experience) throws StaleProxyException{
		ArrayList<AgentController> agents = new ArrayList<AgentController>();
		agents.add(cont.createNewAgent(name,"troops.MainUnit", new String[]{province, allignment}));
		agents.add(cont.createNewAgent(name+"MilUnit","troops.MilUnit", new String[]{province, allignment, manpower, equipmnent, experience}));
		agents.add(cont.createNewAgent(name+"RecoUnit","troops.RecoUnit", new String[]{province, allignment}));
		return agents;
	}
}