package hactarce.marvin;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Locale;

public class Utils {

	public static final Locale LOCALE = Locale.US;

	public static String fmt(@NotNull String format, @Nullable Object... args) {
		return String.format(LOCALE, format, args);
	}

	public static void println(@NotNull String format, @Nullable Object... args) {
		System.out.println(fmt(format, args));
	}

	public static void sneakyCrash() {
		Utils.fmt("%s");
	}

}
