package mailutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import client.Email;

public class MailUtils {
	
	/*
	 * Legge le Email presenti in un file .json e le restituisce in una lista
	 */
	public static List<Email> readEmailsFromJSON(String filepath) {
		
		List<Email> emails = new ArrayList<Email>();
		
		JSONParser parser = new JSONParser();
		try {
			JSONArray emailList = (JSONArray) parser.parse(new FileReader(filepath));;
			
			for (int index = 0; index < emailList.size(); index++) {
				JSONObject jsonAtt = (JSONObject)emailList.get(index);
				JSONObject jsonEmail = (JSONObject)jsonAtt.get("email");
				
				int id = Integer.parseInt((String)jsonEmail.get("id"));
				
				String [] res = ((String)jsonEmail.get("date")).split("/");
				Date date = new Date(Integer.parseInt(res[2])-1900, Integer.parseInt(res[1])-1, Integer.parseInt(res[0]));
			
				String mittente = (String)jsonEmail.get("mittente");
				
				String argomento = (String)jsonEmail.get("argomento");
				
				String testo = (String)jsonEmail.get("testo");

				JSONArray jsonDestinatari = (JSONArray)jsonEmail.get("destinatari");
				StringBuilder strDest = new StringBuilder();
				for (int nDes = 0; nDes < jsonDestinatari.size(); nDes++) {
					strDest.append((String)jsonDestinatari.get(nDes));;
					strDest.append(";");
				}
			
				emails.add(new Email(id, date, mittente, strDest.toString(), argomento, testo));
			}
			
		} catch(FileNotFoundException e) {
			if(filepath.contains("trash")) {
				File f = new File(filepath);
				f.getParentFile().mkdirs();
				try {
					f.createNewFile();
					
					//write an empty array in the new trash file
					writeEmailsInJSON(filepath, emails);
					
				} catch(IOException exc) {
					exc.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return emails;
		
	}
	
	/*
	 * Scrive in un file .json le email presenti nella lista
	 */
	@SuppressWarnings("unchecked")
	public static void writeEmailsInJSON(String filepath, List<Email> emails) {

		JSONObject jsonTrash = new JSONObject();
		JSONArray trashArray = new JSONArray();
		
		for(Email em : emails) {
			
			jsonTrash.put("email", em);//bisogna passargli l'hashmap con la mail
			
//			jsonTrash.put("id", em.getId()+"");
//			jsonTrash.put("date", em.getDate());
//			jsonTrash.put("mittente", em.getMittente());
//			jsonTrash.put("destinatari", em.getDestinatari());
//			jsonTrash.put("argomento", em.getArgomento());
//			jsonTrash.put("testo", em.getTesto());
			trashArray.add(jsonTrash);
		}
		
		//Write JSON file
        try (FileWriter file = new FileWriter(filepath)) {
            file.write(trashArray.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}




















