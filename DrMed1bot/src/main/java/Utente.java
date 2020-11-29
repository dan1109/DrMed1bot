import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * un utente è composto da:
 * id -user
 * comando precedente - prev_cm
 * una lista di medicine - medicine
 * l'orarrio di assunzione - orario_medicine
 * 
 * per il debug prova
 * @author bufal
 *
 */
public class Utente implements java.io.Serializable{

	/**
	 * suggerito come warning per la Serializable 
	 */
	private static final long serialVersionUID = 1L;
	private long id;                           												//tipo l'id univoco per utente
	private String prev_cm="";                    											//il comando precedente dell'utente
	private ArrayList<String> medicine=new ArrayList<String>();      					    //la lista medicine dell'utente
	private ArrayList<Orari_Medicina> orario_medicine= new ArrayList<Orari_Medicina>();    //lista di medicina per utente, essendo una lista di utenti abbiamo una 2D di orari

	public long get_id()
	{
		return id;
	}
	
	/**
	 * restituisce il numero di farmaci di un utente
	 * @return
	 */
	public int tot_farmaci()
	{
		return medicine.size();
	}
	
	/**
	 * di un utente data la posizione del farmaco, ne restituisce il nome
	 * @param pos_farmaco
	 * @return
	 */
	public String get_farmaco_pos(int pos_farmaco)
	{
		String farmaco=medicine.get(pos_farmaco);
		return farmaco;
	}
	
	/**
	 * funzione che verifica e aggiunge un nuovo utente non duplicato
	 * @param u1
	 * @param u2
	 * @return
	 */
	public static void add_new_utente(ArrayList<Utente> u1,long id_2)
	{
		int i=0;
		boolean find=false;
		while(i<u1.size())//finchè la lista non è vuota
		{
			if(u1.get(i).id==id_2)//verifica se gli id sono uguali
			{
				i=u1.size(); //se l'utente esiste esci
				find=true;
			}
			i++;
		}
		if(find==false)//aggiunta opzionale
		{
			u1.add(new Utente());
			u1.get(u1.size()-1).id=id_2;
		}
	}
	
	/**
	 * in base all'id(esistente!) memorizza il comando immesso per un possibile dialogo
	 * @param u1
	 * @param id_2
	 * @param cmd_prec
	 */
	public static void set_prev_cm(ArrayList<Utente> u1,long id_2,String cmd_prec)
	{
		int i=0;
		while(i<u1.size())
		{
			if(u1.get(i).id==id_2)//verifica se gli id sono uguali
			{				
				u1.get(i).setPrev_cm(cmd_prec);
			}
			i++;
		}
	}
	
	/**
	 * dal nome del farmaco ne restituisce la posizione della lista di tutti i suoi orari.
	 * se il farmaco non esiste,restituisce una posizione negativa
	 * @param n
	 * @return
	 */
	public static int pos_farmaco_by_nome(ArrayList<Utente> u1,long id,String n)
	{
		int pos_id= Utente.get_pos_id(u1, id);
		if(u1.get(pos_id).medicine.contains(n))
		{
			return u1.get(pos_id).medicine.indexOf(n);
		}
		else
		{
			return -1;
		}	
	}
	
	/**
	 * invia all'utente tutti gli orari delle relative medicine
	 * 
	 * @param m
	 * @param u1
	 * @param id
	 * @param n
	 */
	public static void print_orari_farmaci(ArrayList<Utente> u1,long id,SendMessage m)
	{
		int i=0;
		int pos_id= Utente.get_pos_id(u1, id);
		String all=new String();
		while(i<u1.get(pos_id).orario_medicine.size())
		{
			all=all.concat(print_orari_pos_farmaco(m,u1,id,u1.get(pos_id).medicine.get(i)));
			i++;
		}
		if(all.isEmpty())
		{
			all="Non ci sono medicine";
		}
		m.setText(all);
	}
	
