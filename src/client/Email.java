package client;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * JavaBean
 */
public class Email {
	
	/*
	 * Definizione della property ID
	 */
	private final IntegerProperty id = new SimpleIntegerProperty();
	
	public final IntegerProperty idProperty() {
		return this.id;
	}
	
	public final int getId() {
		return this.idProperty().get();
	}
	
	public final void setId(final int id) {
		this.idProperty().set(id);
	}
	
	/*
	 * Definizione della property MITTENTE
	 */
	private final StringProperty mittente = new SimpleStringProperty();
	
	public final StringProperty mittenteProperty() {
		return this.mittente;
	}
	
	public final String getMittente() {
		return this.mittenteProperty().get();
	}
	
	public final void setMittente(final String mittente) {
		this.mittenteProperty().set(mittente);
	}
	
	/*
	 * Definizione della property DESTINATARIO
	 */
	private final StringProperty destinatario = new SimpleStringProperty();
	
	public final StringProperty destinatarioProperty() {
		return this.destinatario;
	}
	
	public final String getDestinatario() {
		return this.destinatarioProperty().get();
	}
	
	public final void setDestinatario(String destinatario) {
		this.destinatarioProperty().set(destinatario);
	}
	
	/*
	 * Definizione della property ARGOMENTO
	 */
	private final StringProperty argomento = new SimpleStringProperty();
	
	public final StringProperty argomentoProperty() {
		return this.argomento;
	}
	
	public final String getArgomento() {
		return this.argomentoProperty().get();
	}
	
	public final void setArgomento(String argomento) {
		this.argomentoProperty().set(argomento);
	}
	
	/*
	 * Definizione della property TESTO
	 */
	private final StringProperty testo = new SimpleStringProperty();
	
	public final StringProperty testoProperty() {
		return this.testo;
	}
	
	public final String getTesto() {
		return this.testoProperty().get();
	}
	
	public final void setTesto(String testo) {
		this.testoProperty().set(testo);
	}
	
	/*
	 * Costruttore del JavaBean
	 */
	public Email(int id, String mittente, String destinatario, String argomento, String testo) {
		setId(id);
		setMittente(mittente);
		setDestinatario(destinatario);
		setArgomento(argomento);
		setTesto(testo);
	}
	
}