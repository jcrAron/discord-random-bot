package random.bot.command;

import net.jcraron.command.CommandReturn;
import net.jcraron.table.ArrayTable;
import net.jcraron.table.Table;

public class DiscordReturn extends CommandReturn {

	public DiscordReturn() {
		super(new ArrayTable());
	}

	protected String printTable(String[] keys, String[] printTitles) {
		Table table = getTable();
		if (keys.length == 0 || table.isEmpty()) {
			return "```#empty```";
		}
		int columnSize = keys.length;
		String[][] printString = new String[table.size() + 1][columnSize];
		int[] width = new int[columnSize];
		printString[0] = printTitles;

		for (int i = 0; i < columnSize; i++) {
			width[i] = getWidth(printTitles[i]);
		}
		for (int listIndex = 0; listIndex < table.size(); listIndex++) {
			for (int i = 0; i < columnSize; i++) {
				String key = keys[i];
				String content = String.valueOf(table.getRow(listIndex).print(key));
				printString[listIndex + 1][i] = content;
				int contentWidth = getWidth(content);
				if (width[i] < contentWidth) {
					width[i] = contentWidth;
				}
			}
		}
		StringBuilder sb = new StringBuilder("```\n");
		for (int printLine = 0; printLine < printString.length; printLine++) {
			for (int i = 0; i < columnSize; i++) {
				String content = printString[printLine][i];
				int spaceWidth = width[i] - getWidth(content) + 1;
				for (int count = 0; count < spaceWidth; count++) {
					sb.append(' ');
				}
				sb.append(content);
				sb.append(" |");
			}
			sb.append('\n');
		}
		sb.append("```");
		return sb.toString();
	}

	int getWidth(String string) {
		int sum = 0;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			sum += isHalfWidth(c) ? 1 : 2;
		}
		return sum;
	}

	boolean isHalfWidth(char c) {
		return '\u0000' <= c && c <= '\u00FF' || '\uFF61' <= c && c <= '\uFFDC' || '\uFFE8' <= c && c <= '\uFFEE';
	}
}
