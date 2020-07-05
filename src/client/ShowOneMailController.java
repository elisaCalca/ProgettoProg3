package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ShowOneMailController {
	
	@FXML
	private TextField mittenteField;
	
	@FXML
	private TextField destinatarioField;
	
	@FXML
	private TextField argomentoField;
	
	@FXML
	private TextField testoField;
	
	@FXML
	private Button buttonDelete;
	
	private CasellaPostaViewModel model;
	
	public void initModel(CasellaPostaViewModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		this.model = model;
		
		model.loadMessageList(); //aggiunto per far caricare le email esistenti nella casella
		
		model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
			if(oldEmail != null) {
				mittenteField.textProperty().unbindBidirectional(oldEmail.mittenteProperty());
				destinatarioField.textProperty().unbindBidirectional(oldEmail.destinatarioProperty());
				argomentoField.textProperty().unbindBidirectional(oldEmail.argomentoProperty());
				testoField.textProperty().unbindBidirectional(oldEmail.testoProperty());
			}
			if(newEmail == null) {
				mittenteField.setText("");
				destinatarioField.setText("");
				argomentoField.setText("");
				testoField.setText("");
			} else {
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatarioProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
			}
		});
		
		buttonDelete.setOnAction((ActionEvent e) -> {
			/*
			 * PER PROGETTO:
			 * - mettere la delete multipla in base alla checkbox
			 * - quando cancello quello selezionato, la selezione deve sparire e difianco devono svuotarsi le caselle
			 */
			if(model.getMessageList().size() > 0) {		//serve l'if altrimenti, se la lista è vuota, da eccezione
				System.out.println(model.currentEmailProperty().getValue().idProperty());
				model.getMessageList().remove(model.currentEmailProperty().get());
			}
			if(model.getMessageList().size() == 0) {
				//manca il controllo che lo riabilita se da ZERO messaggi torna ad essercene ALMENO UNO
				buttonDelete.setDisable(true);
			}
		});
		
	}

}
