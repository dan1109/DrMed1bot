import java.time.LocalTime;
import java.util.ArrayList;

public class Orari_Medicina implements java.io.Serializable{

	/**
	 * default id per la serializzazione
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<LocalTime> orario_medicine= new ArrayList<LocalTime>() ; //fasce orarie di una medicina

	/**
	 * restituisce tutto l'orario di una certa pos
	 * @param pos
	 * @return
	 */
	public LocalTime getOrario(int pos) {
		return this.orario_medicine.get(pos);
	}

	/**
	 * restituisce il numero di orari inseriti di un farmaco
	 * 
	 * @param pos_farmaco
	 * @return
	 */
	public int get_size_farmaco()
	{
		return orario_medicine.size();
	}
	
	/**
	 * utile per una funzione di stampa,
	 * l'array list di orari di un farmaco
	 * @param pos_farmaco
	 * @return
	 */
	public String All_orari()
	{
		return this.orario_medicine.toString();
	}
	
	/**
	 * restituisce l'ora di una certa pos
	 * es. "18:30" -> 18
	 * @param pos
	 * @return
	 */
	public int Hour_med(int pos) {
		return this.orario_medicine.get(pos).getHour();
	}
	
	/**
	 * restituisce i minuti di una certa pos
	 *es. "18:30" -> 30
	 * @param pos
	 * @return
	 */
	public int Minute_med(int pos) {
		return this.orario_medicine.get(pos).getMinute();
	}
	
	/**
	 * setta tutto l'orario di una certa posizione
	 * @param o
	 * @param pos
	 */
	public void setOr_med_pos(LocalTime o,int pos) {
		if(pos>=0 && pos<this.orario_medicine.size())//se è una posizione legittima
		{
			this.orario_medicine.set(pos,o);//modifica l'orario
		}
		else//altrimenti
		{
			this.add_Or_med_pos(o);//aggiungi nuovo orario in lista
		}
	}
	
	/**
	 * data una stringa del tipo "18:30"
	 * restituisce un oggetto di tipo o contenente hour=18 e minutes=30
	 * 
	 * @param s
	 * @return
	 */
	public static LocalTime StringToLocalTime(String s)
	{
		LocalTime o = null;
		String [] array_appoggio= s.split(":");
		int hour=Integer.parseInt(array_appoggio[0]);//converto ore... 
		int minut=Integer.parseInt(array_appoggio[1]);//e minuti in interi
		
		if(hour>=0 && hour <=23 && minut>=0 && minut<=59)//orario legit pls
		{
			o = LocalTime.of(hour,minut);
		}
		return o;	
	}
	
	/**
	 * aggiungi un nuovo orario in lista
	 * 
	 * non ci sono duplicati
	 * 
	 * @param o
	 * @param pos
	 */
	public void add_Or_med_pos(LocalTime o) {
		if(!this.orario_medicine.contains(o))
		{
			this.orario_medicine.add(o);
		}
	}
	
	/**
	 * elimina un orario specifico se è stato inserito
	 * @param o
	 * @param pos
	 */
	public void remove_Or_med_pos(LocalTime o) {
		if(this.orario_medicine.contains(o))
		{
			this.orario_medicine.remove(o);
		}
	}
	
}
