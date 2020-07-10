package client;

import java.io.FileWriter;

import org.json.simple.JSONObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

	private CasellaPostaViewModel model;

	public void initModel(CasellaPostaViewModel model) {
		if (this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}

		this.model = model;

		model.loadMessageList(); // aggiunto per far caricare le email del file nella casella

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
			} else {
				dateField.textProperty().bindBidirectional(newEmail.dateProperty());
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatariProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
				buttonDelete.setDisable(false);
			}
		});

		buttonForward.setOnAction((ActionEvent e) -> {

		});

		buttonReply.setOnAction((ActionEvent e) -> {
			if (model.getCurrentEmail() == null) {
				System.out.println("miao");
			}
		});

		buttonDelete.setOnAction((ActionEvent e) -> {
			if (model.getMessageList().size() > 0) { // serve l'if altrimenti, se la lista è vuota, da eccezione
				
				//Scrive la mail nel cestino dell'utente
				Email toDelete = model.currentEmailProperty().get();
				sendEmailToTrash(toDelete);
				
				model.getMessageList().remove(toDelete);
				model.setCurrentEmail(null);
				buttonDelete.setDisable(true);
			}
			if (model.getMessageList().size() == 0) {
				// manca il controllo che lo riabilita se da ZERO messaggi torna ad essercene ALMENO UNO (riga 73 testare)
				buttonDelete.setDisable(true);
			}
		});

		buttonWriteNew.setOnAction((ActionEvent e) -> {

		});

	}
	
	private void sendEmailToTrash(Email trash) {
		JSONObject jsonTrash = new JSONObject();
		jsonTrash.put("id", trash.getId());
		jsonTrash.put("date", trash.getDate());
		jsonTrash.put("mittente", trash.getMittente());
		jsonTrash.put("destinatari", trash.getDestinatari());
		jsonTrash.put("argomento", trash.getArgomento());
		jsonTrash.put("testo", trash.getTesto());
		
		//if not exist create trash.json
		
		//read trash.json
		
		//append the new trashed to the jsonArray
		
		//Write  new JSON trash file
        try (FileWriter file = new FileWriter("File/" + model.getCurrentUser() + "_trash.json")) {
 
            file.write(employeeList.toJSONString());
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

	
}
