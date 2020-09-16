import java.util.*;

class Agent {

	// Agent attributes
	private int xPosition;
	private int yPosition;
	private int age;
	private double energy;

	// Scape properties
	public Simulation sim;

	// Agent variables
	// These values should be used for answering the questions (unless they ask
	// to vary the values). Feel free to experiment though.
	double moveCost = 0.40;
	int vision = 4;
	int metabolism = 4;
	int procreateReq = 16;
	int procreateCost = 10;

	// Agent constructor
	public Agent (Simulation controller) {
		sim = controller;
		age = 0;
		energy = 6;
	}

	public void act() {
		// This function handles all of an Agents' behavior in
		// any given step.
		
		// Let the Agent know it has survived another step.
		age++;

		// 'Cost of living': Subtracting the metabolism from the
		// Agents' energy level.
		energy -= metabolism;

		// Check if the Agent has enough energy to survive the step,
		// otherwise remove it and stop acting. Dead agents don't act.
		if (energy < 0) {
			remove();
			return;
		}
		
		// Generating a list with the Sites that can be moved to,
		// and a list with Sites suitable for offspring..
		List<Site> freeSites = findFreeSites();

		// Evaluating each of the possible Sites to move to.
		// YOU WILL NEED TO IMPLEMENT FINDBESTSITE YOURSELF.
		Site bestSite = findBestSite(freeSites);

		// Moving to the best possible Site, and reaping the energy
		// from it.
		// YOU WILL NEED TO IMPLEMENT MOVE AND REAP YOURSELF.
		move(bestSite);
		reap(bestSite);

		// Checking if the Agent is fertile and has a free neighboring
		// Site, and if so, producing offspring.
		if (energy > procreateReq) {
			List<Site> babySites = findBabySites();
			if (!babySites.isEmpty()) {
				procreate(babySites);
			}
		}
	}

	// Below is a suggestion of functions you need to finish this code.

	// Returns the best Site to reap energy from.
	public Site findBestSite(List<Site> freeSites) {
		// Your own code determining what the best Site is of all
		// possible freeSites for the agent to move to;

		//This should also include max distance the agent can travel before starving
		//And cost over profit
		
		
		// Then return the best Site.
		return freeSites.get(0);
	}

	// Move to the new site. (Tell the old Site the agent has left,
	// the new Site the agent has arrived, and the agent itself its
	// new location...). Make use of the moveCost variable as well.
	public void move(Site newSite) {
		
		//First set the agent to null on current position so we can forget it
		sim.grid[xPosition][yPosition].setAgent(null);
		newSite.setAgent(this);
		
		//Calculate distance traveled
		double dist = calculateDistance(newSite);
		
		//Set new Position
		xPosition = newSite.getXPosition();
		yPosition = newSite.getYPosition();
		
		//subtract moveCost
		energy -= dist * moveCost;
		
	}

	// Gather food from the site.
	public void reap(Site s) {
		
	}

	// Below are functions we already created for you. You can change
	// them if you must, as long as you turn in a working version.

	// Allows an agent to procreate.
	public void procreate(List<Site> babySites) {
		energy -= procreateCost;
		Agent baby = new Agent(sim);
		sim.agents.add(baby);

		Site babySite = babySites.get(0);
		baby.setPosition(babySite.getXPosition(), babySite.getYPosition());
		babySite.setAgent(baby);
	}

	// When an Agent dies, remove it from the Agent Vector, and remove
	// it from the Site it was on.
	public void remove() {
		sim.agents.remove(this);
		sim.grid[xPosition][yPosition].setAgent(null);
	}

	// Find sites around the agent in a square of -range to +range
	protected List<Site> findSitesInRange(int range) {
		return sim.getSitesAround(xPosition, yPosition, range);
	}

	// Generating a Vector with all available free Sites within vision.
	public List<Site> findFreeSites() {
		List<Site> freeSites = new ArrayList<Site>();

		for (Site site : findSitesInRange(vision)) {
			Agent occ = site.getAgent();
			if (occ == null || occ == this) {
				freeSites.add(site);
			}
		}

		Collections.shuffle(freeSites);
		return freeSites;
	}

	// Generating a Vector with all Sites available for offspring.
	public List<Site> findBabySites() {
		List<Site> babySites = new ArrayList<Site>();

		for (Site site : findSitesInRange(1)) {
			if (site.getAgent() == null) {
				babySites.add(site);
			}
		}

		Collections.shuffle(babySites);
		return babySites;
	}

	// A utility function: Calculating the distance from the agents'
	// current position to a specified (x, y) location.
	public double calculateDistance(Site site) {
		int dx = site.getXPosition() - xPosition;
		int dy = site.getYPosition() - yPosition;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public void setPosition(int x, int y) {
		xPosition = x;
		yPosition = y;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition()	{
		return yPosition;
	}

	public double getEnergy() {
		return energy;
	}

	public int getAge() {
		return age;
	}
}