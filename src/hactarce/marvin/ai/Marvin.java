package hactarce.marvin.ai;

import hactarce.marvin.bot.DiscordLogger;
import hactarce.marvin.Utils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static hactarce.marvin.ai.MsgGen.MessageType;

public class Marvin extends DiscordLogger {

	private Memory memory;
	private ConversationContext conversation;
	private long lastConversationMsg = Integer.MAX_VALUE;

	@Override
	public void init() {
		super.init();
		memory = new Memory(jda);
		try {
			memory.load();
			memory.master.openPrivateChannel();
			Utils.log("[MARVIN] Successfully identified master as %s", memory.master.getName());
			int peopleCount = memory.people.size();
			if (peopleCount == 0) {
				Utils.log("[MARVIN] Successfully loaded information regarding nobody");
			} else if (peopleCount == 1) {
				Utils.log("[MARVIN] Successfully loaded information regarding 1 person");
			} else Utils.log("[MARVIN] Successfully loaded information regarding %d people", peopleCount);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			try {
				Utils.log("[MARVIN] Failed to load memory banks; resetting to defaults");
				memory.resetAll();
			} catch (IOException e1) {
				e.printStackTrace();
			}
		}
	}

	//region private void notifyMaster(String[] messages) {...}
	private void notifyMaster(Exception e) {
		notifyMaster(new String[]{
				"Master, I've encountered an error:",
				Utils.fmt("```\n%s\n```", ExceptionUtils.getStackTrace(e))
		});
	}

	private void notifyMaster(Exception e, String context) {
		notifyMaster(new String[]{
				Utils.fmt("Master, I've encountered an error while %s:", context),
				Utils.fmt("```\n%s\n```", ExceptionUtils.getStackTrace(e))
		});
	}

	private void notifyMaster(Exception e, String context, String extraInfo) {
		notifyMaster(new String[]{
				Utils.fmt("Master, I've encountered an error while %s:", context),
				Utils.fmt("```\n%s\n\n%s\n```", extraInfo, ExceptionUtils.getStackTrace(e))
		});
	}

	private void notifyMaster(String[] messages) {
		Arrays.stream(messages).forEach(s -> memory.master.getPrivateChannel().sendMessage(s).queue());
	}
	//endregion

	@Override
	public void handle(Event event) {
		try {
			printIncoming(event);
		} catch (Exception e) {
			e.printStackTrace();
			notifyMaster(e, "parsing a recent event", "EVENT CLASS: " + event.getClass().getName());
		}
		if (event instanceof GuildMemberJoinEvent) {
			GuildMemberJoinEvent _event = (GuildMemberJoinEvent) event;
//			if (_event.getMember().getUser() != jda.getSelfUser()) {
			if (!memory.knows(_event.getMember().getUser())) {
				welcome(new ConversationContext(_event.getMember().getUser(), _event.getMember().getEffectiveName(), _event.getGuild().getPublicChannel()));
			}
//			}
		}
		if (event instanceof GuildJoinEvent) {
			GuildJoinEvent _event = (GuildJoinEvent) event;
			_event.getGuild().getTextChannels().get(0).sendMessage("Hello!");
		}
		if (event instanceof MessageReceivedEvent) {
			MessageReceivedEvent _event = (MessageReceivedEvent) event;
			if (_event.getAuthor() != jda.getSelfUser()) {
				if (!memory.knows(_event.getAuthor())) {
					firstGreet(new ConversationContext(_event));
				}
				memory.getPerson(_event.getAuthor()).lastSeen = Utils.now();
				personAction(new ConversationContext(_event));
				saveAllMemory();
			}
		}
	}

	private void saveAllMemory() {
		try {
			memory.save();
		} catch (IOException e) {
			notifyMaster(e, "trying to save my memory banks");
		}
	}

	private void welcome(ConversationContext context) {
		context.channel.sendMessage(MsgGen.generate(MessageType.WELCOME, new HashMap<String, String>() {{
			put("SERVER", context.channel.getGuild().getName());
			put("USER", context.name);
		}})).queue();
		if (!memory.knows(context.person.discordUser)) firstMeet(context);
	}

	private void greet(ConversationContext context) {
		if (memory.knows(context.person.discordUser)) {
			context.channel.sendMessage(MsgGen.generate(MessageType.GREET, context.name)).queue();
		} else firstGreet(context);
	}

	private void firstGreet(ConversationContext context) {
		Utils.log("[MARVIN] Remembering %s...", context.person.discordUser.getName());
		memory.people.add(new Person(context.person.discordUser));
		Utils.log("[MARVIN] Greeting %s for the first time...", context.person.discordUser.getName());
		greet(context);
		context.channel.sendMessage(MsgGen.generate(MessageType.FIRST_MEET)).queue();
	}

	private void firstMeet(ConversationContext context) {
		Utils.log("[MARVIN] Remembering %s...", context.person.discordUser.getName());
		memory.people.add(context.person);
		Utils.log("[MARVIN] Greeting %s for the first time...", context.person.discordUser.getName());
//		greet(user, name, channel);
		context.channel.sendMessage(MsgGen.generate(MessageType.FIRST_MEET)).queue();
	}

	private void personAction(ConversationContext context) {
		if (context.person.lastSeen + Utils.days(3) < Utils.now()) {
			context.sendMessage(MsgGen.join(
					MsgGen.timeOfDayGreeting(context.name),
					MsgGen.generate(MessageType.LONG_TIME_NO_SEE)
			));
		} else if (context.person.lastSeen + 60 * 6 < Utils.now()) {
			context.sendMessage(MsgGen.timeOfDayGreeting(context.name));
		}
		context.person.lastSeen = Utils.now();
	}

	class ConversationContext {
		final Person person;
		final String name;
		final TextChannel channel;

		// name = effectiveName
		ConversationContext(Person person, String name, TextChannel channel) {
			this.person = person;
			this.name = name;
			this.channel = channel;
		}

		ConversationContext(MessageReceivedEvent event) {
			this(memory.getPerson(event.getAuthor()), event.getMember().getEffectiveName(), event.getTextChannel());
		}

		ConversationContext(User user, String name, TextChannel channel) {
			this(memory.getPerson(user), name, channel);
		}

		void sendMessage(String s) {
			channel.sendMessage(s).queue();
		}
	}

		}
	}

}
