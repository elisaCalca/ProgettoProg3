package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import server.ServerMessageModel.MsgType;

public class AlertController {

	@FXML
	private Label msg;
	
	@FXML
	private ImageView image;
	
	@FXML
	private Button buttonOk;
	
	public void init(Stage stage,String message, MsgType level) {
		
		msg.setText(message);
		
		Image img = null;
		
		if(level == MsgType.INFO) {
			img = new Image("file:Images/msg.png");
		} else if(level == MsgType.ERROR) {
			img = new Image("file:Images/error.png");
		}
		
		image.setImage(img);
		
		buttonOk.setOnAction((ActionEvent e) -> {
			if(level == MsgType.ERROR) {
				stage.close();
				if(!message.contains("not exist")) {
			        System.exit(0);
				} 
			} else if (level == MsgType.INFO){
				stage.close();
			}
		});
	}
	
}
