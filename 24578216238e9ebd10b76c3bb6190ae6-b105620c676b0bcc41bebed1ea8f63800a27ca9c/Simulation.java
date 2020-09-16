import java.util.ArrayList;
import java.util.Random;

class Simulation
{
	public ArrayList<Agent> agents;

	public Simulation()
	{
		// This method is called when a new simulation instance is created

		// Create an empty list where we can put agents in
		agents = new ArrayList<Agent>();

		// But that could also be written as:
		this.agents = new ArrayList<Agent>();
	}

	public void step()
	{	
		// Call Agent.act method on every agent in the agents list
		// Again, you could also write this.agents here.
		for (Agent agent : agents) {
			agent.act();
		}
	}

	public void printAgentCredits()
	{
		for (Agent agent : agents) {
			System.out.println(agent.getName() + ":\t" + Integer.toString(agent.getCredits()));
		}
	}

	static public void main(String[] args)
	{
		// This (static) method is called when you run `java Simulation`
		// static means that it is not

		Random random = new Random(); // Source of random numbers

		Simulation sim = new Simulation();
	
		for (int i = 0; i < 4; ++i) {
			String name = "Agent " + Integer.toString(i + 1);
			int credits = random.nextInt(10); // int between 0 and 10
			Agent agent = new Agent(sim, name, credits);
			sim.agents.add(agent);
		}

		// Print the amount of credits all agents have at the start
		sim.printAgentCredits();
		
		// Simulate 3 epochs
		for (int epoch = 0; epoch < 3; ++epoch) {
			System.out.println("----- Epoch " + Integer.toString(epoch + 1) + " -----");
			sim.step();
		}

		// Print the final results
		sim.printAgentCredits();
	}
}