package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBotComponent extends TelegramLongPollingBot {

    private final String botUsername;
    private final Fachada fachada;

    public TelegramBotComponent(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            Fachada fachada) {
        super(botToken);
        this.botUsername = botUsername;
        this.fachada = fachada;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // A) MANEJO DE BOTONES (CallbackQuery)
        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if ("ROL_DONADOR".equals(callData)) {
                String menuDonador = "👤 *Perfil Donador*\n\n" +
                        "Comandos disponibles:\n" +
                        "/registrar <nombre>\n" +
                        "/mis_estadisticas\n" +
                        "/donador <id>\n" +
                        "/donadores_todos";
                enviarTexto(chatId, menuDonador);

            } else if ("ROL_ADMIN".equals(callData)) {
                String menuAdmin = "🛠️ *Perfil Administrador*\n\n" +
                        "Comandos disponibles:\n" +
                        "/crear_entidad <nombre>\n" +
                        "/editar_entidad <id> <nuevo_nombre>\n" +
                        "/entidad <id>\n" +
                        "/entidades_todas\n" +
                        "/crear_necesidad <id_entidad> <desc>\n" +
                        "/editar_necesidad <id> <nueva_desc>\n" +
                        "/borrar_necesidad <id>\n" +
                        "/necesidad <id>";
                enviarTexto(chatId, menuAdmin);
            }
            return;
        }

        // B) MANEJO DE COMANDOS DE TEXTO
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String mensaje = update.getMessage().getText().trim();

            if ("/start".equalsIgnoreCase(mensaje)) {
                enviarMenuInicial(chatId);
            } else {
                procesarComando(chatId, mensaje);
            }
        }
    }

    private void enviarMenuInicial(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("¡Bienvenido al sistema! Por favor, seleccioná tu rol:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton btnDonador = new InlineKeyboardButton();
        btnDonador.setText("👤 Donador");
        btnDonador.setCallbackData("ROL_DONADOR");

        InlineKeyboardButton btnAdmin = new InlineKeyboardButton();
        btnAdmin.setText("🛠️ Admin");
        btnAdmin.setCallbackData("ROL_ADMIN");

        row.add(btnDonador);
        row.add(btnAdmin);
        rows.add(row);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void procesarComando(long chatId, String mensaje) {
        String[] partes = mensaje.split(" ", 2);
        String comando = partes[0].toLowerCase();
        String arg = partes.length > 1 ? partes[1] : "";

        String respuesta;
        try {
            switch (comando) {
                case "/donador":
                    var donador = this.fachada.buscarDonadorPorId(Integer.valueOf(arg));
                    respuesta = "Donador encontrado: " + donador.toString();
                    break;

                case "/borrar_necesidad":
                    this.fachada.borrarNecesidadPorID(Integer.valueOf(arg));
                    respuesta = "✅ Necesidad " + arg + " eliminada correctamente.";
                    break;

                default:
                    respuesta = "Comando no reconocido. Presioná /start para ver las opciones.";
                    break;
            }
        } catch (Exception e) {
            respuesta = "Error al procesar el comando: " + e.getMessage();
        }

        enviarTexto(chatId, respuesta);
    }

    private void enviarTexto(long chatId, String texto) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(texto);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}