package server;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServerMessageModel {
	
	public enum MsgType{
		INFO,
		WARN,
		ERROR
	}
	
	/*
	 * Definizione della property TYPE
	 */
	private final ObjectProperty<MsgType> type = new SimpleObjectProperty<>(null);
	
	public final ObjectProperty<MsgType> typeProperty() {
		return this.type;
	}
	
	public final MsgType getType() {
		return this.typeProperty().get();
	}
	
	public final void setType(MsgType type) {
		this.typeProperty().set(type);
	}
	
	/*
	 * Definizione della property DATE
	 */
	private final StringProperty date = new SimpleStringProperty();
	
	public final StringProperty dateProperty() {
		return this.date;
	}
	
	public final String getDate() {
		return this.dateProperty().get();
	}
	
	public final void setDate(final Date date) {
		SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
		String stringDate= DateFor.format(date);
		this.dateProperty().set(stringDate);
	}
	
	/*
	 * Definizione della property MSG
	 */
	private final StringProperty msg = new SimpleStringProperty();
	
	public final StringProperty msgProperty() {
		return this.msg;
	}
	
	public final String getMsg() {
		return this.msgProperty().get();
	}
	
	public final void setMsg(String msg) {
		this.msgProperty().set(msg);
	}
	
	/*
	 * Costruttori
	 */
	public ServerMessageModel() {
		setType(MsgType.ERROR);
		setDate(new Date());
		setMsg("No message");
	}
	
	public ServerMessageModel(MsgType type, String msg) {
		setType(type);
		setDate(new Date());
		setMsg(msg);
	}
	
	/*
	 * Metodi di utility
	 */
	@Override
	public String toString() {
		return (this.getType() + " " +
				this.getDate() + " " +
				this.getMsg() + " ");
	}

}
