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

public class WriteEmailController implements Runnable{
	
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
	
	
	@Override
	public void run() {
		
		buttonSend.setOnAction((ActionEvent e) -> {
			msgAllReq.setVisible(false);
			msgInvalidAddress.setVisible(false);
			if(MailUtils.isBlankOrEmpty(emailTo.getText()) ||
				MailUtils.isBlankOrEmpty(emailSubject.getText()) ||
				MailUtils.isBlankOrEmpty(emailText.getText())) {
				msgAllReq.setVisible(true);
				someError = true;
			} else {
				String addresses = emailTo.getText();
				if(!MailUtils.isBlankOrEmpty(addresses)) {
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
				
				model.setId(new Random().nextInt());
				model.setDate(new Date());
				model.setArgomento(emailSubject.getText());
				model.setDestinatari(emailTo.getText());
				model.setTesto(emailText.getText());
				
				//inviarla al server 
				System.out.println(model.toString());
			}
		});

	}
	
	public void initModel(EmailModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		
		msgAllReq.setVisible(false);
		msgInvalidAddress.setVisible(false);
		
	}

}
