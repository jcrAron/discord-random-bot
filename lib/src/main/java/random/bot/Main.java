package random.bot;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import random.bot.command.CommandLoader;
import random.bot.config.ConfigLoader;
import random.bot.log.SystemPrintLoader;
import random.bot.window.LogWindow;

public class Main {

	public static void main(String[] args) {
		LogWindow logWindow = new LogWindow();
		SystemPrintLoader.rebootPrint(logWindow::inputStandend, logWindow::inputError);
		CommandLoader.launch();
		ConfigLoader.launch();
		launchDiscord();
	}

	public static void fileLogWrite(String string) {
		SystemPrintLoader.fileWrite(string);
	}

	public static void launchDiscord() {
		JDABuilder builder = JDABuilder.createDefault(ConfigLoader.Token);
		builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_TYPING);
		builder.addEventListeners(new ListenerAdapter() {
			public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
				Main.onMessageReceived(event);
			}
		});
		JDA jda;
		try {
			jda = builder.build();
			jda.awaitReady();
			String uri = jda.getInviteUrl(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE,Permission.MESSAGE_HISTORY);
			System.out.println("BOT LINK: " + uri);
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		CommandLoader.runCommand(event);
	}
}
