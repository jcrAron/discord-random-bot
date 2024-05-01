package random.bot.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.function.Function;

import net.jcraron.util.FileIOHelper;

public class ConfigLoader {
	public static String Token;
	public static boolean SaveLog;
	public final static String TOKEN_KEY = "token";
	public final static String SAVELOG_KEY = "shouldSaveLog";

	public static Properties properties;

	private static boolean shouldWriteFile = false;

	// "config.properties";
	static File configFile = new File("config.txt");

	public static void launch() {
		if (configFile.exists()) {
			if (configFile.isFile()) {
				setProperties(FileIOHelper.readProperties(configFile));
			} else {
				new IOException(configFile.getName() + " is not file").printStackTrace();
			}
		} else if (!configFile.exists()) {
			newFile();
			setProperties(new Properties());
			printRequireSetting(configFile.getName());
		}
		if (shouldWriteFile) {
			write();
		}
	}

	private static void printRequireSetting(String string) {
		new IOException("Require setting " + string).printStackTrace();
	}

	protected static void setProperties(Properties properties) {
		ConfigLoader.properties = properties;
		Token = getProperty(TOKEN_KEY, "");
		SaveLog = getProperty(SAVELOG_KEY, Boolean.FALSE.toString(), Boolean::valueOf);
	}

	protected static void write() {
		try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(configFile),
				Charset.forName("UTF-8"))) {
			properties.store(out, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static <R> R getProperty(String key, String defaultValue, Function<String, R> cost) {
		R ret;
		if (properties.containsKey(key)) {
			ret = cost.apply(properties.getProperty(key));
		} else {
			properties.setProperty(key, defaultValue);
			shouldWriteFile = true;
			ret = cost.apply(defaultValue);
		}
		return ret;
	}

	private static String getProperty(String key, String defaultValue) {
		String ret;
		if (properties.containsKey(key)) {
			ret = properties.getProperty(key);
		} else {
			properties.setProperty(key, defaultValue);
			shouldWriteFile = true;
			ret = defaultValue;
		}
		return ret;
	}

	private static void newFile() {
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
