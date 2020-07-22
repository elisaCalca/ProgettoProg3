package client;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailModel {
	
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
	 * Definizione della property DATA
	 */
	private final StringProperty date = new SimpleStringProperty();

	public final StringProperty dateProperty() {
		return this.date;
	}
	
	public final String getDate() {
		return this.dateProperty().get();
	}
	
	public final void setDate(final Date date) {
		SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
		String stringDate= DateFor.format(date);
		this.dateProperty().set(stringDate);
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
	private final StringProperty destinatari = new SimpleStringProperty();
	
	public final StringProperty destinatariProperty() {
		return this.destinatari;
	}
	
	public final String getDestinatari() {
		return this.destinatariProperty().get();
	}
	
	public final void setDestinatari(String destinatari) {
		this.destinatariProperty().set(destinatari);
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
	 * Costruttori
	 */
	public EmailModel(int id, Date date, String mittente, String destinatari, String argomento, String testo) {
		setId(id);
		setDate(date);
		setMittente(mittente);
		setDestinatari(destinatari);
		setArgomento(argomento);
		setTesto(testo);
	}
	
	public EmailModel() {
		
	}
	
	/*
	 * Metodi di utility
	 */
	public boolean equals(EmailModel em) {
		if( this.getId() == em.getId() &&
			this.getDate().equals(em.getDate()) &&
			this.getMittente().equals(em.getMittente()) &&
			this.getDestinatari().equals(em.getDestinatari()) &&
			this.getArgomento().equals(em.getArgomento()) &&
			this.getTesto().equals(em.getTesto())				
			) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return (this.getId() + " " + 
				this.getDate() + " " +
				this.getMittente() + " " +
				this.getArgomento() + " " +
				this.getTesto() + " " +
				this.getDestinatari());
	}
	
}




