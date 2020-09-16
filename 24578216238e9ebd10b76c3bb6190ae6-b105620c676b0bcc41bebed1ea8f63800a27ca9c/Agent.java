class Agent
{
	// Like a struct, every agent object has a credits and a reference to the simulation
	
	private String name; // Name of the agent

	private int credits; // Number of credits the agent has

	private Simulation sim; // Simulation the agent lives in

	public Agent(Simulation sim, String name, int credits)
	{
		// here I was stupid enough to name the parameters with the
		// same name as the member variables (credits, name and sim)
		// Thus I have to use this, to make sure that Java knows I mean
		// the instance-specific variables.
		this.sim = sim;
		this.name = name;
		this.credits = credits;
	}

	public String getName()
	{
		return this.name;
	}

	public int getCredits()
	{
		return this.credits;
	}

	public void act()
	{
		// Initialize the variable in which I'll store the poorest agent. But for now, I'll initialize it
		// to 'null', meaning empty. (Very similar to a C pointer pointing to location 0.)
		Agent poorestAgent = null;

		// Find the poorest agent
		// Note that I can access the simulation through this.sim here, and access the list of agents
		// because that is a public member variable (thus accessible and editable by anyone)
		for (Agent agent : sim.agents) {
			// First, lets check if the agent is not me (because I'm one of the agents of the simulation!)
			if (agent != this) {
				// Then, if there is no poorest agent yet, or there is one but the current agent
				// is poorer than the currently poorest agent, save that one as the poorest agent
				if (poorestAgent == null || agent.getCredits() < poorestAgent.getCredits()) {
					poorestAgent = agent;
				}
			}
		}

		// Calculate how much I can give away and already remove it from my own count.
		// Because the receiving agent will ask me for how much I have as soon as I give
		// them credits (see the receiveCredits method).
		int creditsToGive = credits / 2;
		credits = credits - creditsToGive;

		// Now (given that there are more agents than only me in the sim.agents array) we can give the
		// agent some of our credits, and tell them it was ours so they can thank us
		// I pass on 'this' as the contributor, because it was I who gave the other agent credits!
		poorestAgent.receiveCredits(this, creditsToGive);
	}

	public void receiveCredits(Agent contributor, int amountOfCredits)
	{
		// Update our amount of credits
		this.credits += amountOfCredits;

		// Print out a thank you!
		System.out.println(this.getName() + " says:");
		System.out.println(contributor.getName() + " just gave me " + Integer.toString(amountOfCredits) + " credits.");
		System.out.println("Now I have " + Integer.toString(this.credits) + " and they have " + Integer.toString(contributor.getCredits()));

		// If I now have more credits than the contributing agent gave me, thank them especially
		if (credits > contributor.getCredits()) {
			System.out.println("I now have more than my contributor! Thanks!");
		}
	}
}