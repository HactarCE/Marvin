package hactarce.marvin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.util.Properties;

public class BotAuth {

	private BotAuth() {
	}

	private static String discordAuthInfoFile = "discordapi.properties";

	public static String getDiscordApiToken() throws IOException {
		try {
			return getDiscordProps().getProperty("token");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to load Discord authentication info");
			throw e;
		}
	}

	public static User getDiscordMaster(JDA jda) throws IOException {
		return jda.getUserById(getDiscordProps().getProperty("master"));
	}

	private static Properties getDiscordProps() throws IOException {
		Properties properties = new Properties();
		properties.load(BotAuth.class.getClassLoader().getResourceAsStream(discordAuthInfoFile));
		return properties;
	}

}
