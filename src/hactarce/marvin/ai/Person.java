package hactarce.marvin.ai;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Person {

	static JDA jda;
	String preferredName;
	User discordUser;
	Map<String, Object> favorites = new HashMap<>();
	int sentiment = 0;
	boolean su = false;
	long lastSeen = System.currentTimeMillis();

	Person(User discordUser) {
		this.discordUser = discordUser;
	}

	public String getEffectiveName() {
		return preferredName == null ? discordUser.getName() : preferredName;
	}

	public String getEffectiveName(Guild guild) {
		Member member = guild.getMember(discordUser);
		if (preferredName == null) return member.getEffectiveName();
		String nick = guild.getMember(discordUser).getNickname();
		return nick == null ? preferredName : nick;
	}

	JSONObject toJSON() {
		return new JSONObject(new HashMap<String, Object>() {{
			if (preferredName != null) put("preferredName", preferredName);
			put("discordID", discordUser.getId());
			put("fav", new JSONObject(favorites));
			put("sentiment", sentiment);
			put("su", su);
			put("lastSeen", lastSeen);
		}});
	}

	static Person fromJSON(JSONObject jsonObject) {
		Person person = new Person(jda.getUserById(jsonObject.getString("discordID")));
		person.preferredName = jsonObject.optString("preferredName");
		JSONObject favorites = jsonObject.getJSONObject("fav");
		for (String key : favorites.keySet()) {
			person.favorites.put(key, favorites.get(key));
		}
		person.sentiment = jsonObject.getInt("sentiment");
		person.su = jsonObject.getBoolean("su");
		person.lastSeen = jsonObject.getLong("lastSeen");
		return person;
	}

}
