package client;

import javafx.application.Platform;
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
		
//		Image imgSrc = null;
//		if(level == MsgType.INFO) {
////			imgSrc = new Image("Images/msg");
//			
//			image = new ImageView("Images/msg.png");
//		} else if(level == MsgType.ERROR) {
////			imgSrc = new Image("Images/error");
//			String url = "Images/error.png";
//			image = new ImageView(url);
//		}
////		image.setImage(imgSrc);
		
		buttonOk.setOnAction((ActionEvent e) -> {
			if(level == MsgType.ERROR) {
				stage.close();
		        System.exit(0);
			} else if (level == MsgType.INFO){
				stage.close();
			}
		});
	}
	
}