	/**
	 * stampa, dato l'indice di una medicina specifica, la lista dei suoi orari
	 * @param m
	 * @param pos_farmaco
	 * @return
	 */
	public static String print_orari_pos_farmaco(SendMessage m,ArrayList<Utente> u1,long id,String n)
	{
		int pos_id= Utente.get_pos_id(u1, id);
		int pos_farmaco=pos_farmaco_by_nome(u1,id,n);
		String orari_farmaco="Orari per farmaco "+u1.get(pos_id).medicine.get(pos_farmaco)+"\n"+u1.get(pos_id).orario_medicine.get(pos_farmaco).All_orari()+"\n";
		return 	orari_farmaco;
	}
	
	
	/**
	 * restituisce la posizione di dove sta il mio id
	 * 
	 * negativo se non c'è
	 * 
	 * @param u1
	 * @param id_2
	 * @return
	 */
	public static int get_pos_id(ArrayList<Utente> u1,long id_2)
	{
		int i=0;
		while(i<u1.size())
		{
			if(u1.get(i).id==id_2)//verifica se gli id sono uguali
			{				
				return i;
			}
			i++;
		}
		return -1; //l'elemento non sta in lista
	}

	/**
	 * dato un carattere separatore split_char, la serie di farmaci elencati in una stringa, inserisce in
	 * una pos della lista utenti (ad un id specifico) tutti i farmaci nella lista dei farmaci
	 * i farmaci sono convertiti direttamente in minuscolo.
	 * 
	 * duplicati non sono ammessi.
	 * 
	 * @param farmaci
	 * @param u1
	 */
	public static void split_farmaci(String split_char,String farmaci,ArrayList<Utente> u1,long id)
	{
		String [] array_appoggio= farmaci.split(split_char);
		//funziona che elimina gli spazi aggiuntivi tra una virgola e l'altra..
		int i=0;
		while(i<array_appoggio.length)//per tutti gli elementi splittati...
		{
			array_appoggio[i]=array_appoggio[i].toLowerCase();//prima tutto in minuscolo
			array_appoggio[i]=array_appoggio[i].trim();//poi si elimina lo spazi in eccesso (lascia 1 per parola)
			if(array_appoggio[i].startsWith(" "))//se il farmaco inizia con uno spazio(digitazione in errore " ciao")
			{
				array_appoggio[i]=array_appoggio[i].replaceFirst(" ", null);//togli il primo spazio se c'è
			}
			i++;
		}
		//adesso gli elementi sono tutti fixati per facilitare la rimozione dei duplicati
		int pos= Utente.get_pos_id(u1,id);
		i=0;
		while(i<array_appoggio.length)//per tutti gli elementi splittati...
		{
			if(!u1.get(pos).medicine.contains(array_appoggio[i]))//se non ci sono duplicati...
			{
				u1.get(pos).medicine.add(array_appoggio[i]); //...aggiunge in lista farmaci
				u1.get(pos).orario_medicine.add(new Orari_Medicina());//aggiunge l'oggetto orario per accedere alle liste
			}
			i++;
		}
	}
	
	/**
	 * cancella i farmaci specificati e gli orari 
	 */
	public static void delete_farmaci(String split_char,String farmaci,ArrayList<Utente> u1,long id)
	{
		String[] array_appoggio= farmaci.split(split_char);
		//funziona che elimina gli spazi aggiuntivi tra una virgola e l'altra..
		int i=0;
		while(i<array_appoggio.length)//per tutti gli elementi splittati...
		{
			array_appoggio[i]=array_appoggio[i].trim();//poi si elimina lo spazi in eccesso (lascia 1 per parola)
			if(array_appoggio[i].startsWith(" "))//se il farmaco inizia con uno spazio(digitazione in errore " ciao")
			{
				array_appoggio[i]=array_appoggio[i].replaceFirst(" ", null);//togli il primo spazio se c'è
			}
			i++;
		}
		//adesso gli elementi sono tutti fixati per facilitare la rimozione dei duplicati
		int pos= Utente.get_pos_id(u1,id);
		i=0;
		while(i<array_appoggio.length)//per tutti gli elementi splittati...
		{
			if(Integer.parseInt(array_appoggio[i])>=1 && Integer.parseInt(array_appoggio[i])<=u1.get(pos).medicine.size())//se non ci sono duplicati...
			{
				u1.get(pos).medicine.remove(Integer.parseInt(array_appoggio[i])-1); //...cancella il farmaco i lista
				u1.get(pos).orario_medicine.remove(Integer.parseInt(array_appoggio[i])-1);//aggiunge l'oggetto orario per accedere alle liste
			}
			i++;
		}
	}
	
