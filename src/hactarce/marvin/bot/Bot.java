package hactarce.marvin.bot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public abstract class Bot {

	MessageSender messageSender;

	public Bot(MessageSender messageSender, SpecialSender specialSender) {

	}

	public abstract void receiveMsg(String msg);

	public void sendMsg(String msg) {
		sendMsg(msg);
	}

	public abstract Object receiveSpecial(Object data);

	public Object sendSpecial(Object data) {
//		return specialSender.sendSpecial(data);
		return null;
	}

	public interface MessageSender {
		void sendMsg(String msg);
	}

	public interface SpecialSender {
		Object sendSpecial(Object data);
	}

}
