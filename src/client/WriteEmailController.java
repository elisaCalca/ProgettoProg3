package client;

import java.io.IOException;
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
	private Client client;
	
	
	public void initModel(EmailModel model, Client client) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		this.client = client;
		
		msgAllReq.setVisible(false);
		msgInvalidAddress.setVisible(false);
		nameSender.setText("Sending email as: " + model.getMittente());
		
		model.destinatariProperty().addListener((obs, oldVal, newVal) -> {
			boolean valid = true;
			String[] address = newVal.trim().split(";");
			for(String addr : address) {
				valid = valid ? MailUtils.isValidAddress(addr) : false;
				if(!valid) {
					msgInvalidAddress.setVisible(true);
					buttonSend.setDisable(true);
				} else {
					msgInvalidAddress.setVisible(false);
					buttonSend.setDisable(false);
				}
			}
		});
		
		emailTo.textProperty().bindBidirectional(model.destinatariProperty());;
		emailSubject.textProperty().bindBidirectional(model.argomentoProperty());
		emailText.textProperty().bindBidirectional(model.testoProperty());
		
		buttonSend.setOnAction((ActionEvent e) -> {
			msgAllReq.setVisible(false);	//non dovrebbe più servire dopo il timer, testare!
			msgInvalidAddress.setVisible(false);
			someError = false;
			if(MailUtils.isNullOrEmpty(emailTo.getText()) ||
				MailUtils.isNullOrEmpty(emailSubject.getText()) ||
				MailUtils.isNullOrEmpty(emailText.getText())) {
				
				msgAllReq.setVisible(true);
				new Thread (() -> {
					try {
						Thread.sleep(3000);
						msgAllReq.setVisible(false);
					} catch (InterruptedException exc) {
						exc.printStackTrace();
					}
				}).start();
				
				someError = true;
			}
			if(!someError) {
				msgAllReq.setVisible(false);
				msgInvalidAddress.setVisible(false);
				
				model.setDate(new Date());
				
				try {
					client.sendToServer(model);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.out.println("Email inviata " + model.toString());
			}
		});
	}

}
