package client;

import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mailutils.MailUtils;

public class ShowOneMailController {

	@FXML
	private TextField dateField;

	@FXML
	private TextField mittenteField;

	@FXML
	private TextField destinatarioField;

	@FXML
	private TextField argomentoField;

	@FXML
	private TextArea testoField;

	@FXML
	private Button buttonDelete;

	@FXML
	private Button buttonForward;

	@FXML
	private Button buttonReply;

	@FXML
	private Button buttonWriteNew;
	
	@FXML
	private Button buttonTrash;
	
	@FXML
	private Button buttonLogOut;

	private CasellaPostaViewModel model;

	public void initModel(CasellaPostaViewModel model) {
		if (this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}

		this.model = model;

		model.loadMessageList(); // aggiunto per far caricare le email del file nella casella di posta

		//ascolta l'email che è attualmente selezionata
		model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
			if (oldEmail != null) {
				dateField.textProperty().unbindBidirectional(oldEmail.dateProperty());
				mittenteField.textProperty().unbindBidirectional(oldEmail.mittenteProperty());
				destinatarioField.textProperty().unbindBidirectional(oldEmail.destinatariProperty());
				argomentoField.textProperty().unbindBidirectional(oldEmail.argomentoProperty());
				testoField.textProperty().unbindBidirectional(oldEmail.testoProperty());
			}
			if (newEmail == null) {
				dateField.setText("");
				mittenteField.setText("");
				destinatarioField.setText("");
				argomentoField.setText("");
				testoField.setText("");
				buttonDelete.setDisable(true);
				buttonForward.setDisable(true);
				buttonReply.setDisable(true);
			} else {
				dateField.textProperty().bindBidirectional(newEmail.dateProperty());
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatariProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
				buttonDelete.setDisable(false);
				buttonForward.setDisable(false);
				buttonReply.setDisable(false);
			}
		});
		
		if(model.getCurrentEmail() == null) {
			buttonForward.setDisable(true);
			buttonReply.setDisable(true);
			buttonDelete.setDisable(true);
		}

		buttonForward.setOnAction((ActionEvent e) -> {

		});

		buttonReply.setOnAction((ActionEvent e) -> {
			if (model.getCurrentEmail() == null) {
				System.out.println("miao");
			}
		});

		buttonDelete.setOnAction((ActionEvent e) -> {
			if (model.getMessageList().size() > 0) {
				
				//Scrive la mail nel cestino dell'utente
				Email toDelete = model.currentEmailProperty().get();
				sendEmailToTrash(toDelete);
				
				model.getMessageList().remove(toDelete);
				model.setCurrentEmail(null);
			}
			if (model.getMessageList().size() == 0) {
				// manca il controllo che lo riabilita se da ZERO messaggi torna ad essercene ALMENO UNO (riga 73 testare)
				buttonDelete.setDisable(true);
			}
		});

		buttonWriteNew.setOnAction((ActionEvent e) -> {
			Email newEmail = new Email(8, new Date(), "ciao@ciao.it", "Elisa.Calcaterra@mymail.com", "prova", "testoprova");
			model.addMessage(newEmail);
		});

	}
	
	/*
	 * Legge il vecchio file trash
	 * Aggiunge la nuova email alla lista ottenuta dal trash
	 * Riscrive il trash contenente la nuova email
	 */
	private void sendEmailToTrash(Email trash) {
		String filepath = "Files/Trash/" + model.getCurrentUser() + "_trash.json";
		List<Email> trashList = MailUtils.readEmailsFromJSON(filepath);
		trashList.add(trash);
		MailUtils.writeEmailsInJSON(filepath, trashList);
	}

	
}
