class Site {

	private double foodMax;
	private double food;
	private int xPosition;
	private int yPosition;
	private Agent agent;

	public Site() {
		food = 0;
	}

	public Site(double cap, int x, int y) {
		food = foodMax = cap;
		xPosition = x;
		yPosition = y;
	}

	// Called once every step; determines the growback.
	// Be creative. You can add new parameters, for example.
	public void grow() {
		
	}

	// Use the getFood and setFood methods to let an
	// agent reap this site.
	public double getFood() {
		return food;
	}

	public void setFood(double f) {
		food = f;
	}

	public Agent getAgent() {
		return agent;
	}

	// Use this method to let agents move around.
	public void setAgent(Agent a) {
		agent = a;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}
}