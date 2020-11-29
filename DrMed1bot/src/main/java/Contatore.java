import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Contatore extends TelegramLongPollingBot {
    public void Time(final long id, String farmaco, String[] ora){
            int i = 0;
            long inizio;
            Timer timer = new Timer();
            long ciclo = 86_400_000;//tempo di 1gg

            while(i<ora.length){
                inizio = ChronoUnit.MILLIS.between(LocalTime.parse((LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))),DateTimeFormatter.ofPattern("HH:mm")),
                        LocalTime.parse(ora[i], DateTimeFormatter.ofPattern("HH:mm")));
                if(inizio < 0){
                    inizio = (inizio*24-inizio)* (-1); //se l'orario è del giorno successivo escono i millisecondi negativi
                }
                final String testo= "Sono le " + ora[i] + "! Devi prendere la medicina: " + farmaco;
                timer.scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        SendMessage message = new SendMessage();
                        message.setChatId(id);
                        message.setText(testo);
                        try {
                            execute(message);
                        }catch(TelegramApiException e){
                            e.printStackTrace();
                        }
                       // System.out.println("daniele bufl");
                    }
                },inizio,ciclo);
                i++;
            }

    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    /**
     * devono essere uguali con DrMedBot.java
     */
    @Override
    public String getBotUsername() {
        return "DrMed1bot";
    }

    /**
     * devono essere uguali con DrMedBot.java
     */
    @Override
    public String getBotToken() {
        return "775702801:AAF5r76mFXPVSPAx5Fjz7uO8LTYZOZhrzy8";
    }
}
