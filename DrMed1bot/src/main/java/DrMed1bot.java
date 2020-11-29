import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DrMed1bot extends TelegramLongPollingBot {
	private ArrayList<Utente> utenti=new ArrayList<Utente>();
    
	DrMed1bot()
 	{
   		try {
				this.utenti=Utente.load();
				load_threads_time();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
    public void onUpdateReceived(Update update) {
    	
    	long new_id= update.getMessage().getChatId();//mi salvo l'id corrente
    	Utente.add_new_utente(utenti,new_id);//lo salvo nella lista
    	
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
        switch (command) {
            case"/annulla":
            message.setText("Comando precedente annullato");
            break;
            case"/start":
            DrMed1bot.set_text_Bot(1, message);
            break;
            case"/printorarifarmaci":
            Utente.print_orari_farmaci(utenti, new_id, message);
        	break;
        	case"/terminate"://solo per il debug errore d'istanza
       		System.exit(0);//esci
       		break;
        	case"/memoorarifarmaco"://è come se chiamasse anche il comando di stampa lista farmaci
       		message.setText("Specificare il numero relativo al farmaco memorizzato e gli orari di assunzione dello stesso separati entrambi da - e gli orari fra loro da , e ore e minuti di un orario nel formato HH:mm \nesempio: 1-18:30,11:30\n"+Utente.print_farmaci(utenti, new_id));
       		break;
            case"/printfarmaci":
            	message.setText(Utente.print_farmaci(utenti, new_id));
        	break;
            case"/deletefarmaci":
        		message.setText("Scrivimi i farmaci numerati che vuoi eliminare:\n es. 1,2,3\n");
            	break;
            case"/memofarmaci":
            	DrMed1bot.set_text_Bot(0, message);
            	break;
            case "/aiuto":
            	DrMed1bot.set_text_Bot(1, message);
            	break;
            default:
            	if(!utenti.get(Utente.get_pos_id(utenti,new_id)).getPrev_cm().equals(""))//serve per darmi il messaggio di benvenuto
            	{
                	if(utenti.get(Utente.get_pos_id(utenti,new_id)).getPrev_cm().equals("/memofarmaci"))
                	{
                		Utente.split_farmaci(",", command, utenti, new_id);
                		DrMed1bot.set_text_Bot(5, message);
                		break;
                	}
                	if(utenti.get(Utente.get_pos_id(utenti,new_id)).getPrev_cm().equals("/deletefarmaci"))
                	{
                		Utente.delete_farmaci(",", command, utenti, new_id);
                		DrMed1bot.set_text_Bot(8, message);
                		break;
                	}
                	if(utenti.get(Utente.get_pos_id(utenti,new_id)).getPrev_cm().equals("/memoorarifarmaco"))
                	{
                		Utente.split_orari_farmaco(",", command, utenti, new_id); //1-18:30,11:30,
                		message.setText("Orari memorizzati!");
                		break;
                	}
            	}
                DrMed1bot.set_text_Bot(3, message);
                break;
        }

        message.setChatId(update.getMessage().getChatId());

        try {
            execute(message);
        }catch(TelegramApiException e){
                e.printStackTrace();
        }
        
    	Utente.set_prev_cm(utenti,new_id,command);//ricorda l'ultimo comando dell'i-esimo utente
    	this.save_users();//salva gli utenti alla fine di un comando
    }

    
    /**
     * per una questione semplicemente estetica numeriamo i comandi da 0 a...
     * per ogni tipo di message.setTex(solo stampe)
     * 
     * -0 :send cmd farmaci
     * -1 :send cmd help
     * -2 :send cmd /prmemo
     * -3 :send messaggio d'errore cmd
     * -4 :send conferma dopo chiamata /prmemo
     * -5 :send conferma dopo /memoFarmaci
     * 
     * @param scegli
     */
    public static void set_text_Bot(int scegli,SendMessage message)
    {
    	switch (scegli){
    	case 0:
        	message.setText("Inserire farmaci da assumere:\nPer inserirne di più usa la virgola\nesempio: Tachipirina,Oki");
        	break;
    	case 1:        	
            message.setText("Lista dei comandi:\n/annulla - annulla l'inserimento di un comando\n/deletefarmaci - specifica i farmaci da rimuovere \n(usare prima /printfarmaci per vedere chi cancellare) \n/printorarifarmaci - Stampa gli orari di tutti i farmaci \n/memoorarifarmaco - memorizza gli orari di un farmaco\n/printfarmaci - stampa i farmaci inseriti \n/memofarmaci - memorizza le medicine \n");
            break;
    	case 2:
    		message.setText("Inserisci la parola da memorizzare");
    		break;
    	case 3:
            message.setText("Comando non valido!");
            break;
    	case 4:
    		message.setText("Comando memorizzato!");
            break;
    	case 5:
    		message.setText("Medicine memorizzate!");
            break;
    	case 8:
    		message.setText("Farmaci (e relativi orari) rimossi!");
    	}//fine switch
    }
    
    public void load_threads_time()
    {
		int i=0;
		while(i<utenti.size())
		{
			int j=0;
			while(j<utenti.get(i).tot_farmaci())
			{
				Contatore c = new Contatore();
				c.Time(utenti.get(i).get_id(), utenti.get(i).get_farmaco_pos(j), Utente.LocalTimeToArrayString(utenti,i,j));		
				j++;
			}
			i++;
		}
    }
    
    /**
     * ad ogni update viene aggiornato il file utenti.dat
     */
    public void save_users()
    {
    		try {
				Utente.save(this.utenti);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
    
    /**
     * esegue il comando case: /print
     * 
     * 
     * @param message
     * @param new_id
     */
    public void case_print(SendMessage message,long new_id)
    {
    	int pos_print= Utente.get_pos_id(utenti,new_id);
    	message.setText(utenti.get(pos_print).getPrev_cm());
    }
    
    public String getBotUsername() {
        return "DrMed1bot";
    }

    /**
     * l'unica cosa che può cambiare se si ricrea un bot con lo stesso nome
     */
    public String getBotToken() {
        return "775702801:AAF5r76mFXPVSPAx5Fjz7uO8LTYZOZhrzy8";
    }
}