	/**
	 * dato un carattere separatore split_char, la serie di orari del farmaco elencati in una stringa, inserisce in
	 * una pos della lista utenti (ad un id specifico) tutti gli orari dei farmaci nella lista dei farmaci
	 * 
	 * orari duplicati non sono ammessi.
	 * 
	 * @param farmaci
	 * @param u1
	 */
	public static void split_orari_farmaco(String split_char,String orari,ArrayList<Utente> u1,long id)
	{
		String [] array_appoggio2= orari.split("-");//esempio:(1-18:30,11:30) -> (1 , 18:30,11:30 ) splitto per -
		final int pos_farmaco = Integer.parseInt(array_appoggio2[0])-1; //se la lista vista dall'utente parte da 1 a n devo sottrarre un valore per programmare
		final int pos_id= Utente.get_pos_id(u1,id);
	
		if(pos_farmaco>=0 && pos_farmaco<u1.get(pos_id).medicine.size())//se la posizione del farmaco è legit
		{
			String [] array_appoggio= array_appoggio2[1].split(split_char);//esempio:(18:30,11:30) -> (18:30),(11:30) splitto per ,
			
			int i=0;
			while(i<array_appoggio.length)//per tutti gli elementi splittati...
			{
				if(array_appoggio[i].startsWith(" "))//se il farmaco inizia con uno spazio(digitazione in errore " 18:30")
				{
					array_appoggio[i]=array_appoggio[i].replaceFirst(" ", null);//togli il primo spazio se c'è
				}
				i++;
			}
			
			ArrayList<LocalTime> list_appoggio=new ArrayList<LocalTime>();
			i=0;
			while(i<array_appoggio.length)
			{
				list_appoggio.add(Orari_Medicina.StringToLocalTime(array_appoggio[i]));//aggiunge un oggetto localtime dalla stringa "18:30",per ogni orario
				i++;
			}
			array_appoggio2=null;//non serve più
			array_appoggio=null;//non serve più
		
			list_appoggio.removeAll(Collections.singletonList(null));//rimuove tutti gli orari null della lista (sono null se avevi inserito un'orario sbagliato)
			
			i=0;
			while(i<list_appoggio.size())//per tutti gli elementi splittati...
			{
				u1.get(pos_id).orario_medicine.get(pos_farmaco).add_Or_med_pos(list_appoggio.get(i));
				i++;
			}	
			
			list_appoggio=null;//anche questo non mi serve più
			i=0;
			int size=u1.get(pos_id).orario_medicine.get(pos_farmaco).get_size_farmaco();
			String [] ora = new String[size];//alloca un vettore statico pari al numero di orari del farmaco		
			while(i<size)
			{
				ora[i]=u1.get(pos_id).orario_medicine.get(pos_farmaco).Hour_med(i)+":"+u1.get(pos_id).orario_medicine.get(pos_farmaco).Minute_med(i);
				i++;
			}
			Contatore c = new Contatore();
			c.Time(id, u1.get(pos_id).medicine.get(pos_farmaco), LocalTimeToArrayString(u1,pos_id,pos_farmaco));//ora è un array temporaneo di String
		}
	}
	
