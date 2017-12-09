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
import java.io.BufferedReader;
import java.io.FileReader;
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

import org.json.JSONArray;
import org.json.JSONObject;

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
			Timer timer=new Timer(30, this);

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
		
		String jsonData = readFile("./Config/initialState.json");
	    JSONObject config = new JSONObject(jsonData);
	    
	    JSONArray leadershipArray = new JSONArray(config.getJSONArray("leadership").toString());
	    for(int i = 0; i < leadershipArray.length(); i++) {
	        JSONObject leadership = leadershipArray.getJSONObject(i);
	        System.out.println(leadership.getInt("manpower"));
	        leaderAgents.add(mainContainer.createNewAgent(leadership.getString("alignment"), "troops.Leadership", new String[]{leadership.getString("alignment"), leadership.getString("manpower"), leadership.getString("manpower")}));
	    }
	    
	    JSONArray agentsArray = new JSONArray(config.getJSONArray("agents").toString());
	    for (int i = 0; i < agentsArray.length(); i++) {
	        JSONObject province = new JSONObject(agentsArray.getJSONObject(i).toString());
	        String provinceString = province.keySet().toArray()[0].toString();
	        provinceAgents.add(mainContainer.createNewAgent(provinceString, "provinces.ProvinceAgent", new String[]{provinceString}));
	        JSONArray troops = new JSONArray(((JSONObject) agentsArray.get(i)).getJSONArray(provinceString).toString());
	        for (int j = 0; j < troops.length(); j++) {
		        JSONObject agent = new JSONObject(troops.getJSONObject(j).toString());
		        agents.addAll(createDivision(mainContainer, agent.getString("name"), provinceString, agent.getString("alignment"), agent.getString("manpower"),agent.getString("equipment"), agent.getString("morale")));
	        }
	    }
	    
		new Map();
	}
	
	public static ArrayList<AgentController> createDivision(jade.wrapper.AgentContainer cont, String name, String province, String allignment, String manpower, String equipmnent, String experience) throws StaleProxyException{
		ArrayList<AgentController> agents = new ArrayList<AgentController>();
		agents.add(cont.createNewAgent(name,"troops.MainUnit", new String[]{province, allignment}));
		agents.add(cont.createNewAgent(name+"MilUnit","troops.MilUnit", new String[]{province, allignment, manpower, equipmnent, experience}));
		agents.add(cont.createNewAgent(name+"RecoUnit","troops.RecoUnit", new String[]{province, allignment}));
		return agents;
	}
	
	public static String readFile(String filename) {
	    String result = "";
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(filename));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        result = sb.toString();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
}