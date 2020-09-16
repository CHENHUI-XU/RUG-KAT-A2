import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;


// Functions involved in showing the simulation.

public class MainPanel extends JPanel implements Observer {
	
	Dimension preferredSize = new Dimension(651, 651);
	Simulation scape;
	Point selection = null;

	public MainPanel(Simulation controller) {
		this.scape = controller;
		this.scape.addObserver(this);

		setBackground(new Color(255, 250, 205));
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();

		for (int y = 0; y < scape.ySize; ++y) {
			for (int x = 0; x < scape.xSize; ++x) {
				Site site = scape.grid[x][y];

				paintSite(g2, site);

				if (site.getAgent() != null)
					paintAgent(g2, site, site.getAgent());
			}
		}

		paintGrid(g2);	

		if (selection != null)
			paintSelection(g2);

		g2.dispose();
	}

	private void paintSite(Graphics2D g2, Site site) {
		Dimension siteSize = getSiteSize();
		double energy = site.getFood();
		double div = (255 / scape.maxFood) * energy;
		int gradient = (int) (255 - div);
		
		Color background = (gradient > 235)
			? new Color(255, 250, 205)
			: new Color(gradient, 255, gradient);

		g2.setColor(background);
		g2.fillRect(
			siteSize.width * site.getXPosition() + 1,
			siteSize.height * site.getYPosition() + 1,
			siteSize.width - 2,
			siteSize.height - 2);
	}

	private void paintAgent(Graphics2D g2, Site site, Agent agent)
	{
		Dimension siteSize = getSiteSize();

		g2.setColor(Color.RED);
		g2.fillOval(
			siteSize.width * site.getXPosition() + 2,
			siteSize.height * site.getYPosition() + 2,
			siteSize.width - 4,
			siteSize.height - 4);
	}
	
	private void paintGrid(Graphics2D g2) {
		Dimension panelSize = getSize();
		Dimension siteSize = getSiteSize();
		
		g2.setColor(Color.BLACK);
		for (int x = 0; x <= scape.xSize; ++x)
			g2.drawLine(
				x * siteSize.width, 0,
				x * siteSize.width, panelSize.height);

		for (int y = 0; y <= scape.ySize; ++y)
			g2.drawLine(
				0, y * siteSize.height,
				panelSize.width, y * siteSize.height);
	}

	private void paintSelection(Graphics2D g2) {
		Dimension siteSize = getSiteSize();
		g2.setColor(Color.RED);

		g2.setStroke(new BasicStroke(2));
		g2.drawRect(
			selection.x * siteSize.width,
			selection.y * siteSize.height,
			siteSize.width, siteSize.height);
	}

	@Override
	public void update(Observable scape, Object arg) {
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public Dimension getSiteSize() {
		return new Dimension((getSize().width - 1) / scape.xSize, (getSize().height - 1) / scape.ySize);
	}

	public Point getPositionAtPoint(Point position) {
		return new Point(
			position.x / getSiteSize().width,
			position.y / getSiteSize().height);
	}

	public void setSelected(Point position) {
		selection = position;
		repaint();
	}
}
