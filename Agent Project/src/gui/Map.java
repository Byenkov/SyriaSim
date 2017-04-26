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
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.tools.DummyAgent.DummyAgent;
import troops.DivisionInfo;

public class Map extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public Map() {

		setSize(new Dimension(720, 560));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setResizable(false);
		class Panel extends JPanel{
			private Country syria;
//			private Timer timer=new Timer(1000, this);
			
			public Panel(Country syria){
				this.syria = syria;
//				timer.start();
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
		Country syria = new Country(150.0, 150.0);
		Panel panel = new Panel(syria);
		setTitle("Map");
		this.getContentPane().add(panel);
		setVisible(true);
	}
	
	public static void main(String arg[]) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new Map();
				}
			});
	}
}