package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import ar.edu.utn.dds.k3003.persist.ColaboradorChatRepository;
import com.fasterxml.jackson.core.io.BigIntegerParser;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

public class Notificar {

    public void alerta(Incidente incidente, ColaboradorDTO colaboradorDTO){ //ok
        /*this.sendNotificationToColaborator(Math.toIntExact(colaboradorDTO.getId()), "la heladera " +
                incidente.getHeladeraId() + " sufrió un incidente de tipo " +
                incidente.getTipoIncidente() + "\n");*/

    System.out.print("Se notifica al colaborador "+ colaboradorDTO.getId() + " que la heladera " +
        incidente.getHeladeraId() + " sufrió un incidente de tipo " + incidente.getTipoIncidente() + "\n");
    }

    public void notiTraslado(ColaboradorDTO colaboradorDTO , Long id){ //ok
       /* this.sendNotificationToColaborator(Math.toIntExact(colaboradorDTO.getId()), "Fuiste asignado al traslado "
                + id + "\n");*/
        System.out.print("Se notifica al colaborador "+ colaboradorDTO.getId() + " que el traslado " + id + " fue asignado" );
    }

    public void sendNotificationToColaborator(int colaboratorId, String message) {
        ColaboradorChatRepository repository = new ColaboradorChatRepository();
        Optional<String> chatIdOptional = repository.findChatIdByIdColaborador((colaboratorId)).describeConstable();

        if (chatIdOptional.isPresent()) {
            String chatId = chatIdOptional.get();

            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                MyTelegramBot bot = new MyTelegramBot("7518388464:AAFsGv921ngMjOqoK1l7nlgS8FTkkKELAfk", "Powerdds");
                botsApi.registerBot(bot);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                System.err.println("Error al enviar mensaje al colaborador con ID " + colaboratorId);
            }
        } else {
            System.out.println("No se encontró el chat ID para el colaborador con ID: " + colaboratorId);
        }
    }
    private static class MyTelegramBot extends org.telegram.telegrambots.bots.TelegramLongPollingBot {
        private final String token;
        private final String username;

        public MyTelegramBot(String token, String username) {
            this.token = token;
            this.username = username;
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }

        @Override
        public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
            // Implementa si necesitas manejar actualizaciones entrantes
        }
    }
}
