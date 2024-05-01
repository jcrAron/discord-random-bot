package random.bot.log;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import net.jcraron.util.FileIOHelper;

public class SystemPrintLoader {

	public static void rebootPrint(Consumer<String> out, Consumer<String> err) {
		System.setOut(new PrintStream(createOutputStream(System.out, out)));
		System.setErr(new PrintStream(createOutputStream(System.err, err)));
	}

	private static OutputStream createOutputStream(PrintStream printStream, Consumer<String> consumer) {
		OutputStream ret = new FilterOutputStream(System.out) {
			@Override
			public void write(byte[] bytes, int off, int len) throws IOException {
				String str = new String(bytes, off, len);
				consumer.accept(str);
				super.write(bytes, off, len);
			}
		};
		return ret;
	}

	static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");

	public static void fileWrite(String string) {
		File file=new File("log", "randombot-" + sdFormat.format(new Date()) + ".log");
		if (file.exists()) {
			return;
		}
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileIOHelper.write(file, string);
	}
}
