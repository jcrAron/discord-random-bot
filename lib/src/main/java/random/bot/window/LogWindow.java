package random.bot.window;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import random.bot.Main;
import random.bot.config.ConfigLoader;
import random.bot.log.LogText;

public class LogWindow extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9121673356608176196L;
	private ScheduledExecutorService executor;
	protected LinkedBlockingQueue<LogText> queue;

	private TextArea jTextComponent;
	private JScrollPane scrollPane;
	private TrayIconZoom trayIconZoom;
	private Image icon;

	public LogWindow() {
		super("Discord random bot");
		icon = createImage("/icon.png");
		this.setMinimumSize(new Dimension(800, 600));
		JFrame.setDefaultLookAndFeelDecorated(true);
		createTrayIconZoom();
		setWindowCloseListener();
		initDisplayList();
		this.setIconImage(this.icon);
		this.setVisible(true);
		queue = new LinkedBlockingQueue<>();
		executor = Executors.newScheduledThreadPool(2);
		executor.scheduleAtFixedRate(this::updateRunnable, 53, 53, TimeUnit.MILLISECONDS);
		executor.scheduleAtFixedRate(this::repaint, 73, 73, TimeUnit.MILLISECONDS);
	}

	protected void createTrayIconZoom() {
		ActionListener displayListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogWindow.this.setVisible(true);
			}
		};
		ActionListener exitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LogWindow.this.dispose();
			}
		};

		trayIconZoom = new TrayIconZoom(this.icon, displayListener, displayListener, exitListener);
		trayIconZoom.setTooltip(this.getTitle());
		trayIconZoom.trayLaunch();
	}

	protected void setWindowCloseListener() {
		WindowAdapter windowListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				String[] options = new String[] { "exit", "hide", "cancel" };
				int button = JOptionPane.showOptionDialog(LogWindow.this, "Exit?", "Exit",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
				switch (button) {
				case 0:
					LogWindow.this.dispose();
					break;
				case 1:
					LogWindow.this.setVisible(false);
					break;
				case 2:
					break;
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				if (ConfigLoader.SaveLog) {
					Main.fileLogWrite(jTextComponent.getText().toString());
				}
				executor.shutdown();
				System.exit(0);
			}
		};
		this.addWindowListener(windowListener);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	protected void initDisplayList() {
		jTextComponent = new TextArea();
		jTextComponent.setEditable(false);
		scrollPane = new JScrollPane(jTextComponent);
		scrollPane.getHorizontalScrollBar().setEnabled(true);
		scrollPane.setAutoscrolls(true);
		this.add(scrollPane);
	}

	protected void updateRunnable() {
		if (!queue.isEmpty()) {
			LogText text = queue.poll();
			jTextComponent.append(text.toString());
		}
	}

	public void inputStandend(String string) {
		queue.add(new LogText(string, false));
	}

	public void inputError(String string) {
		queue.add(new LogText(string, true));
	}

	public static Image createImage(String path) {
		URL imageURL = Main.class.getResource(path);
		return Toolkit.getDefaultToolkit().getImage(imageURL).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
	}
}
