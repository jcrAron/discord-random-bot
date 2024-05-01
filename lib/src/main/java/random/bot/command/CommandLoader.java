package random.bot.command;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.jcraron.command.Command;
import net.jcraron.command.CommandReturn;
import net.jcraron.command.split.CommandParser;
import net.jcraron.command.split.CommandParser.CommandEntry;
import net.jcraron.exception.InterruptException;
import net.jcraron.exception.ParseFormatException;

public class CommandLoader {

	public final static String RAW_START = "!randombot ";
	public final static String BOT_NAME = "randombot";

	private final static String SENDMESSAGE_FORMAT = "[Send Message] %s: ";
	private final static String BOT_SENDMESSAGE = SENDMESSAGE_FORMAT.formatted(BOT_NAME);
	private final static HashMap<String, Command> REGISTRY = new HashMap<>();

	public static Command register(Command command) {
		REGISTRY.put(command.getName(), command);
		return command;
	}

	public static void runCommand(MessageReceivedEvent event) {
		Message message = event.getMessage();
		String contentRaw = message.getContentRaw().trim();
		if (!contentRaw.startsWith(RAW_START)) {
			return;
		}

		String userName = event.getAuthor().getName();
		System.out.println(SENDMESSAGE_FORMAT.formatted(userName) + contentRaw);

		String except_start = contentRaw.substring(RAW_START.length());
		String sendMessage = null;
		List<CommandEntry> commandList = null;
		try {
			commandList = new CommandParser(except_start, REGISTRY).unbox();
			CommandReturn returns = new DiscordReturn();
			for (CommandEntry entry : commandList) {
				entry.command().run(entry.arg(), returns);
			}
			sendMessage = returns.getMessage();
		} catch (InterruptException | ParseFormatException exception) {
			sendMessage = exception.getClass().getSimpleName() + ": " + exception.getMessage();
			exception.printStackTrace();
		} catch (Exception other) {
			sendMessage = "#unknown command";
			other.printStackTrace();
		}

		if (sendMessage != null && !sendMessage.isEmpty()) {
			event.getChannel().sendMessage(sendMessage).queue();
			System.out.println(BOT_SENDMESSAGE + sendMessage);
		}
	}

	public static void launch() {
		new CommandInstances();
	}
}
