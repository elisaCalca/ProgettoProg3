package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmailModel implements Serializable{
	
	private static final long serialVersionUID = 7004764169781453612L;
	
	/*
	 * Definizione della property ID
	 */
	private transient IntegerProperty id = new SimpleIntegerProperty();
	
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
	private transient StringProperty date = new SimpleStringProperty();

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
	private transient StringProperty mittente = new SimpleStringProperty();
	
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
	private transient StringProperty destinatari = new SimpleStringProperty();
	
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
	private transient StringProperty argomento = new SimpleStringProperty();
	
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
	private transient StringProperty testo = new SimpleStringProperty();
	
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
		int rand = new Random().nextInt();
		setId((rand > 0 ? rand : rand * -1));
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
	
	/*
	 * Ridefinizione dei metodi necessari per la serializzazione dell'oggetto
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
	    s.defaultWriteObject();
		s.writeInt(idProperty().intValue());
	    s.writeUTF(dateProperty().getValueSafe()); //getValueSafe ritorna una stringa vuota se è null
	    s.writeUTF(mittenteProperty().getValueSafe());
	    s.writeUTF(destinatariProperty().getValueSafe());
	    s.writeUTF(argomentoProperty().getValueSafe());
	    s.writeUTF(testoProperty().getValueSafe());
	}
	
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		id = new SimpleIntegerProperty(s.readInt());
		date = new SimpleStringProperty(s.readUTF());
		mittente = new SimpleStringProperty(s.readUTF());
		destinatari = new SimpleStringProperty(s.readUTF());
		argomento = new SimpleStringProperty(s.readUTF());
		testo = new SimpleStringProperty(s.readUTF());
	}
	/*
	 * https://stackoverflow.com/questions/44931603/javafx-io-exception-about-implements-serializable
	 */
}




