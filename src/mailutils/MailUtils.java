package mailutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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

		JSONArray trashArray = new JSONArray();
		
		for(Email em : emails) {

			JSONObject jsonTrash = new JSONObject();
			JSONObject jsonEmail = new JSONObject();
			jsonEmail.put("id", em.getId()+"");
			jsonEmail.put("date", em.getDate());
			jsonEmail.put("mittente", em.getMittente());
			
			JSONArray jsonDest = new JSONArray();
			String[] destinatari = em.getDestinatari().split(";");
			for(String des : destinatari) {
				jsonDest.add(des);
			}
			
			jsonEmail.put("destinatari", jsonDest);
			
			jsonEmail.put("argomento", em.getArgomento());
			jsonEmail.put("testo", em.getTesto());
			
			jsonTrash.put("email", (JSONObject)jsonEmail);//bisogna passargli l'hashmap con la mail
			
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
	
	/*
	 * Controlla se un indirizzo email è sintatticamente corretto
	 */
	public static boolean isValidAddress(String address) {
		String[] points = address.split(Pattern.quote("."));
		if(points.length == 3) {
			String[] at = points[1].split("@");
			if(at.length == 2) {
				return true;
			}
		}
		return false;
	}

}




















