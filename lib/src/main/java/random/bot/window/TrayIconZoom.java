package random.bot.window;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/TrayIconDemoProject/src/misc/TrayIconDemo.java
 */
public class TrayIconZoom {

	private final ActionListener exitActionListener;
	private final ActionListener callActionListener;
	private final ActionListener displayActionListener;
	private Image icon;

	private TrayIcon trayIcon;
	private SystemTray tray;

	public TrayIconZoom(Image icon, ActionListener callActionListener, ActionListener displayActionListener,
			ActionListener exitActionListener) {
		this.icon = icon;
		this.exitActionListener = exitActionListener;
		this.callActionListener = callActionListener;
		this.displayActionListener = displayActionListener;
		createAndShowGUI();
	}

	public void setTooltip(String tip) {
		trayIcon.setToolTip(tip);
	}

	public void trayLaunch() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
	}

	protected void createAndShowGUI() {
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(icon);
		tray = SystemTray.getSystemTray();

		MenuItem displayItem = new MenuItem("Display");
		MenuItem exitItem = new MenuItem("Exit");

		popup.add(displayItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(callActionListener);
		displayItem.addActionListener(displayActionListener);

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				TrayIconZoom.this.exitActionListener.actionPerformed(e);
			}
		});
	}
}