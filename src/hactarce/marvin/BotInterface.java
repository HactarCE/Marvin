package hactarce.marvin;

public interface BotInterface {

	String getStringID();

	String MESSAGE_FORMAT_MSG = "[%s] %s";
	String MESSAGE_FORMAT_PM_USER_MSG = "[%s][PM][%s][%s] %s";
	String MESSAGE_FORMAT_SERVER_MSG = "[%s][%s] %s";
	String MESSAGE_FORMAT_SERVER_USER_MSG = "[%s][%s][%s] %s";
	String MESSAGE_FORMAT_SERVER_CHANNEL_MSG = "[%s][%s][%s] %s";
	String MESSAGE_FORMAT_SERVER_CHANNEL_USER_MSG = "[%s][%s][%s][%s] %s";

	void init();

}
