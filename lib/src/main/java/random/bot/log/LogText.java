package random.bot.log;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class LogText {
	static SimpleDateFormat sdFormat = new SimpleDateFormat(" [yyyy/MM/dd-HH:mm:ss.SSS] ");
	private final String string;
	private final long time;
	private final boolean isErr;
	private String printString = null;

	public LogText(String string, long time, boolean isErr) {
		this.string = string;
		this.time = time;
		this.isErr = isErr;
	}

	public LogText(String string, boolean isErr) {
		this(string, System.currentTimeMillis(), isErr);
	}

	public String getString() {
		return string;
	}

	public long getTime() {
		return time;
	}

	public boolean isErr() {
		return isErr;
	}

	public String printString() {
		if (printString == null) {
			Date date = new Date(this.time);
			StringBuilder strbuilder = new StringBuilder();
			strbuilder.append((isErr ? "[WARN]" : "[INFO]"));
			strbuilder.append(sdFormat.format(date));
			strbuilder.append(this.string);
			printString = strbuilder.toString();
		}
		return printString;
	}

	public String toString() {
		return this.printString();
	}
}
