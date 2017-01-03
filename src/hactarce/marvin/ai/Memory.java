package hactarce.marvin.ai;

import hactarce.marvin.bot.BotAuth;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class Memory {

	private static final String MEMORY_FILE = "memory.json";

	private JDA jda;
	User master;
	Set<Person> people;

	Memory(JDA jda) {
		this.jda = jda;
		Person.jda = jda;
	}

	void load() throws IOException {
		JSONObject allData = new JSONObject(new String(Files.readAllBytes(Paths.get(MEMORY_FILE))));
		master = jda.getUserById(allData.getString("master"));
		Set<Person> newPersonSet = new HashSet<>();
		for (Object obj : allData.getJSONArray("knownPeople")) {
			newPersonSet.add(Person.fromJSON((JSONObject) obj));
		}
		// By using newPersonSet, people will not be modified unless no error was thrown by this point
		people = newPersonSet;
		getPerson(master).su = true;
	}

	void save() throws IOException {
		FileWriter writer = new FileWriter(MEMORY_FILE, false);
		writer.write(new JSONObject(
				new HashMap<String, Object>() {{
					put("master", master.getId());
					put("knownPeople", new JSONArray(people.stream().map(Person::toJSON).toArray()));
				}}
		).toString(2));
		writer.close();
	}

	void resetAll() throws IOException {
		master = BotAuth.getDiscordMaster(jda);
		people = new HashSet<>();
		save();
	}

	boolean knows(User user) {
		return people.stream().anyMatch(person -> person.discordUser == user);
	}

	Person getPerson(User user) {
		return people.stream().filter(person -> person.discordUser == user).findFirst().orElse(new Person(user));
	}

}
