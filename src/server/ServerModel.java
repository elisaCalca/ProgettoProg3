package server;

import java.util.Date;
import java.util.Hashtable;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.ServerMessageModel.MsgType;

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
	
	/*
	 * Definizione della struttura che memorizza i client connessi - thread safe
	 */
	private Hashtable<String, ServerThread> lstClientAssociated = new Hashtable<String, ServerThread>();
	
	public Hashtable<String, ServerThread> getLstClientAssociated() {
		return lstClientAssociated;
	}

}
