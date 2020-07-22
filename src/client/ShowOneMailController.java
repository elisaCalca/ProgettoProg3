package client;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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

		});

		buttonReply.setOnAction((ActionEvent e) -> {
			if (model.getCurrentEmail() == null) {
				System.out.println("MALE perchè in questo caso il bottone deve essere disabilitato.");
			} else {
				
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
			//apre una finestra su un thread parallelo che fa scrivere una nuova email
			EmailModel newEmailModel = new EmailModel();
			newEmailModel.setMittente(model.currentUserProperty().get());
			
			try {
				Stage primaryStage = new Stage();
				
				BorderPane root = new BorderPane();
				FXMLLoader newEmailLoader = new FXMLLoader(getClass().getResource("writeemail.fxml"));
				root.setCenter(newEmailLoader.load());
				WriteEmailController writeEmailController = newEmailLoader.getController();
				writeEmailController.initModel(newEmailModel);
				
				Scene scene = new Scene(root, 755, 450);
				primaryStage.setScene(scene);
				primaryStage.show();
				
				Thread threadWriteEmail = new Thread(writeEmailController);
				threadWriteEmail.setDaemon(true);
				threadWriteEmail.start();
				
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		});
		
		buttonTrash.setOnAction((ActionEvent e) -> {
			
		});
		
		buttonLogOut.setOnAction((ActionEvent e) -> {
			
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

	
}
