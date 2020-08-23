package client;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ShowTrashController {
	
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
	private Button buttonMoveToInbox;
	
	@FXML
	private Button buttonDeletePerm;
	
	private CasellaPostaModel model;
	
	public void initModel(CasellaPostaModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		//la message list del trash viene già caricata nel controller principale
		
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
				buttonMoveToInbox.setDisable(true);
				buttonDeletePerm.setDisable(true);
			} else {
				dateField.textProperty().bindBidirectional(newEmail.dateProperty());
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatariProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
				buttonMoveToInbox.setDisable(false);
				buttonDeletePerm.setDisable(false);
			}
		});
		
		if(model.getCurrentEmail() == null) {
			buttonMoveToInbox.setDisable(true);
			buttonDeletePerm.setDisable(true);
		}
		
		buttonMoveToInbox.setOnAction((ActionEvent e) -> {
			if(model.getCurrentEmail() != null) {
				System.out.println("mando al server l'email da togliere dal cestino");
				EmailModel toMove = model.getCurrentEmail();
				Platform.runLater(() -> {	//non so se serve
					toMove.setId(toMove.getId() * -1);
				});
				try {
					model.getClient().sendToServer(toMove);
				} catch (IOException exc) {
					System.err.println("An error occured while moving email from trash to mailbox");
				}
				Platform.runLater(() -> {	//non so se serve
					model.getMessageList().remove(toMove);
				});
			}
		});
		
		buttonDeletePerm.setOnAction((ActionEvent e) -> {
			if(model.getCurrentEmail() != null) {
				EmailModel toDelete = new EmailModel();
				try {
					model.getClient().sendToServer(toDelete);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Platform.runLater(() -> {	//non so se serve
					model.getMessageList().remove(toDelete);
				});
			}
		});
		
	}

}
