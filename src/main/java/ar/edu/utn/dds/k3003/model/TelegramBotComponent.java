package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
        // A) MANEJO DE BOTONES INTERACTIVOS (CallbackQuery)
        if (update.hasCallbackQuery()) {
            String callData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callData) {
                // --- MENÚS PRINCIPALES ---
                case "ROL_DONADOR":
                    mostrarSubmenuDonador(chatId);
                    break;

                case "ROL_ADMIN":
                    mostrarSubmenuAdmin(chatId);
                    break;

                case "MENU_INICIAL":
                    enviarMenuInicial(chatId);
                    break;

                // --- INSTRUCCIONES ACCIONES DONADOR ---
                case "ACT_REGISTRAR":
                    enviarTexto(chatId, "📝 *Registrarse como Donador*\n\n" +
                            "Enviá un mensaje con tus datos **separados por coma**:\n\n" +
                            "`/registrar Juan, Perez, 30, juan@email.com, 12345678, Av. Medrano 951`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_ESTADISTICAS":
                    enviarTexto(chatId, "📊 *Consultar Estadísticas*\n\n" +
                            "Enviá el comando con tu ID de donador:\n\n" +
                            "`/mis_estadisticas 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_DONADOR_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Donador por ID*\n\n" +
                            "Enviá el comando con el ID a buscar:\n\n" +
                            "`/donador 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_DONADORES_TODOS":
                    procesarComando(chatId, "/donadores_todos");
                    break;

                // --- INSTRUCCIONES ACCIONES ADMIN (ENTIDADES) ---
                case "ACT_CREAR_ENTIDAD":
                    enviarTexto(chatId, "🏢 *Crear Entidad*\n\n" +
                            "Enviá los datos de la entidad **separados por coma**:\n\n" +
                            "`/crear_entidad Fundación Cimientos, Av. Medrano 951, 1144332211, contacto@cimientos.org`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_EDITAR_ENTIDAD":
                    enviarTexto(chatId, "✏️ *Editar Entidad*\n\n" +
                            "Enviá el ID de la entidad y el nuevo nombre:\n\n" +
                            "`/editar_entidad 1 CimientosOficial`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_ENTIDAD_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Entidad por ID*\n\n" +
                            "Enviá el comando seguido del ID:\n\n" +
                            "`/entidad 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_ENTIDADES_TODAS":
                    procesarComando(chatId, "/entidades_todas");
                    break;

                // --- INSTRUCCIONES ACCIONES ADMIN (NECESIDADES) ---
                case "ACT_CREAR_NECESIDAD":
                    enviarTexto(chatId, "➕ *Alta de Necesidad*\n\n" +
                            "Enviá los datos **separados por coma**:\n\n" +
                            "`/crear_necesidad 1, 3, Leche en polvo, 50, PROD-101, RECURRENTE`\n\n" +
                            "_(Formato: EntidadID, Urgencia(Nro), Descripción, Cantidad, ProductoID, Tipo(EXTRAORDINARIA/RECURRENTE))_\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_EDITAR_NECESIDAD":
                    enviarTexto(chatId, "✏️ *Modificar Necesidad*\n\n" +
                            "Enviá el ID de la necesidad y la nueva descripción:\n\n" +
                            "`/editar_necesidad 5 Leche Larga Vida`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_BORRAR_NECESIDAD":
                    enviarTexto(chatId, "🗑️ *Borrar Necesidad*\n\n" +
                            "Enviá el ID de la necesidad a eliminar:\n\n" +
                            "`/borrar_necesidad 5`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_NECESIDAD_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Necesidad por ID*\n\n" +
                            "Enviá el comando seguido del ID:\n\n" +
                            "`/necesidad 5`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                default:
                    break;
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

    // --- MENÚS Y SUBMENÚS CON BOTONES ---

    private void enviarMenuInicial(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("¡Bienvenido al sistema *DonaTrack*! 👋\n\nPor favor, seleccioná tu rol para continuar:");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(crearBoton("👤 Donador", "ROL_DONADOR"));
        row.add(crearBoton("🛠️ Admin", "ROL_ADMIN"));
        rows.add(row);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuDonador(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("👤 *Panel de Donadores*\n¿Qué acción querés realizar?");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("📝 Registrarse", "ACT_REGISTRAR"));
        row1.add(crearBoton("📊 Mis Estadísticas", "ACT_ESTADISTICAS"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("🔍 Buscar por ID", "ACT_DONADOR_POR_ID"));
        row2.add(crearBoton("📋 Ver Todos los donadores", "ACT_DONADORES_TODOS"));

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("⬅️ Volver al Menú Principal", "MENU_INICIAL"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuAdmin(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🛠️ *Panel de Administración*\nSeleccioná el módulo u opción que necesites:");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("🏢 Crear Entidad", "ACT_CREAR_ENTIDAD"));
        row1.add(crearBoton("✏️ Editar Entidad", "ACT_EDITAR_ENTIDAD"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("🔍 Entidad por ID", "ACT_ENTIDAD_POR_ID"));
        row2.add(crearBoton("📋 Ver todas las Entidades", "ACT_ENTIDADES_TODAS"));

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("➕ Crear Necesidad", "ACT_CREAR_NECESIDAD"));
        row3.add(crearBoton("✏️ Editar Necesidad", "ACT_EDITAR_NECESIDAD"));

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(crearBoton("🗑️ Borrar Necesidad", "ACT_BORRAR_NECESIDAD"));
        row4.add(crearBoton("🔍 Necesidad por ID", "ACT_NECESIDAD_POR_ID"));

        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(crearBoton("⬅️ Volver al Menú Principal", "MENU_INICIAL"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    // --- PROCESAMIENTO DE COMANDOS DE TEXTO ---

    private void procesarComando(long chatId, String mensaje) {
        String[] partes = mensaje.split(" ", 2);
        String comando = partes[0].toLowerCase();
        String arg = partes.length > 1 ? partes[1].trim() : "";

        String respuesta;
        try {
            switch (comando) {
                // =============================================================
                // COMANDOS DONADORES
                // =============================================================
                case "/registrar":
                    String[] campos = arg.split(",");
                    if (campos.length < 6) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/registrar Nombre, Apellido, Edad, Email, DNI, Domicilio`"
                        );
                    }

                    String nombre = campos[0].trim();
                    String apellido = campos[1].trim();
                    String edadStr = campos[2].trim();
                    String email = campos[3].trim();
                    String nroDocumento = campos[4].trim();
                    String domicilio = campos[5].trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || nroDocumento.isEmpty()) {
                        throw new IllegalArgumentException("El Nombre, Apellido, Email y DNI no pueden estar vacíos.");
                    }

                    int edad;
                    try {
                        edad = Integer.parseInt(edadStr);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("La edad debe ser un número entero válido.");
                    }

                    DonadorDTO nuevoDonador = new DonadorDTO(
                            null,
                            nombre,
                            apellido,
                            edad,
                            email,
                            nroDocumento,
                            domicilio,
                            null,
                            null
                    );

                    DonadorDTO donadorCreado = this.fachada.registrarDonador(nuevoDonador);

                    respuesta = "✅ *¡Donador registrado exitosamente!*\n\n" +
                            "🆔 *ID Asignado:* `" + donadorCreado.id() + "`\n" +
                            "👤 *Nombre:* " + donadorCreado.nombre() + " " + donadorCreado.apellido() + "\n\n" +
                            "📌 *Guardá tu ID* para consultar tus estadísticas.";
                    break;

                case "/mis_estadisticas":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/mis_estadisticas 1`");
                    respuesta = this.fachada.obtenerEstadisticasDonador(parsearId(arg));
                    break;

                case "/donador":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/donador 1`");
                    Integer idBuscado = parsearId(arg);
                    DonadorDTO donador = this.fachada.buscarDonadorPorId(idBuscado);
                    respuesta = (donador != null) ? "👤 *Donador encontrado:*\n" + donador : "No se encontró el donador con ID " + idBuscado;
                    break;

                case "/donadores_todos":
                    List<DonadorDTO> donadores = this.fachada.buscarTodosLosDonadores();
                    respuesta = "📋 *Lista de Donadores:*\n" + donadores.toString();
                    break;

                // =============================================================
                // COMANDOS ADMIN (ENTIDADES)
                // =============================================================
                case "/crear_entidad":
                    String[] camposEntidad = arg.split(",");
                    if (camposEntidad.length < 4) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/crear_entidad Razón Social, Domicilio, Teléfono, Correo`"
                        );
                    }

                    String razonSocial = camposEntidad[0].trim();
                    String domicilioEnt = camposEntidad[1].trim();
                    String telefonoEnt = camposEntidad[2].trim();
                    String correoEnt = camposEntidad[3].trim();

                    if (razonSocial.isEmpty() || correoEnt.isEmpty()) {
                        throw new IllegalArgumentException("La Razón Social y el Correo no pueden estar vacíos.");
                    }

                    respuesta = this.fachada.crearEntidad(razonSocial, domicilioEnt, telefonoEnt, correoEnt);
                    break;

                case "/editar_entidad":
                    String[] subArgsEnt = arg.split(" ", 2);
                    if (subArgsEnt.length < 2) throw new IllegalArgumentException("Faltan datos. Ejemplo: `/editar_entidad 1 NuevoNombre`");
                    respuesta = this.fachada.editarEntidad(parsearId(subArgsEnt[0]), subArgsEnt[1]);
                    break;

                case "/entidad":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/entidad 1`");
                    respuesta = this.fachada.buscarEntidadPorId(parsearId(arg));
                    break;

                case "/entidades_todas":
                    respuesta = this.fachada.buscarTodasLasEntidades();
                    break;

                // =============================================================
                // COMANDOS ADMIN (NECESIDADES)
                // =============================================================
                case "/crear_necesidad":
                    String[] camposNec = arg.split(",");
                    if (camposNec.length < 6) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/crear_necesidad EntidadID, Urgencia, Descripción, Cantidad, ProductoID, Tipo`\n\nEjemplo:\n`/crear_necesidad 1, 3, Leche en polvo, 50, PROD-101, RECURRENTE`"
                        );
                    }

                    String entidadId = camposNec[0].trim();
                    Integer urgencia = parsearId(camposNec[1].trim());
                    String desc = camposNec[2].trim();
                    Integer cantidad = parsearId(camposNec[3].trim());
                    String productoId = camposNec[4].trim();

                    TipoNecesidadMaterialEnum tipo;
                    try {
                        tipo = TipoNecesidadMaterialEnum.valueOf(camposNec[5].trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("El tipo debe ser EXTRAORDINARIA o RECURRENTE.");
                    }

                    respuesta = this.fachada.crearNecesidad(entidadId, urgencia, desc, cantidad, productoId, tipo);
                    break;

                case "/editar_necesidad":
                    String[] subArgsEditNec = arg.split(" ", 2);
                    if (subArgsEditNec.length < 2) throw new IllegalArgumentException("Faltan datos. Ejemplo: `/editar_necesidad 5 NuevaDesc`");
                    respuesta = this.fachada.editarNecesidad(parsearId(subArgsEditNec[0]), subArgsEditNec[1]);
                    break;

                case "/borrar_necesidad":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/borrar_necesidad 5`");
                    Integer idBorrar = parsearId(arg);
                    this.fachada.borrarNecesidadPorID(idBorrar);
                    respuesta = "✅ Necesidad ID " + idBorrar + " eliminada correctamente.";
                    break;

                case "/necesidad":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/necesidad 5`");
                    respuesta = this.fachada.buscarNecesidadPorId(parsearId(arg));
                    break;

                default:
                    respuesta = "Comando no reconocido. Enviá /start para abrir el menú principal.";
                    break;
            }
        } catch (Exception e) {
            respuesta = "❌ *Error:* " + e.getMessage();
        }

        enviarTexto(chatId, respuesta);
    }

    // --- MÉTODOS AUXILIARES ---

    private Integer parsearId(String texto) {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID/Número ingresado debe ser un número entero válido.");
        }
    }

    private InlineKeyboardButton crearBoton(String texto, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(texto);
        btn.setCallbackData(callbackData);
        return btn;
    }

    private void enviarTexto(long chatId, String texto) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(texto);
        message.setParseMode(ParseMode.MARKDOWN);
        ejecutarMensaje(message);
    }

    private void ejecutarMensaje(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}