package hactarce.marvin;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Utils {

	private Utils() {
	}

	public static final Locale LOCALE = Locale.US;

	public static String fmt(@NotNull String format, @Nullable Object... args) {
		return String.format(LOCALE, format, args);
	}

	public static void log(@NotNull String format, @Nullable Object... args) {
		System.out.println(new SimpleDateFormat("[HH:mm:ss] ").format(new Date()) + fmt(format, args));
	}

	public static void sneakyCrash() {
		Utils.fmt("%s");
	}

	public static InputStream getResourceAsStream(String name) {
		return Utils.class.getClassLoader().getResourceAsStream(name);
	}

	public static String getResourceAsString(String name) {
		Scanner s = new java.util.Scanner(getResourceAsStream(name)).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	// Removes unnecessary whitespace
	public static String sanitize(String s) {
		return Pattern.compile("\\s+").matcher(s.trim()).replaceAll(" ");
	}

	//region Millisecond conversion
	public static long now() {
		return System.currentTimeMillis();
	}

	public static long seconds(int s) {
		return 1000 * s;
	}

	public static long minutes(int m) {
		return 1000 * 60 *  m;
	}

	public static long hours(int h) {
		return 1000 * 60 * 60 * h;
	}

	public static long days(int d) {
		return 1000 * 60 * 60 * 24 * d;
	}
	//endregion

}
