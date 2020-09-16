/**
 * @author elske
 * @author alleveenstra
 *
 */

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

public class Simulation extends Observable {

	// Global scape variables
	Site[][] grid;
	int xSize = 50;
	int ySize = 50;
	double minFood = 0;
	double maxFood = 6;
	int epochs = 0;
	int numAgents = 10;
	List<Agent> agents;
	Random gen = new Random();
	double totalFoodCapacity;

	// Layout variables
	Point[] sources = {
		new Point(12, 12),
		new Point(37, 37),
	};

	int spread = 20;

	// Visualization variables
	JFrame frame;
	MainPanel mainPanel;
	ButtonPanel buttonPanel;

	public static void main(String args[]) {
		Simulation sim = new Simulation();
		sim.run();
	}

	public Simulation() {
		reset();
	}

	public void reset()
	{
		epochs = 0;
		initGrid();
		initAgents();
		setChanged();
	}

	public void run() {
		createAndShowGUI();
	}

	// Function involved in showing the simulation.
	private void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Scape");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		//Set up the content pane.
		buildUI(frame.getContentPane());

		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		buttonPanel.forwardEpochs.grabFocus();
	}

	// Function involved in showing the simulation.
	private void buildUI(Container pane) {
		pane.setLayout(new FlowLayout());

		mainPanel = new MainPanel(this);
		pane.add(mainPanel);

		buttonPanel = new ButtonPanel(this);
		pane.add(buttonPanel);

		// Add a mouse listener for when you click on a site
		mainPanel.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point pos = mainPanel.getPositionAtPoint(e.getPoint());
				mainPanel.setSelected(pos);
				buttonPanel.setSite(pos != null ? grid[pos.x][pos.y] : null);
			}
		});
	}

	// Initializing the Scape with Sites, each with its own energyMax according to its graphical location.
	private void initGrid() {
		grid = new Site[xSize][ySize];

		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				double capacity = minFood;
				
				// Find the nearest mountain, which will determine capacity
				for (Point center : sources) {
					int dx = center.x - x;
					int dy = center.y - y;
					double distance = Math.sqrt(dx * dx + dy * dy);
					capacity = Math.max(capacity, maxFood * (1 - distance / spread));
				}
				
				grid[x][y] = new Site(capacity, x, y);
				totalFoodCapacity = totalFoodCapacity + capacity;
			}
		}
	}

	// Iniliatizing the Scape with Agents and assigning them to sites
	private void initAgents() {
		agents = new ArrayList<Agent>();

		for (int a = 0; a < numAgents; a++) {
			Agent agent = new Agent(this);
			agents.add(agent);
		
			int x = 0;
			int y = 0;
			boolean free = false;
			while (!free) {
				x = gen.nextInt(xSize);
				y = gen.nextInt(ySize);
				free = grid[x][y].getAgent() == null;
			}
			
			agent.setPosition(x,y);
			grid[x][y].setAgent(agent);
		}
	}

	// The function step () is called every turn, determining Scape and Agent behavior.
	private void step() {
		// Increase the step counter
		epochs++;

		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				grid[x][y].grow(); // YOU WILL NEED TO IMPLEMENT GROW() YOURSELF.
			}
		}

		Collections.shuffle(agents);

		// Work on a copy of this.agents because agent.act might cause the agent
		// to die and be removed from the agents list. And that, while still
		// iterating over the list, might cause trouble.
		for (Agent agent : new ArrayList<Agent>(agents)) {
			agent.act(); // YOU WILL NEED TO COMPLETE ACT() YOURSELF.
		}

		setChanged();
	}

	public void forward(int steps) {
		for (int c = 0; c < steps; c++) {
			step();
		}

		notifyObservers();
	} 

	// Used by the agents to get sites around them in the simulation
	public List<Site> getSitesAround(int xPosition, int yPosition, int range) {
		List<Site> sites = new ArrayList<Site>();

		int x0 = Math.max(xPosition - range, 0);
		int y0 = Math.max(yPosition - range, 0);
		int x1 = Math.min(xPosition + range + 1, xSize);
		int y1 = Math.min(yPosition + range + 1, ySize);

		for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {
				sites.add(grid[x][y]);
			}
		}

		return sites;
	}
}
