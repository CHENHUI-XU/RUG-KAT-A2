import java.util.*;

class Agent {

	// Agent attributes
	private int xPosition;
	private int yPosition;
	private int age;
	private double energy;
	// Variable that is used to convert food into energy for agents after reaped
	private double conversionRate = 1.8;
	private boolean migrating = false;
	
	// These variables store the agents previous position, this is used in migration
	private int prevX = 0;
	private int prevY = 0;

	// Scape properties
	public Simulation sim;

	// Agent variables
	// These values should be used for answering the questions (unless they ask
	// to vary the values). Feel free to experiment though.
	double moveCost = 0.40;
	int vision = 4;
	int metabolism = 4; // 4
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
		
		// To check if the agent should migrate, this can be commented out to disable migration
		boolean migrating = StartMigration();
		
		// Generating a list with the Sites that can be moved to,
		// and a list with Sites suitable for offspring..
		List<Site> freeSites = findFreeSites();

		// Evaluating each of the possible Sites to move to.
		// YOU WILL NEED TO IMPLEMENT FINDBESTSITE YOURSELF.
		Site bestSite = findBestSite(freeSites);
		
		// If the agent is migrating it should find the best site for migration
		if (migrating) {
			Site migrationSite = findMigrationSite(freeSites);
			if (migrationSite != null)
				bestSite = migrationSite;
		}

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
		
		//keep track of best possible energy for reaching the site
		double  maxEnergy = 0;
		//current energy for selected site
		double currentEnergy = 0;
		//keep track of selected best site
		Site bestSite = freeSites.get(0);
		
		//iterate through the freeSites list and find the best one with maximal 
		//energy and without starving
		//This should also include max distance the agent can travel before starving
				//And cost over profit
		for(Site site : freeSites) {
			double dist = this.calculateDistance(site);
			if(dist*moveCost <= this.energy) {
				//Suppose current site is chosen, calculate the energy that 
				//will be left once move there
				currentEnergy = this.energy - dist*moveCost - this.metabolism 
						+ site.getFood()* this.conversionRate;
				if(currentEnergy > maxEnergy) {
					maxEnergy = currentEnergy;
					bestSite = site;
				}
			}
		}
		
		// Then return the best Site.
		return bestSite;
	}
	
	
	// Function to find the site most likely to result in migration to the other mountain
	public Site findMigrationSite(List<Site> freeSites)
	{
		List<Agent> agentList = getAgentsInVision();
		
		Site m_site = null;
		
		// First see if the agent is close to the other agents, if this is the case - find a site as far away as possible
		if (agentList.size() >= 5)
		{
			//First it calculates the average distance of the agents near itself
			int averageX = 0;
			int averageY = 0;
			for(Agent agent : agentList)
			{
				averageX += agent.getXPosition();
				averageY += agent.getYPosition();
			}
			averageX = averageX / agentList.size();
			averageY = averageY / agentList.size();
			
			// It should then move to the tile furthest away, this should in theory be away from the mountain
			// If the agent is near the border
			
			double maxDistance = 0;
			for(Site site : freeSites) {
				int siteX = site.getXPosition();
				int siteY = site.getYPosition();
				double dx = siteX - averageX;
				double dy = siteY - averageY;
				double dist = Math.sqrt(dx * dx + dy * dy);
				
				if (dist > maxDistance)
				{
					m_site = site;
					maxDistance = dist;
				}
			}
		} else {
			// If the agent is not near that many other agents it should keep moving in the same direction
			// This is achieved by moving to the available tile that is furthest from its previous position
			
			double maxDist = 0;
			for (Site site : freeSites)
			{
				double dx = prevX - site.getXPosition();
				double dy = prevY - site.getYPosition();
				double dist = Math.sqrt(dx * dx + dy * dy);
				if (dist > maxDist)
				{
					m_site = site;
					maxDist = dist;
				}
			}
		}
		
		return m_site;
	}

	// Move to the new site. (Tell the old Site the agent has left,
	// the new Site the agent has arrived, and the agent itself its
	// new location...). Make use of the moveCost variable as well.
	public void move(Site newSite) {
		
		//First set the agent to null on current position so we can forget it
		sim.grid[this.xPosition][this.yPosition].setAgent(null);
		newSite.setAgent(this);
		
		//Calculate distance traveled
		double dist = calculateDistance(newSite);
		
		//Set previous position (used for migration)
		
		prevX = xPosition;
		prevY = yPosition;
		
		//Set new Position
		this.xPosition = newSite.getXPosition();
		this.yPosition = newSite.getYPosition();
		
		//subtract moveCost
		this.energy -= dist * moveCost;
		
	}

	// Gather food from the site.
	public void reap(Site s) {
		// adding the energy of current site, conversionRate is used to convert food 
		//into energy
		this.energy = this.energy + conversionRate * s.getFood();
		
		//set the food to 0 after reaped
		s.setFood(0);
	}
	
	
	// Function to see whether or not an agent has the possibility to migrate
	public boolean StartMigration()
	{
		
		// Check if the agent has energy to get somewhere, and limited to enable procreation
		if ((energy >= 2*metabolism + vision*moveCost) & (energy < procreateReq))
		{
			return true;
		} 
		//If the agent is already migrating it should keep going if possible
		else if (migrating & (energy >= metabolism))
		{
			return true;
		}
		
		return false;
	}
	
	// Get a list of all the agents in this agents vision (used for migration)
	public List<Agent> getAgentsInVision()
	{
		List<Agent> agentList = new ArrayList<Agent>();
		
		// The vision is divided by half since there seemed to be a lot of agents in its vision
		for (Site site : findSitesInRange(vision/2)) {
			Agent ag = site.getAgent();
			if (ag != null) {
				agentList.add(ag);
			}
		}
		
		return agentList;
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