import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MainClass {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
        	DrMed1bot d= new DrMed1bot();//così posso caricare gli utenti senza un update
            telegramBotsApi.registerBot(d);//ora posso registare il bot
            
//            telegramBotsApi.registerBot(new DirectionsHandlers());
//            telegramBotsApi.registerBot(new RaeHandlers()); // utf8
//            telegramBotsApi.registerBot(new WeatherHandlers());
//            telegramBotsApi.registerBot(new TransifexHandlers());
//            telegramBotsApi.registerBot(new FilesHandlers());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
