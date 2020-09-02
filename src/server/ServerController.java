package server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import server.ServerMessageModel.MsgType;

public class ServerController {

	@FXML
	private ListView<ServerMessageModel> serverMessageList;
	
	@FXML
	private Button btnQuit;
	
	private ServerModel model;
	
	public void initModel(ServerModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		
		serverMessageList.setItems(model.getServerMessageList());
		
		serverMessageList.setCellFactory(lv -> new ListCell<ServerMessageModel>() {
			@Override
			public void updateItem(ServerMessageModel msg, boolean empty) {
				super.updateItem(msg, empty);
				if(empty) {
					setText(null);
				} else {
					setText(msg.getType() + "  -  " +
							msg.getDate() + "  -  " +
							msg.getMsg() + " "
					);
					if(msg.getType().equals(MsgType.INFO)) {
						setTextFill(javafx.scene.paint.Color.FORESTGREEN);
					} else if(msg.getType().equals(MsgType.WARN)) {
						setTextFill(javafx.scene.paint.Color.ORANGE);
					} else {
						setTextFill(javafx.scene.paint.Color.RED);
					}
				}
			}
		});
		
		btnQuit.setOnAction((ActionEvent e) -> {
			Platform.exit();
	        System.exit(0);
		});
		
	}
	
}
