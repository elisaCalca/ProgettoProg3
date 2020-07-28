package client;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
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

	private CasellaPostaModel model;

	public void initModel(CasellaPostaModel model) {
		if (this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}

		this.model = model;

		//la lettura è da far fare al server, questo metodo dovrà ascoltare la socket che riceve dal server
		try {
			model.loadMessageList();// aggiunto per far caricare le email del file nella casella di posta 
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("An ERROR occured while loading message list");
		} 

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
			if (model.getCurrentEmail() == null) {
				System.out.println("MALE perchè in questo caso il bottone doveva essere disabilitato!");
			} else {
				EmailModel forwardEmailModel = new EmailModel();
				forwardEmailModel.setMittente(model.getCurrentUser());
				forwardEmailModel.setArgomento(model.getCurrentEmail().getArgomento());
				forwardEmailModel.setTesto(model.getCurrentEmail().getTesto());
				try {
					
					OpenWriteWindow(forwardEmailModel);
					
				} catch (IOException exc) {
					exc.printStackTrace();
				}
			}
		});

		buttonReply.setOnAction((ActionEvent e) -> {
			if (model.getCurrentEmail() == null) {
				System.out.println("MALE perchè in questo caso il bottone doveva essere disabilitato!");
			} else {
				EmailModel replyEmailModel = new EmailModel();
				replyEmailModel.setMittente(model.getCurrentUser());
				replyEmailModel.setArgomento(model.getCurrentEmail().getArgomento());
				replyEmailModel.setDestinatari(model.getCurrentEmail().getMittente());	//il destinatario della risposta è il mittente
				try {
					
					OpenWriteWindow(replyEmailModel);
					
				} catch (IOException exc) {
					exc.printStackTrace();
				}
			}
		});

		buttonDelete.setOnAction((ActionEvent e) -> {
			if (model.getMessageList().size() > 0) {
				
				//Scrive la mail nel cestino dell'utente
				EmailModel toDelete = model.currentEmailProperty().get();
				try {
					sendEmailToTrash(toDelete);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("An ERROR occured while sending email to Trash");
				}
				
				model.getMessageList().remove(toDelete);
				model.setCurrentEmail(null);
			}
			if (model.getMessageList().size() == 0) {
				// manca il controllo che lo riabilita se da ZERO messaggi torna ad essercene ALMENO UNO (riga 73 testare)
				buttonDelete.setDisable(true);
			}
		});

		buttonWriteNew.setOnAction((ActionEvent e) -> {
//			The Javadoc of Stage states: Stage objects must be constructed and modified on the JavaFX Application Thread.
			EmailModel newEmailModel = new EmailModel();
			newEmailModel.setMittente(model.getCurrentUser());
			try {
				
				OpenWriteWindow(newEmailModel);
				
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			
		});

		//	Elisa.Calcaterra@mymail.com
		buttonTrash.setOnAction((ActionEvent e) -> {
			buttonTrash.setDisable(true);//ok, ma poi come e quando lo riattivo???
			
		});
		
		//alla chiusura tramite bottone LogOut termina tutti i thread JavaFX
		buttonLogOut.setOnAction((ActionEvent e) -> {
			Platform.exit();
	        System.exit(0);
		});

	}
	
	/*
	 * Legge il vecchio file trash
	 * Aggiunge la nuova email alla lista ottenuta dal trash
	 * Riscrive il trash contenente la nuova email
	 */
	private void sendEmailToTrash(EmailModel trash) throws IOException {
		String filepath = "Files/Trash/" + model.getCurrentUser() + "_trash.json";
		List<EmailModel> trashList = MailUtils.readEmailsFromJSON(filepath);
		trashList.add(trash);
		MailUtils.writeEmailsInJSON(filepath, trashList);
	}
	
	/*
	 * Apre la view per:
	 * - scrivere una nuova email
	 * - inoltrare una email
	 * - rispondere ad una email
	 * In base a quale azione lo invoca riceve il model inizializzato nel modo corretto
	 */
	private void OpenWriteWindow(EmailModel email) throws IOException{
		Stage stage = new Stage();
		
		BorderPane root = new BorderPane();
		FXMLLoader newEmailLoader = new FXMLLoader(getClass().getResource("writeemail.fxml"));
		root.setCenter(newEmailLoader.load());
		WriteEmailController writeEmailController = newEmailLoader.getController();
		writeEmailController.initModel(email);
		
		Scene scene = new Scene(root, 600, 430);
		stage.setScene(scene);
		stage.show();
	}

}
