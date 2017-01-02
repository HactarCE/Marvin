package hactarce.marvin.ai;

import hactarce.marvin.Utils;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MsgGen {

	private static final String MESSAGES_FILE = "cheery.json";
	private static final Random rng = new Random();

	private static JSONObject messages = new JSONObject(Utils.getResourceAsString(MESSAGES_FILE));

	private MsgGen() {
	}

	static String join(String... strings) {
		StringBuilder output = new StringBuilder();
		for (String s : strings) {
			output.append(' ');
			output.append(s);
		}
		return output.deleteCharAt(0).toString();
	}

	static String generate(MessageType type) {
		return generate(type, new HashMap<>());
	}

	static String generate(MessageType type, String... params) {
		Map<String, String> paramsMap = new HashMap<>();
		for (int i = 0; i < params.length; i++) {
			paramsMap.put(Integer.toString(i), params[i]);
		}
		return generate(type.name(), paramsMap);
	}

	static String generate(MessageType type, Map<String, String> params) {
		return generate(type.name(), params);
	}

	private static String generate(String type, Map<String, String> params) {
		if (!messages.has(type)) return Utils.fmt("`$%s$`", type);
		List<Object> possibleMessages = messages.getJSONArray(type).toList();
		String message = (String) possibleMessages.get(rng.nextInt(possibleMessages.size()));
		Matcher matcher = Pattern.compile("(\\$([A-Za-z\\d_]+)\\$)").matcher(message);
		while (matcher.find()) {
			message = matcher.replaceFirst(params.containsKey(matcher.group(2))
					? params.get(matcher.group(2))
					: generate(matcher.group(2), params));
		}
		return message;
	}

	enum MessageType {
		FAMILIAR_GREETING,
		FAMILIAR_GREETING_AFTERNOON,
		FAMILIAR_GREETING_EARLY_MORNING,
		FAMILIAR_GREETING_EVENING,
		FAMILIAR_GREETING_MORNING,
		FAMILIAR_GREETING_NIGHT,
		FIRST_MEET,
		GREET,
		LONG_TIME_NO_SEE,
		SWITCH_PERSONALITY,
		WELCOME,
	}

	static String timeOfDayGreeting(String... params) {
		switch (GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
			default:
				return generate(MessageType.FAMILIAR_GREETING);
			case 4:
			case 5:
			case 6:
				return generate(MessageType.FAMILIAR_GREETING_EARLY_MORNING);
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				return generate(MessageType.FAMILIAR_GREETING_MORNING);
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
				return generate(MessageType.FAMILIAR_GREETING_AFTERNOON);
			case 18:
			case 19:
			case 20:
			case 21:
				return generate(MessageType.FAMILIAR_GREETING_EVENING);
			case 22:
			case 23:
			case 0:
			case 1:
			case 2:
			case 3:
				return generate(MessageType.FAMILIAR_GREETING_NIGHT);
		}
	}

}
