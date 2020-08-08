package server;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerModel {

	/*
	 * Definizione dell'ObservableList serverMessageList
	 */
	private ObservableList<ServerMessageModel> serverMessageList = FXCollections.observableArrayList(
			serverMsg -> new Observable[] {
					serverMsg.typeProperty(),
					serverMsg.dateProperty(),
					serverMsg.msgProperty()
			});
	
	synchronized public ObservableList<ServerMessageModel> serverMessageListProperty() {
		return this.serverMessageList;
	}
	
	synchronized public ObservableList<ServerMessageModel> getServerMessageList() {
		return serverMessageList;
	}
	
	synchronized public void addServerMessage(ServerMessageModel msgReceived) {
		serverMessageList.add(msgReceived);
	}
	
}
