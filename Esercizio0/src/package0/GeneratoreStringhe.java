package package0;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import java.util.Scanner;

public class GeneratoreStringhe {

	public static void main(String[] args) {
		
		JSONArray array = new JSONArray();

		HashMap<String, String> ordiniMap = new HashMap<>();	// creazione mappa degli ordini
		ordiniMap.put("Chiave", "ORDINI");
		ordiniMap.put("Valore", "00001");
		array.add(ordiniMap);

		HashMap<String, String> clientiMap = new HashMap<>();	//creazione mappa dei clienti
		clientiMap.put("Chiave", "CLIENTI");
		clientiMap.put("Valore", "00001");
		array.add(clientiMap);

		try (FileWriter file = new FileWriter("dati.json")) {	//inizializzazione file 
			file.write(array.toJSONString());
			file.flush();
			System.out.println("File <dati.json> generato con successo\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scanner sc = new Scanner(System.in);
		String scelta = null;
		String limit = null;
	
		while (scelta != "4") {	//menu' di scelta per l'utente
			
			System.out.println("Per leggere lo stato attuale del file premi 0");
			System.out.println("Per aggiungere un ordine premi 1");
			System.out.println("Per aggiungere un cliente premi 2");
			System.out.println("Per aggiungere sia un cliente che un ordine premi 3");
			System.out.println("Per terminare il programma premi 4");
			System.out.print("\nScelta: ");

			scelta = sc.next();

			switch (scelta) {
			case "0":
				break;			
			case "1":
				limit = generaProssimoValore("ORDINI");
				if(limit == "max") System.out.println("\nE' stato raggiunto il massimo inserimento per gli ordini");
				break;
			case "2":
				limit = generaProssimoValore("CLIENTI");
				if(limit == "max") System.out.println("\nE' stato raggiunto il massimo inserimento per i clienti");
				break;
			case "3":
				limit = generaProssimoValore("ORDINI");
				if(limit == "max") System.out.println("\nE' stato raggiunto il massimo inserimento per gli ordini");
				limit = generaProssimoValore("CLIENTI");
	      		if(limit == "max") System.out.println("\nE' stato raggiunto il massimo inserimento per i clienti");
				break;
			case "4":
				System.out.println("\nProgramma terminato.");
				System.exit(0);
				break;
			default:
				System.out.println("\nScelta non valida, riprova.");
			}
			array = leggiFileJSON();	//lettura da file aggiornata e stampa a video
			System.out.println("\n"+array+"\n");
		}
		sc.close();
	}

	public static String generaProssimoValore(String chiave) {	//generazione del prossimo valore sia numerico che alfabetico
		
		JSONArray array = leggiFileJSON();
		JSONObject oggetto = null;
		String prossimoValore = null;

		for (Object obj : array) {
			JSONObject jsonObject = (JSONObject) obj;
			if (jsonObject.get("Chiave").equals(chiave)) {
				oggetto = jsonObject;
				break;
			}
		}

		String valoreCorrente = (String) oggetto.get("Valore");
		String appoggio = null;
		
		if (valoreCorrente.equals("99999")) {	//primo passo per il cambio tipo di generazione
			
			valoreCorrente = "";
			appoggio = "AAAAA";
		}
		
		try {
			int valoreInt = Integer.parseInt(valoreCorrente);
			valoreInt++;
			prossimoValore = String.format("%05d", valoreInt);
		} catch (NumberFormatException e) {

		    if (appoggio == "AAAAA") {	//se fallisce la conversione in intero faccio generazione alfabetica
				prossimoValore = "AAAAA";
			} else {
				prossimoValore = generaStringaAlfa(valoreCorrente);

			}
		}
		
		if (prossimoValore == "max") {
			return prossimoValore;
		}

		oggetto.put("Valore", prossimoValore);
		array = modificaArrayJSON(array, oggetto);	//aggiornamento del file json

		try (FileWriter file = new FileWriter("dati.json")) {
			file.write(array.toJSONString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prossimoValore;
	}

	private static JSONArray leggiFileJSON() {	//lettura da file json
		
		try (FileReader reader = new FileReader("dati.json")) {
			JSONParser parser = new JSONParser();
			return (JSONArray) parser.parse(reader);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return new JSONArray();
	}

	private static JSONArray modificaArrayJSON(JSONArray array, JSONObject oggetto) {//aggiornamento del file json
		
		JSONArray nuovoArray = new JSONArray();
		
		for (Object obj : array) {
			JSONObject jsonObject = (JSONObject) obj;
			
			if (jsonObject.get("Chiave").equals(oggetto.get("Chiave"))) {
				nuovoArray.add(oggetto);
			} else {
				nuovoArray.add(jsonObject);
			}
			
		}
		return nuovoArray;
	}

	private static String generaStringaAlfa(String valoreCorrente) {
		
		String prossimoValore;
		
		if (valoreCorrente.equals("ZZZZZ")) {	//gestione del limite massimo, fermo l'aggiunta per evitare eccezioni

			return prossimoValore = "max";
		} else {
			
			char[] caratteri = valoreCorrente.toCharArray();
			int posizione = 4;

			while (posizione >= 0) {
				if (caratteri[posizione] < 'Z') {
					caratteri[posizione]++;
					break;
				} else {
					caratteri[posizione] = 'A';
					posizione--;
				}
			}
			
			prossimoValore = new String(caratteri);
		}
		return prossimoValore;
	}
}