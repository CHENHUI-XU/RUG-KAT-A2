import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ButtonPanel extends JPanel implements ActionListener, Observer {
	private static final long serialVersionUID = 4429598756080187232L;

	Dimension preferredSize = new Dimension(250, 650);
	Simulation scape;
	JTextPane info;
	JPanel stepControls, jumpControls, appControls, body;
	JLabel epochsLabel, forwardLabel;
	JTextField forwardEpochs;
	JButton next, forward, play, restart, exit;

	ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
	ScheduledFuture<?> updateLoop;

	Site selected;  // Selected site on which info will be displayed

	String content[] = {
		"Scape",
		"Agents: ",
		"\nSite",
		"Coordinates: ",
		"Site food: ",
		"\nAgent on Site",
		"Agent ID: ",
		"Age: ",
		"Agent Energy: ",
	};
	
	String style[] = {
		"bold",
		"regular",
		"bold",
		"regular",
		"regular",
		"bold",
		"regular",
		"regular",
		"regular",
		"regular",
		"regular",
	};

	public ButtonPanel(Simulation controller) {
		scape = controller;
		scape.addObserver(this);

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		info = new JTextPane();
		info.setPreferredSize(new Dimension(270, 300));
		info.setMaximumSize(new Dimension(270, 300));
		info.setEditable(false);
		info.setOpaque(false);
		info.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(0, 0, 20, 0),
						BorderFactory.createCompoundBorder(
								BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
								BorderFactory.createEmptyBorder(5, 5, 5, 5))));
		StyledDocument doc = info.getStyledDocument();
		addStylesToDocument(doc);
		updateInfo();

		epochsLabel = new JLabel("", SwingConstants.CENTER);
		String ep = "Epochs: " + scape.epochs;
		epochsLabel.setText(ep);

		stepControls = new JPanel();
		stepControls.setLayout(new FlowLayout());
		stepControls.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		next = new JButton("Step");
		next.setActionCommand("next");
		next.addActionListener(this);

		play = new JButton("Play");
		play.setActionCommand("play");
		play.addActionListener(this);

		stepControls.add(next);
		stepControls.add(Box.createRigidArea(new Dimension(1, 0)));
		stepControls.add(play);

		forwardLabel = new JLabel("Enter the number of epochs to forward.", SwingConstants.LEFT);
		forwardLabel.setVerticalAlignment(SwingConstants.BOTTOM);

		jumpControls = new JPanel();
		jumpControls.setLayout(new BoxLayout(jumpControls, BoxLayout.LINE_AXIS));

		forwardEpochs = new JTextField("100");
		forwardEpochs.setMaximumSize(new Dimension(100, 25));

		forward = new JButton("Forward");
		forward.setActionCommand("forward");
		forward.addActionListener(this);

		jumpControls.add(forwardEpochs);
		jumpControls.add(Box.createRigidArea(new Dimension(5, 0)));
		jumpControls.add(forward);

		appControls = new JPanel();
		appControls.setLayout(new BoxLayout(appControls, BoxLayout.LINE_AXIS));
		appControls.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		restart = new JButton("Restart");
		restart.setActionCommand("restart");
		restart.addActionListener(this);

		exit = new JButton("Exit");
		exit.setActionCommand("exit");
		exit.addActionListener(this);

		appControls.add(Box.createHorizontalGlue());
		appControls.add(restart);
		appControls.add(Box.createRigidArea(new Dimension(5, 0)));
		appControls.add(exit);

		body = new JPanel();
		body.setLayout(new GridLayout(0, 1));

		body.add(epochsLabel);
		body.add(stepControls);
		body.add(forwardLabel);
		body.add(jumpControls);

		this.add(info, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
		this.add(appControls, BorderLayout.SOUTH);
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public void setSite(Site selected) {
		this.selected = selected;
		updateInfo();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "next":
				stop();
				scape.forward(1);
				break;

			case "forward":
				stop();
				scape.forward(Integer.parseInt(forwardEpochs.getText()));
				break;

			case "restart":
				stop();
				scape.reset();
				scape.forward(0); // triggers update of the screen
				break;

			case "exit":
				System.exit(0);
				break;

			case "play":
				play();
				break;

			case "stop":
				stop();
				break;
		}
	}

	private void play() {
		if (isPlaying())
			return;

		// Execute update(1) every 100ms starting now.
		updateLoop = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				scape.forward(1);
			}
		}, 0, 100, TimeUnit.MILLISECONDS);

		play.setActionCommand("stop");
		play.setText("Stop");
	}

	private void stop() {
		if (!isPlaying())
			return;

		updateLoop.cancel(false);
		play.setActionCommand("play");
		play.setText("Play");
	}

	private boolean isPlaying() {
		return updateLoop != null && !updateLoop.isCancelled();
	}

	@Override
	public void update(Observable obs, Object arg) {
		epochsLabel.setText("Epochs:  " + scape.epochs);
		updateInfo();
	}

	private void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");
		StyleConstants.setFontSize(regular, 14);

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);
	}

	private void updateInfo() {
		StyledDocument doc = info.getStyledDocument();

		content[0] = "Scape";
		content[1] = "Agents: " + scape.agents.size();
		content[2] = "\nSite";
		if (selected != null) {
			content[3] = "Coordinates: (" + selected.getXPosition() + ", " + selected.getYPosition() + ")";
			content[4] = "Site food: " + round(selected.getFood());
		} else {
			content[3] = "Coordinates: ";
			content[4] = "Site food: ";
		}

		content[5] = "\nAgent on Site";

		if (selected != null && selected.getAgent() != null) {
			content[6] = "ID: " + selected.getAgent();
			content[7] = "Age: " + selected.getAgent().getAge();
			content[8] = "Agent Energy: " + round(selected.getAgent().getEnergy());
		}
		else {
			content[6] = "ID: ";
			content[7] = "Age: ";
			content[8] = "Agent Energy: ";
		}

		try {
			doc.remove(0, doc.getLength());
			for (int i = 0; i < content.length; i++) {
				doc.insertString(doc.getLength(), content[i] + "\n", doc.getStyle(style[i]));
			}
		}
		catch (BadLocationException ble) {
			System.err.println("Couldn't insert text into text pane.");
		}
	}

	static private String round(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(value);
	}
}
