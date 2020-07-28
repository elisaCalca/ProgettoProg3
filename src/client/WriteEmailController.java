package client;

import java.util.Date;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mailutils.MailUtils;

public class WriteEmailController {
	
	@FXML
	private Label nameSender;
	
	@FXML
	private TextField emailTo;
	
	@FXML
	private TextField emailSubject;
	
	@FXML
	private TextArea emailText;
	
	@FXML
	private Label msgAllReq;
	
	@FXML
	private Label msgInvalidAddress;
	
	@FXML
	private Button buttonSend;
	
	private EmailModel model;
	private boolean someError = false;
	
	
	public void initModel(EmailModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		
		msgAllReq.setVisible(false);
		msgInvalidAddress.setVisible(false);
		nameSender.setText("Sending email as: " + model.getMittente());
		
		emailTo.textProperty().bindBidirectional(model.destinatariProperty());;
		emailSubject.textProperty().bindBidirectional(model.argomentoProperty());
		emailText.textProperty().bindBidirectional(model.testoProperty());
		
		buttonSend.setOnAction((ActionEvent e) -> {
			msgAllReq.setVisible(false);
			msgInvalidAddress.setVisible(false);
			someError = false;
			if(MailUtils.isNullOrEmpty(emailTo.getText()) ||
				MailUtils.isNullOrEmpty(emailSubject.getText()) ||
				MailUtils.isNullOrEmpty(emailText.getText())) {
				msgAllReq.setVisible(true);
				someError = true;
			} else {
				String addresses = emailTo.getText();
				if(!MailUtils.isNullOrEmpty(addresses)) {
					String[] addressesArray = addresses.split(";");
					for(String addr : addressesArray) {
						if(!MailUtils.isValidAddress(addr)) {
							msgInvalidAddress.setVisible(true);
							someError = true;
						}
					}
				}
			}
			if(!someError) {
				msgAllReq.setVisible(false);
				msgInvalidAddress.setVisible(false);
				
				model.setDate(new Date());
				
				
				//inviarla al server 
				
				System.out.println("model" + model.toString());
			}
		});
	}

}
