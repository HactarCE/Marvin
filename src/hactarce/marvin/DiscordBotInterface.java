package hactarce.marvin;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserGameUpdateEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.InterfacedEventManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class DiscordBotInterface extends InterfacedEventManager implements BotInterface {

	@Override
	public String getStringID() {
		return "DISCORD";
	}

	private static JDA jda;
	private User master;

	@Override
	public void init() {
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken(BotAuth.getDiscordApiToken())
					.setEventManager(this)
					.buildBlocking();
			master = BotAuth.getDiscordMaster(jda);
			master.openPrivateChannel();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (RateLimitedException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(Event event) {
		try {
			printIncoming(event);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("meow");
			master.getPrivateChannel().sendMessage("Master, I've encountered a message while parsing a recent event.").queue();
			master.getPrivateChannel().sendMessage(
					Utils.fmt("```\nEVENT CLASS: %s\n\n%s\n```", event.getClass().getName(), ExceptionUtils.getStackTrace(e))
			).queue();
		}
	}

	private void printIncoming(Event event) {
		if (event instanceof DisconnectEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Disconnected");
		}
		if (event instanceof ReadyEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Ready");
		}
		if (event instanceof ReconnectedEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Reconnected");
		}
		if (event instanceof ResumedEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Resumed (?)");
		}
		if (event instanceof ShutdownEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Shutdown (?)");
		}
		if (event instanceof StatusChangeEvent) {
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(), "Status changed (?)");
		}
		//region CHANNEL
		//region PRIV
		if (event instanceof PrivateChannelCreateEvent) {
			PrivateChannelCreateEvent _event = (PrivateChannelCreateEvent) event;
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(),
					Utils.fmt("%s has opened a private chat with me", _event.getUser().getName())
			);
		}
		if (event instanceof PrivateChannelDeleteEvent) {
			PrivateChannelDeleteEvent _event = (PrivateChannelDeleteEvent) event;
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(),
					Utils.fmt("%s has closed their private chat", _event.getUser())
			);
		}
		//endregion
		//region TEXT
		//region UPDATE
		if (event instanceof TextChannelUpdateNameEvent) {
			TextChannelUpdateNameEvent _event = (TextChannelUpdateNameEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("The text channel #%s has been renamed to #%s", _event.getOldName(), _event.getChannel().getName())
			);
		}
		if (event instanceof TextChannelUpdateTopicEvent) {
			TextChannelUpdateTopicEvent _event = (TextChannelUpdateTopicEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_CHANNEL_MSG, getStringID(), _event.getGuild().getName(), '#' + _event.getChannel().getName(),
					Utils.fmt("The topic has been changed to %s", _event.getChannel().getTopic())
			);
		}
		//endregion
		if (event instanceof TextChannelCreateEvent) {
			TextChannelCreateEvent _event = (TextChannelCreateEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), '#' + _event.getGuild().getName(),
					Utils.fmt("A new text channel #%s has been created", _event.getChannel().getName())
			);
		}
		if (event instanceof TextChannelDeleteEvent) {
			TextChannelDeleteEvent _event = (TextChannelDeleteEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), '#' + _event.getGuild().getName(),
					Utils.fmt("The text channel #%s has been deleted", _event.getChannel().getName())
			);
		}
		//endregion
		//region VOICE
		//region UPDATE
		if (event instanceof VoiceChannelUpdateNameEvent) {
			VoiceChannelUpdateNameEvent _event = (VoiceChannelUpdateNameEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("The voice channel %s has been renamed to %s", _event.getOldName(), _event.getChannel().getName())
			);
		}
		//endregion
		if (event instanceof VoiceChannelCreateEvent) {
			VoiceChannelCreateEvent _event = (VoiceChannelCreateEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("A new voice channel %s has been created", _event.getChannel().getName())
			);
		}
		if (event instanceof VoiceChannelDeleteEvent) {
			VoiceChannelDeleteEvent _event = (VoiceChannelDeleteEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("The voice channel %s has been deleted", _event.getChannel().getName())
			);
		}
		//endregion
		//endregion
		//region GUILD
		//region MEMBER
		if (event instanceof GuildMemberJoinEvent) {
			GuildMemberJoinEvent _event = (GuildMemberJoinEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("%s joined the server", _event.getMember().getEffectiveName())
			);
		}
		if (event instanceof GuildMemberLeaveEvent) {
			GuildMemberLeaveEvent _event = (GuildMemberLeaveEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("%s left the server", _event.getMember().getEffectiveName())
			);
		}
		if (event instanceof GuildMemberNickChangeEvent) {
			GuildMemberNickChangeEvent _event = (GuildMemberNickChangeEvent) event;
			if (_event.getPrevNick() != null) {
				if (_event.getNewNick() != null) {
					Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
							Utils.fmt("%s has had their nickname changed from %s to %s", _event.getMember().getUser().getName(), _event.getPrevNick(), _event.getNewNick())
					);
				} else {
					Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
							Utils.fmt("%s no longer has the nickname %s", _event.getMember().getUser().getName(), _event.getPrevNick())
					);
				}
			} else if (_event.getNewNick() != null) {
				Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
						Utils.fmt("%s now has the nickname %s", _event.getMember().getUser().getName(), _event.getNewNick())
				);
			}
		}
		//endregion
		//region UPDATE
		if (event instanceof GuildUpdateNameEvent) {
			GuildUpdateNameEvent _event = (GuildUpdateNameEvent) event;
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(),
					Utils.fmt("The server %s has been renamed to %s", _event.getOldName(), _event.getGuild().getName())
			);
		}
		//endregion
		//region VOICE
		if (event instanceof GuildVoiceJoinEvent) {
			GuildVoiceJoinEvent _event = (GuildVoiceJoinEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_CHANNEL_MSG, getStringID(), _event.getGuild().getName(), _event.getChannelJoined().getName(),
					Utils.fmt("%s has joined voice chat", _event.getMember().getEffectiveName())
			);
		}
		if (event instanceof GuildVoiceLeaveEvent) {
			GuildVoiceLeaveEvent _event = (GuildVoiceLeaveEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_CHANNEL_MSG, getStringID(), _event.getGuild().getName(), _event.getChannelLeft().getName(),
					Utils.fmt("%s has left voice chat", _event.getMember().getEffectiveName())
			);
		}
		if (event instanceof GuildVoiceLeaveEvent) {
			GuildVoiceLeaveEvent _event = (GuildVoiceLeaveEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_CHANNEL_MSG, getStringID(), _event.getGuild().getName(), _event.getChannelLeft().getName(),
					Utils.fmt("%s has moved to %s", _event.getMember().getEffectiveName(), _event.getVoiceState().getChannel().getName())
			);
		}
		//endregion
		if (event instanceof GuildBanEvent) {
			GuildBanEvent _event = (GuildBanEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("%s has been BANNED", _event.getUser().getName())
			);
		}
		if (event instanceof GuildJoinEvent) {
			GuildJoinEvent _event = (GuildJoinEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(), "I have joined the server");
		}
		if (event instanceof GuildLeaveEvent) {
			GuildLeaveEvent _event = (GuildLeaveEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(), "I have left the server");
		}
		if (event instanceof GuildUnbanEvent) {
			GuildUnbanEvent _event = (GuildUnbanEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_MSG, getStringID(), _event.getGuild().getName(),
					Utils.fmt("%s has been unbanned", _event.getUser().getName())
			);
		}
		//endregion
		//region MESSAGE
		//region GUILD
		if (event instanceof GuildMessageReceivedEvent) {
			GuildMessageReceivedEvent _event = (GuildMessageReceivedEvent) event;
			Utils.println(MESSAGE_FORMAT_SERVER_CHANNEL_USER_MSG, getStringID(),
					_event.getGuild().getName(),
					'#' + _event.getChannel().getName(),
					_event.getMember().getEffectiveName(),
					_event.getMessage().getStrippedContent()
			);
		}
		//endregion
		//region PRIV
		if (event instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent _event = (PrivateMessageReceivedEvent) event;
			Utils.println(MESSAGE_FORMAT_PM_USER_MSG, getStringID(), _event.getChannel().getUser().getName(), _event.getAuthor().getName(), _event.getMessage().getStrippedContent());
		}
		//endregion
		//endregion
		//region USER
		if (event instanceof UserGameUpdateEvent) {
			UserGameUpdateEvent _event = (UserGameUpdateEvent) event;
			Utils.println(MESSAGE_FORMAT_MSG, getStringID(),
					Utils.fmt("%s is now playing %s", _event.getUser().getName(), _event.getGuild().getMember(_event.getUser()).getGame().getName())
			);
		}
		//endregion
	}

}
