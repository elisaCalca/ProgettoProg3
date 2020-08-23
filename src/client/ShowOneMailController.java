package client;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
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
import javafx.stage.WindowEvent;
import mailutils.MailUtils;
import server.ServerMessageModel.MsgType;

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
	private CasellaPostaModel casellaTrash;

	public void initModel(CasellaPostaModel model) {
		if (this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		casellaTrash = new CasellaPostaModel(model.getClient());
		//la lettura è da far fare al server, questo metodo dovrà ascoltare la socket che riceve dal server
		try {
			model.loadMessageList();// aggiunto per far caricare le email del file nella casella di posta 
		} catch (IOException | ClassNotFoundException e2) {
			e2.printStackTrace();
			System.out.println("An ERROR occured while loading message list");
		} 
		
		//thread che si mette in ascolto delle email ricevute
		new Thread(() -> {
			try {
				System.out.println("Thread che ascolta quando il client riceve");
				while(true) {
					Object received = model.getClient().receiveFromServer();
					
					if(received instanceof EmailModel) {
						if(((EmailModel) received).getId() < 0) {
							//il cestino ha ricevuto una email
							Platform.runLater(() -> {
								casellaTrash.addMessage((EmailModel)received);
							});
						} else {
							//la mailbox ha ricevuto una email
							Platform.runLater(() -> {
								model.addMessage((EmailModel)received);
								try {
									OpenAlert("You have one new message in Mailbox", MsgType.INFO);
								} catch (IOException exc) {
									exc.printStackTrace();
								}
							});
						}
					} else if(received instanceof ArrayList<?>) {
						System.out.println("Load trash list per la casella trash");
						ArrayList<EmailModel> arr = (ArrayList<EmailModel>)received;
						if(arr.size() > 0 && arr.get(0).getId() < 0) {
							for(EmailModel em : arr) {
								Platform.runLater(() -> {
									casellaTrash.addMessage(em);
								});
							}
						}
						
					} else if(received instanceof String) {
						String rec = (String) received;
						if(rec.contains("Not exist ")) {
							String notExist = rec.replaceAll("Not exist ", "");
							Platform.runLater(() -> {
								try {
									OpenAlert("Client " + notExist + " does not exist!", MsgType.INFO);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							});
						}
					}
				}
			} catch (SocketException exc) {
				System.err.println("socket exception showonemailcontroller nel thread che ascolta quello che arriva");
			} catch (ClassNotFoundException e1) {
				System.err.println("Error nel thread che ascolta quando il client riceve delle email");		
			} catch (IOException e2) {
				//popup che dice che il server è caduto
				Platform.runLater(() -> {
					try {
						OpenAlert("ERROR - Unable to contact the server", MsgType.ERROR);
					} catch (IOException ecc) {
						ecc.printStackTrace();
					}
				});
				
			}
		}).start();

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
			if (model.getCurrentEmail() != null) {
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
			if (model.getCurrentEmail() != null) {
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
				//invia al server l'email da cestinare
				EmailModel toDelete = model.getCurrentEmail();
				toDelete.setId(toDelete.getId() * -1);	//metto l'id negativo per far capire al server cosa deve fare con la mail
				try {
					model.getClient().sendToServer(toDelete);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.err.println("An ERROR occured while sending email to Trash");
				}
				
				model.getMessageList().remove(toDelete);
				model.setCurrentEmail(null);
			}
			if (model.getMessageList().size() == 0) {
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
			buttonTrash.setDisable(true);
			casellaTrash.getMessageList().clear();//la svuota così non restano email sporche dal caricamento precedente
			try {
				Stage stage = new Stage();
				//quando la finestra del cestino viene chiusa riattiva il bottone per aprire il cestino
				stage.setOnCloseRequest((WindowEvent w) -> {
					buttonTrash.setDisable(false);
				});
				
				BorderPane root = new BorderPane();
				FXMLLoader listTrashLoader = new FXMLLoader(getClass().getResource("list.fxml"));
				root.setLeft(listTrashLoader.load());
				MailListController listTrashController = listTrashLoader.getController();
				listTrashController.initData("Trash of " + model.currentUserProperty().get());
				
				FXMLLoader editorTrashLoader = new FXMLLoader(getClass().getResource("ShowOneMailTrashed.fxml"));
				root.setRight(editorTrashLoader.load());	
				ShowTrashController editorTrashController = editorTrashLoader.getController();
				
//				CasellaPostaModel casellaTrash = new CasellaPostaModel(model.getClient());
				casellaTrash.currentUserProperty().set(model.currentUserProperty().get());
				listTrashController.initModel(casellaTrash);
				editorTrashController.initModel(casellaTrash);
				
				Scene scene = new Scene(root, 755, 450);
				stage.setScene(scene);
				stage.show();
				
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			//inviare al server la richiesta per riempire il cestino
			try {
				model.getClient().sendToServer("Trash " + model.getCurrentUser());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		
		//alla chiusura tramite bottone LogOut termina tutti i thread JavaFX (stessa JVM)
		buttonLogOut.setOnAction((ActionEvent e) -> {
			try {
				model.getClient().closeChannel(model.getCurrentUser());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Platform.exit();
	        System.exit(0);
		});

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
		writeEmailController.initModel(email, model.getClient());
		
		Scene scene = new Scene(root, 600, 430);
		stage.setScene(scene);
		stage.show();
	}
	
	/*
	 * Apre una finestra di alert che mostra il messaggio che gli viene passato come parametro
	 */
	private void OpenAlert(String message, MsgType type) throws IOException {
		Stage stageAlert = new Stage();
		
		BorderPane root = new BorderPane();
		FXMLLoader alertLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
		root.setCenter(alertLoader.load());
		
		AlertController alertController = alertLoader.getController();
		alertController.init(stageAlert, message, type);
		
		Scene scene = new Scene(root, 400, 200);
		stageAlert.setScene(scene);
		stageAlert.show();
	}

}