	/**
	 * dato una lista di ora restituisce una array di strignhe
	 * @param u1
	 * @param pos_id
	 * @param pos_farmaco
	 * @return
	 */
	public static String[] LocalTimeToArrayString(ArrayList<Utente> u1,int pos_id,int pos_farmaco)
	{
		int i=0;
		int size=u1.get(pos_id).orario_medicine.get(pos_farmaco).get_size_farmaco();
		String [] ora = new String[size];//alloca un vettore statico pari al numero di orari del farmaco		
		while(i<size)
		{
			int h=u1.get(pos_id).orario_medicine.get(pos_farmaco).Hour_med(i);
			int m=+u1.get(pos_id).orario_medicine.get(pos_farmaco).Minute_med(i);
			if(h==0)
			{
				ora[i]="00"+":";
			}
			else
			{
				if(h<10)
				{
					ora[i]="0"+h+":";
				}
				else
				{
					ora[i]=h+":";
				}
			}
			if(m==0)
			{
				ora[i]=ora[i]+"00";
			}
			else
			{
				if(m<10)
				{
					ora[i]=ora[i]+"0"+m;
				}
				else
				{
					ora[i]=ora[i]+m;
				}
			}
			i++;
		}
		return ora;
	}
	
	/**
	 * stampa le medicine del utente id della lista u1  mandandolo nel messaggio m
	 * 
	 * @param u1
	 * @param id
	 */
	public static String print_farmaci(ArrayList<Utente> u1,long id)
	{
		int pos= Utente.get_pos_id(u1,id);
		String medicine_ord=new String();
		medicine_ord=medicine_ord.concat("Lista medicine ordinate:\n");
		int i=0;
		while(i<u1.get(pos).medicine.size())
		{
			medicine_ord=medicine_ord.concat((i+1)+" : "+u1.get(pos).medicine.get(i)+"\n");
		    i++;
		}
		if(medicine_ord.equals("Lista medicine ordinate:\n"))
		{
			medicine_ord=new String();
			medicine_ord="Non ci sono medicine";
		}
		return medicine_ord;
	}
	
	   /**
		 * Salva una lista di utenti su un file predefinito "utenti.dat"
		 *
		 * @throws FileNotFoundException the file not found exception
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void save(ArrayList<Utente> u1) throws FileNotFoundException, IOException
		{
			String filename = "utenti.dat";
			// Serialization
			try
			{
				//Saving of object in a file
				FileOutputStream file = new FileOutputStream(filename);
				ObjectOutputStream out = new ObjectOutputStream(file);

				// Method for serialization of object
				out.writeObject(u1);// da errore qui? Risolto: devi scrivere implements Serializable ad ogni oggetto di Utente (es. orari_farmaco)

				out.close();
				file.close();

			}

			catch(IOException ex)
			{
				System.out.println("IOException is caught");
			}
		}


		/**
		 * data una lista in input la sostituisce da una caricata da file
		 * Load.
		 *
		 * @throws FileNotFoundException the file not found exception
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @throws ClassNotFoundException 
		 */
		@SuppressWarnings("unchecked")
		public static ArrayList<Utente> load() throws FileNotFoundException, IOException, ClassNotFoundException
		{
			String filename = "\\utenti.dat";	//dove caricheremo da file
			
			File directory = new File("");
			String path=directory.getAbsolutePath();
			path=path.concat(filename);
			
			filename = filename.substring(1);//utenti.path
			ArrayList<Utente> u2= new ArrayList<Utente>();		//struttura di passaggio
			
			// Deserialization
			File tempFile = new File(path);//verifica il percorso assoluto dov'è salvato il file
			boolean exists = tempFile.exists();
			if(exists==true)//solo se esiste il file lo carichi,altrimenti u2 resta vuoto
			{
				// Reading the object from a file
				FileInputStream file = new FileInputStream(filename);//l'eccezione parte da qui se il file non esiste
				ObjectInputStream in = new ObjectInputStream(file);

				// Method for deserialization of object
				u2= (ArrayList<Utente>)in.readObject();
					
				in.close();
				file.close();
			}		
			return u2;
		}
	
	/**
	 * ottiene il comando precedente inserito
	 * @return
	 */
	public String getPrev_cm() {
		return this.prev_cm;
	}

	/**
	 * setta il comando inserito
	 * @param prev_cm
	 */
	public void setPrev_cm(String prev_cm) {
		this.prev_cm = prev_cm;
	}
	
}
