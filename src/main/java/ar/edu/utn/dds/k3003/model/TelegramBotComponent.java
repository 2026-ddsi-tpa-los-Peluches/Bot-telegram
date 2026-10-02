package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
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

            System.out.println("🔘 CALLBACK RECIBIDO: " + callData);

            long chatId = update.getCallbackQuery().getMessage().getChatId();

            try {
                AnswerCallbackQuery answer = new AnswerCallbackQuery();
                answer.setCallbackQueryId(update.getCallbackQuery().getId());
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            switch (callData) {
                // --- MENÚS PRINCIPALES ---
                case "ROL_DONADORES_Y_ENTIDADES":
                    mostrarSubmenuDonadoresYEntidades(chatId);
                    break;

                case "ROL_LOGISTICA":
                    mostrarSubmenuLogistica(chatId);
                    break;

                case "ROL_INCENTIVOS":
                    mostrarSubmenuIncentivos(chatId);
                    break;

                case "ROL_DONACIONES":
                    mostrarSubmenuDonaciones(chatId);
                    break;

                case "MENU_INICIAL":
                    enviarMenuInicial(chatId);
                    break;

                // --- INSTRUCCIONES ACCIONES DONADORES Y ENTIDADES ---
                case "ACT_REGISTRAR":
                    enviarTexto(chatId, "📝 *Registrarse como Donador*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Apellido, Edad, Correo, Teléfono, Domicilio`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/registrar Juan, Perez, 30, juan@email.com, 12345678, Av. Medrano 951`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_ESTADISTICAS":
                    enviarTexto(chatId, "📊 *Consultar Estadísticas*\n\n" +
                            "📋 *Campo requerido:* `DonadorID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/mis_estadisticas 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_DONADOR_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Donador por ID*\n\n" +
                            "📋 *Campo requerido:* `DonadorID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/donador 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_DONADORES_TODOS":
                    procesarComando(chatId, null, "/donadores_todos");
                    break;

                // --- INSTRUCCIONES ACCIONES ADMIN (ENTIDADES) ---
                case "ACT_CREAR_ENTIDAD":
                    enviarTexto(chatId, "🏢 *Crear Entidad Benéfica*\n\n" +
                            "📋 *Campos requeridos:* `Razón Social, Domicilio, Teléfono, Correo`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_entidad Fundación Cimientos, Av. Medrano 951, 1144332211, contacto@cimientos.org`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_EDITAR_ENTIDAD":
                    enviarTexto(chatId, "✏️ *Editar Entidad*\n\n" +
                            "📋 *Campos requeridos:* `ID, Razón Social, Domicilio, Teléfono, Correo`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/editar_entidad 1, Fundación Cimientos, Av. Medrano 951, 1144332211, contacto@cimientos.org`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_ENTIDAD_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Entidad por ID*\n\n" +
                            "📋 *Campo requerido:* `EntidadID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/entidad 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_ENTIDADES_TODAS":
                    procesarComando(chatId, null, "/entidades_todas");
                    break;

                // --- INSTRUCCIONES ACCIONES ADMIN (NECESIDADES) ---
                case "ACT_CREAR_NECESIDAD":
                    enviarTexto(chatId, "➕ *Alta de Necesidad*\n\n" +
                            "📋 *Campos requeridos:* `EntidadID, Urgencia(Nro), Descripción, Cantidad, ProductoID, Tipo (EXTRAORDINARIA/RECURRENTE)`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_necesidad 1, 3, Leche en polvo, 50, 1, RECURRENTE`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_EDITAR_NECESIDAD":
                    enviarTexto(chatId, "✏️ *Editar Necesidad*\n\n" +
                            "📋 *Campos requeridos:* `ID, Urgencia, Descripción`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/editar_necesidad 5, 4, Leche Larga Vida`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_BORRAR_NECESIDAD":
                    enviarTexto(chatId, "🗑️ *Borrar Necesidad*\n\n" +
                            "📋 *Campo requerido:* `ID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/borrar_necesidad 5`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_NECESIDAD_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Necesidad por ID*\n\n" +
                            "📋 *Campo requerido:* `NecesidadID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/necesidad 5`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                // --- INSTRUCCIONES ACCIONES LOGÍSTICA ---
                case "ACT_CREAR_DEPOSITO":
                    enviarTexto(chatId, "🏢 *Crear Depósito*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Dirección, Capacidad, Algoritmo (SUB_ATENDIDOS, PRIORIDAD_POR_SCORE)`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_deposito Deposito Central, Av. Siempre Viva 123, 500, PRIORIDAD_POR_SCORE`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_DEPOSITO_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Depósito por ID*\n\n" +
                            "📋 *Campo requerido:* `DepositoID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/deposito 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;
                case "ACT_DEPOSITOS_TODOS":
                    procesarComando(chatId, null, "/depositos_todos");
                    break;

                case "ACT_ASIGNACIONES_TODAS":
                    procesarComando(chatId, null, "/asignaciones_todas");
                    break;

                case "ACT_GESTIONAR_DONACION":
                    enviarTexto(chatId, "📦 *Gestionar Donación*\n\n" +
                            "📋 *Campos requeridos:* `DepositoID, DonacionID, ProductoID, Cantidad`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/gestionar_donacion 1, 10, 1, 5`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                // =============================================================
                // INSTRUCCIONES ACCIONES INCENTIVOS
                // =============================================================

                case "ACT_INSIGNIAS_TODAS":
                    procesarComando(chatId, null, "/insignias_todas");
                    break;

                case "ACT_MISIONES_TODAS":
                    procesarComando(chatId, null, "/misiones_todas");
                    break;

                case "ACT_ASIGNAR_INSIGNIA":
                    enviarTexto(chatId, "🏅 *Asignar Insignia a Donador*\n\n" +
                            "📋 *Campos requeridos:* `DonadorID, InsigniaID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/asignar_insignia 1, 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_ASIGNAR_MISION":
                    enviarTexto(chatId, "🎯 *Asignar Misión a Donador*\n\n" +
                            "📋 *Campos requeridos:* `DonadorID, MisionID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/asignar_mision 1, 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_MISION_CURSO":
                    enviarTexto(chatId, "🔍 *Consultar Misión en Curso*\n\n" +
                            "📋 *Campo requerido:* `DonadorID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/mision_curso 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_QUITAR_MISION":
                    enviarTexto(chatId, "❌ *Cancelar Misión en Curso*\n\n" +
                            "📋 *Campo requerido:* `DonadorID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/quitar_mision 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_CREAR_INSIGNIA":
                    enviarTexto(chatId, "🏅 *Crear Insignia*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Descripción, Criterio`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_insignia Donador Estrella, Otorgada por superar 10 donaciones, 10_DONACIONES`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_INSIGNIA_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Insignia por ID*\n\n" +
                            "📋 *Campo requerido:* `InsigniaID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/insignia 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                case "ACT_CREAR_MISION":
                    enviarTexto(chatId, "🎯 *Crear Misión*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Descripción, Requisito`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_mision Misión Semanal, Completar 3 donaciones en la semana, 3`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "ACT_MISION_POR_ID":
                    enviarTexto(chatId, "🔍 *Consultar Misión por ID*\n\n" +
                            "📋 *Campo requerido:* `MisionID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/mision 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá el ID y envialo)_");
                    break;

                // DONACIONES
                case "DON_CATEGORIAS":
                    procesarComando(
                            chatId,
                            null,
                            "/categorias"
                    );
                    break;

                case "DON_CREAR_CATEGORIA":
                    enviarTexto(chatId, "🏷️ *Crear Categoría*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Descripción`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_categoria Alimentos, Productos no perecederos`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "DON_PRODUCTOS":
                    procesarComando(
                            chatId,
                            null,
                            "/productos"
                    );
                    break;

                case "DON_CREAR_PRODUCTO":
                    enviarTexto(chatId, "📦 *Crear Producto*\n\n" +
                            "📋 *Campos requeridos:* `Nombre, Descripción, CategoriaID, IdentificadorID`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_producto Leche, Leche entera descremada, 1, 1`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "DON_IDENTIFICADORES":
                    procesarComando(
                            chatId,
                            null,
                            "/identificadores"
                    );
                    break;

                case "DON_CREAR_IDENTIFICADOR":
                    enviarTexto(chatId, "🔖 *Crear Identificador*\n\n" +
                            "📋 *Campos requeridos:* `Tipo (QR / CODIGODEBARRAS), Descripción`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_identificador QR, Código QR estándar`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                case "DON_DONACIONES":
                    procesarComando(
                            chatId,
                            null,
                            "/donaciones"
                    );
                    break;

                case "DON_CREAR_DONACION":
                    enviarTexto(chatId, "🎁 *Registrar Donación*\n\n" +
                            "📋 *Campos requeridos:* `DonadorID, DepositoID, Descripción, ProductoID, Cantidad`\n\n" +
                            "💡 *Ejemplo para copiar y modificar:*\n" +
                            "`/crear_donacion 1, 1, Donación de leche, 1, 10`\n\n" +
                            "_(Tocá el mensaje de arriba para copiarlo, cambiá los datos y envialo)_");
                    break;

                default:
                    break;
            }
            return;
        }

        // B) MANEJO DE COMANDOS DE TEXTO
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            int messageId = update.getMessage().getMessageId();
            String mensaje = update.getMessage().getText().trim();

            if ("/start".equalsIgnoreCase(mensaje)) {
                enviarMenuInicial(chatId);
            } else {
                procesarComando(chatId, messageId, mensaje);
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

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("👤 Donador o Entidad", "ROL_DONADORES_Y_ENTIDADES"));
        row1.add(crearBoton("🎁 Donaciones", "ROL_DONACIONES"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("🚚 Logística", "ROL_LOGISTICA"));
        row2.add(crearBoton("🚀 Incentivos", "ROL_INCENTIVOS"));

        rows.add(row1);
        rows.add(row2);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuLogistica(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🚚 *Panel de Logística*\nSeleccioná la acción que necesites realizar:");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("🏢 Crear Depósito", "ACT_CREAR_DEPOSITO"));
        row1.add(crearBoton("🔍 Depósito ID", "ACT_DEPOSITO_POR_ID"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("📋 Ver Depósitos", "ACT_DEPOSITOS_TODOS"));
        row2.add(crearBoton("📊 Ver Asignaciones", "ACT_ASIGNACIONES_TODAS"));

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("📦 Gestionar Donación", "ACT_GESTIONAR_DONACION"));

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(crearBoton("⬅️ Volver al Menú Principal", "MENU_INICIAL"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuDonadoresYEntidades(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("👤 *Panel de Donadores, Entidades y Necesidades*\n¿Qué acción querés realizar?");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // ENTIDADES
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("🏢 Crear Entidad", "ACT_CREAR_ENTIDAD"));
        row1.add(crearBoton("✏️ Editar Entidad", "ACT_EDITAR_ENTIDAD"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("🔍 Entidad por ID", "ACT_ENTIDAD_POR_ID"));
        row2.add(crearBoton("📋 Ver todas las Entidades", "ACT_ENTIDADES_TODAS"));

        // NECESIDADES (¡Agregadas aquí!)
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("➕ Crear Necesidad", "ACT_CREAR_NECESIDAD"));
        row3.add(crearBoton("🔍 Necesidad por ID", "ACT_VER_NECESIDAD"));

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(crearBoton("✏️ Editar Necesidad", "ACT_EDITAR_NECESIDAD"));
        row4.add(crearBoton("🗑️ Borrar Necesidad", "ACT_BORRAR_NECESIDAD"));

        // DONADORES
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(crearBoton("📝 Registrarse", "ACT_REGISTRAR"));
        row5.add(crearBoton("📊 Mis Estadísticas", "ACT_ESTADISTICAS"));

        List<InlineKeyboardButton> row6 = new ArrayList<>();
        row6.add(crearBoton("🔍 Buscar Donador por ID", "ACT_DONADOR_POR_ID"));
        row6.add(crearBoton("📋 Ver Todos los Donadores", "ACT_DONADORES_TODOS"));

        // VOLVER
        List<InlineKeyboardButton> row7 = new ArrayList<>();
        row7.add(crearBoton("⬅️ Volver al Menú Principal", "MENU_INICIAL"));

        // Agregamos todas las filas a la estructura
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);
        rows.add(row6);
        rows.add(row7);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuDonaciones(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(
                "🎁 *Panel de Donaciones*\n" +
                        "Seleccioná la operación:"
        );
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // CATEGORÍAS
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("🏷️ Categorías", "DON_CATEGORIAS"));
        row1.add(crearBoton("➕ Crear Categoría", "DON_CREAR_CATEGORIA"));

        // PRODUCTOS
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("📦 Productos", "DON_PRODUCTOS"));
        row2.add(crearBoton("➕ Crear Producto", "DON_CREAR_PRODUCTO"));

        // IDENTIFICADORES
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("🔖 Identificadores", "DON_IDENTIFICADORES"));
        row3.add(crearBoton("➕ Crear Identificador", "DON_CREAR_IDENTIFICADOR"));

        // DONACIONES
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(crearBoton("🎁 Ver Donaciones", "DON_DONACIONES"));
        row4.add(crearBoton("➕ Registrar Donación", "DON_CREAR_DONACION"));

        // VOLVER
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        row5.add(crearBoton("⬅️ Volver", "MENU_INICIAL"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        rows.add(row5);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        ejecutarMensaje(message);
    }

    private void mostrarSubmenuIncentivos(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🚀 *Panel de Incentivos*\nSeleccioná la acción que necesites realizar:");
        message.setParseMode(ParseMode.MARKDOWN);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("➕ Crear Insignia", "ACT_CREAR_INSIGNIA"));
        row1.add(crearBoton("🔍 Insignia por ID", "ACT_INSIGNIA_POR_ID"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(crearBoton("➕ Crear Misión", "ACT_CREAR_MISION"));
        row2.add(crearBoton("🔍 Misión por ID", "ACT_MISION_POR_ID"));

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(crearBoton("📋 Ver Insignias", "ACT_INSIGNIAS_TODAS"));
        row3.add(crearBoton("📋 Ver Misiones", "ACT_MISIONES_TODAS"));

        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(crearBoton("🏅 Asignar Insignia", "ACT_ASIGNAR_INSIGNIA"));
        row4.add(crearBoton("🎯 Asignar Misión", "ACT_ASIGNAR_MISION"));

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

    private void procesarComando(long chatId, Integer messageId, String mensaje) {
        String[] partes = mensaje.split(" ", 2);
        String comando = partes[0].toLowerCase();
        String arg = partes.length > 1 ? partes[1].trim() : "";

        String respuesta;
        try {
            switch (comando) {
                // =============================================================
                // COMANDOS DE UTILIDAD CHAT
                // =============================================================
                case "/limpiar":
                case "/borrar":
                    borrarUltimosMensajes(chatId, messageId, 50);
                    enviarMenuInicial(chatId);
                    return;

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

                    respuesta = (donador != null)
                            ? formatearDonador(donador)
                            : "❌ No se encontró ningún donador con el ID *" + idBuscado + "*";
                    break;

                case "/donadores_todos":
                    List<DonadorDTO> donadores = this.fachada.buscarTodosLosDonadores();
                    respuesta = formatearListaDonadores(donadores);
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
                    String[] camposEditEnt = arg.split(",");
                    if (camposEditEnt.length < 5) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/editar_entidad ID, Razón Social, Domicilio, Teléfono, Correo`"
                        );
                    }

                    Integer idEntEdit = parsearId(camposEditEnt[0].trim());
                    String rsEdit = camposEditEnt[1].trim();
                    String domEdit = camposEditEnt[2].trim();
                    String telEdit = camposEditEnt[3].trim();
                    String corEdit = camposEditEnt[4].trim();

                    respuesta = this.fachada.editarEntidad(idEntEdit, rsEdit, domEdit, telEdit, corEdit);
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
                                "Faltan datos. Formato esperado:\n`/crear_necesidad EntidadID, Urgencia, Descripción, Cantidad, ProductoID, Tipo`\n\nEjemplo:\n`/crear_necesidad 1, 3, Leche en polvo, 50, 1, RECURRENTE`"
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
                    String[] camposEditNec = arg.split(",");
                    if (camposEditNec.length < 3) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/editar_necesidad ID, Urgencia, Descripción`\n\n" +
                                        "Ejemplo:\n`/editar_necesidad 5, 4, Leche Larga Vida - URGENTE`"
                        );
                    }

                    Integer idNecEdit = parsearId(camposEditNec[0].trim());
                    Integer urgenciaEdit = parsearId(camposEditNec[1].trim());
                    String descEdit = camposEditNec[2].trim();

                    if (descEdit.isEmpty()) {
                        throw new IllegalArgumentException("La descripción no puede estar vacía.");
                    }

                    respuesta = this.fachada.editarNecesidad(
                            idNecEdit,
                            urgenciaEdit,
                            descEdit
                    );
                    break;

                case "/borrar_necesidad":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/borrar_necesidad 5`");
                    Integer idBorrar = parsearId(arg);
                    respuesta = this.fachada.borrarNecesidadPorID(idBorrar);
                    break;

                case "/necesidad":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/necesidad 5`");
                    respuesta = this.fachada.buscarNecesidadPorId(parsearId(arg));
                    break;

                // =============================================================
                // COMANDOS LOGÍSTICA
                // =============================================================
                case "/crear_deposito":
                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/crear_deposito Nombre, Dirección, Capacidad, TipoAlgoritmoEnum`"
                        );
                    }
                    respuesta = this.fachada.crearDeposito(arg);
                    break;

                case "/deposito":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID. Ejemplo: `/deposito 1`");
                    respuesta = this.fachada.buscarDepositoPorId(parsearId(arg));
                    break;

                case "/depositos_todos":
                    respuesta = this.fachada.obtenerDepositos();
                    break;

                case "/asignaciones_todas":
                    respuesta = this.fachada.obtenerAsignaciones();
                    break;

                case "/gestionar_donacion":
                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/gestionar_donacion DepositoID, DonacionID, ProductoID, Cantidad`"
                        );
                    }
                    respuesta = this.fachada.gestionarDonacion(arg);
                    break;

                // =============================================================
                // COMANDOS INCENTIVOS
                // =============================================================
                case "/insignias_todas":
                    List<InsigniaDTO> insignias = this.fachada.getAllInsignias();
                    respuesta = formatearListaInsignias(insignias);
                    break;

                case "/misiones_todas":
                    List<MisionDTO> misiones = this.fachada.getAllMisiones();
                    respuesta = formatearListaMisiones(misiones);
                    break;

                case "/asignar_insignia":
                    String[] camposIns = arg.split(",");
                    if (camposIns.length < 2) {
                        throw new IllegalArgumentException("Formato esperado: `/asignar_insignia DonadorID, InsigniaID`");
                    }
                    String donIdIns = camposIns[0].trim();
                    String insId = camposIns[1].trim();
                    InsigniaDTO insigniaDTO = this.fachada.getInsignia(insId);
                    if (insigniaDTO == null) throw new IllegalArgumentException("Insignia no encontrada.");
                    this.fachada.asignarInsigniaADonador(donIdIns, insigniaDTO);
                    respuesta = "✅ *¡Insignia asignada con éxito al donador " + donIdIns + "!*";
                    break;

                case "/asignar_mision":
                    String[] camposMis = arg.split(",");
                    if (camposMis.length < 2) {
                        throw new IllegalArgumentException("Formato esperado: `/asignar_mision DonadorID, MisionID`");
                    }
                    String donIdMis = camposMis[0].trim();
                    String misId = camposMis[1].trim();
                    MisionDTO misionDTO = this.fachada.getMision(misId);
                    if (misionDTO == null) throw new IllegalArgumentException("Misión no encontrada.");

                    String categoriaDonador = this.fachada.categoriaActualDeDonador(donIdMis);
                    if (!misionDTO.categoriaInicio().name().equalsIgnoreCase(categoriaDonador)) {
                        throw new IllegalStateException("Error 409: La categoría inicial de la misión no coincide con la del donador.");
                    }

                    this.fachada.asignarMisionADonador(donIdMis, misionDTO);
                    respuesta = "✅ *¡Misión asignada con éxito al donador " + donIdMis + "!*";
                    break;

                case "/mision_curso":
                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException("Falta el ID del donador. Ejemplo: `/mision_curso 1`");
                    }

                    MisionDTO misionCurso = this.fachada.getMisionEnCursoDeDonador(arg);

                    respuesta = (misionCurso != null)
                            ? "🎯 *¡Misión en curso del donador!* 🎯\n\n" +
                            "📌 *Nombre:* " + misionCurso.nombre() + "\n" +
                            "🆔 *ID:* `" + misionCurso.id() + "`\n" +
                            "🔹 *Tipo:* " + misionCurso.tipo() + "\n" +
                            "🏆 *Insignia ID:* " + misionCurso.insigniaID() + "\n" +
                            "📈 *Categorías:* " + misionCurso.categoriaInicio() + " ➡️ " + misionCurso.categoriaFin()
                            : "⚠️ El donador no tiene ninguna misión en curso actualmente.";
                    break;

                case "/quitar_mision":
                    if (arg.isEmpty()) throw new IllegalArgumentException("Falta el ID del donador. Ejemplo: `/quitar_mision 1`");
                    this.fachada.quitarMisionDeDonador(arg);
                    respuesta = "✅ *Misión en curso cancelada correctamente para el donador ID " + arg + ".*";
                    break;

                case "/crear_mision":
                    String[] camposMision = arg.split(",");
                    if (camposMision.length < 5) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/crear_mision Nombre, InsigniaID, CategoriaInicio, CategoriaFin, Tipo`\n\n" +
                                        "Ejemplo:\n`/crear_mision Misión Bronce, 1, BRONCE, PLATA, ACUMULATIVA`"
                        );
                    }

                    String nombreMision = camposMision[0].trim();
                    String insigniaIDMision = camposMision[1].trim();

                    CategoriaDonadorEnum catInicio;
                    CategoriaDonadorEnum catFin;
                    TipoMisionEnum tipoMision;

                    try {
                        catInicio = CategoriaDonadorEnum.valueOf(camposMision[2].trim().toUpperCase());
                        catFin = CategoriaDonadorEnum.valueOf(camposMision[3].trim().toUpperCase());
                        tipoMision = TipoMisionEnum.valueOf(camposMision[4].trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Las categorías o el tipo de misión son inválidos.");
                    }

                    if (nombreMision.isEmpty()) {
                        throw new IllegalArgumentException("El Nombre no puede estar vacío.");
                    }

                    MisionDTO nuevaMision = new MisionDTO(
                            null,
                            nombreMision,
                            insigniaIDMision,
                            catInicio,
                            catFin,
                            tipoMision
                    );

                    MisionDTO misionCreada = this.fachada.agregarMision(nuevaMision);

                    respuesta = "✅ *¡Misión creada exitosamente!*\n\n" +
                            "🆔 *ID Asignado:* `" + misionCreada.id() + "`\n" +
                            "🎯 *Nombre:* " + misionCreada.nombre() + "\n" +
                            "🏆 *Insignia ID:* " + misionCreada.insigniaID() + "\n" +
                            "📈 *Transición:* " + misionCreada.categoriaInicio() + " ➡️ " + misionCreada.categoriaFin() + "\n" +
                            "🔹 *Tipo:* " + misionCreada.tipo();
                    break;

                case "/mision":
                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException("Falta el ID. Ejemplo: `/mision 1`");
                    }

                    MisionDTO misionBuscada = this.fachada.getMision(arg.trim());

                    respuesta = (misionBuscada != null)
                            ? "🎯 *Información de la Misión* 🎯\n\n" +
                            "🆔 *ID:* `" + misionBuscada.id() + "`\n" +
                            "📌 *Nombre:* " + misionBuscada.nombre() + "\n" +
                            "🏆 *Insignia ID:* " + misionBuscada.insigniaID() + "\n" +
                            "📈 *Categorías:* " + misionBuscada.categoriaInicio() + " ➡️ " + misionBuscada.categoriaFin() + "\n" +
                            "🔹 *Tipo:* " + misionBuscada.tipo()
                            : "❌ No se encontró ninguna misión con el ID *" + arg.trim() + "*";
                    break;


                case "/crear_insignia":
                    String[] camposInsig = arg.split(",");
                    if (camposInsig.length < 3) {
                        throw new IllegalArgumentException(
                                "Faltan datos. Formato esperado:\n`/crear_insignia Nombre, Descripción, Criterio`"
                        );
                    }
                    InsigniaDTO nuevaInsig = new InsigniaDTO(null, camposInsig[0].trim(), camposInsig[1].trim());
                    InsigniaDTO creadaInsig = this.fachada.agregarInsignia(nuevaInsig);
                    respuesta = "✅ *Insignia creada con éxito!*\n🆔 ID: `" + creadaInsig.id() + "`\n📛 Nombre: " + creadaInsig.nombre();
                    break;



                    // =============================================================
                    // COMANDOS DONACIONES
                    // =============================================================

                case "/crear_categoria":

                    String[] camposCategoria = arg.split(",");

                    if (camposCategoria.length < 2) {
                        throw new IllegalArgumentException(
                                "Formato esperado: " +
                                        "`/crear_categoria Nombre, Descripción`"
                        );
                    }

                    String nombreCategoria =
                            camposCategoria[0].trim();

                    String descripcionCategoria =
                            camposCategoria[1].trim();

                    respuesta = this.fachada.crearCategoria(
                            nombreCategoria,
                            descripcionCategoria
                    );

                    break;


                case "/categorias":

                    respuesta =
                            this.fachada.obtenerCategorias();

                    break;


                case "/borrar_categoria":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/borrar_categoria 1`"
                        );
                    }

                    respuesta =
                            this.fachada.borrarCategoria(
                                    arg.trim()
                            );

                    break;


                case "/crear_identificador":

                    String[] camposIdentificador =
                            arg.split(",");

                    if (camposIdentificador.length < 2) {
                        throw new IllegalArgumentException(
                                "Formato esperado: " +
                                        "`/crear_identificador QR, Descripción`"
                        );
                    }

                    String tipoIdentificador =
                            camposIdentificador[0].trim();

                    String descripcionIdentificador =
                            camposIdentificador[1].trim();

                    respuesta =
                            this.fachada.crearIdentificador(
                                    tipoIdentificador,
                                    descripcionIdentificador
                            );

                    break;


                case "/identificadores":

                    respuesta =
                            this.fachada.obtenerIdentificadores();

                    break;


                case "/borrar_identificador":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/borrar_identificador 1`"
                        );
                    }

                    respuesta =
                            this.fachada.borrarIdentificador(
                                    arg.trim()
                            );

                    break;


                case "/crear_producto":

                    String[] camposProducto =
                            arg.split(",");

                    if (camposProducto.length < 4) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/crear_producto Nombre, Descripción, CategoriaID, IdentificadorID`"
                        );
                    }

                    String nombreProducto =
                            camposProducto[0].trim();

                    String descripcionProducto =
                            camposProducto[1].trim();

                    String categoriaProducto =
                            camposProducto[2].trim();

                    String identificadorProducto =
                            camposProducto[3].trim();

                    respuesta =
                            this.fachada.crearProducto(
                                    nombreProducto,
                                    descripcionProducto,
                                    categoriaProducto,
                                    identificadorProducto
                            );

                    break;


                case "/productos":

                    respuesta =
                            this.fachada.obtenerProductos();

                    break;


                case "/producto":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/producto 1`"
                        );
                    }

                    respuesta =
                            this.fachada.buscarProductoPorId(
                                    arg.trim()
                            );

                    break;


                case "/editar_producto":

                    String[] camposEditarProducto =
                            arg.split(",");

                    if (camposEditarProducto.length < 5) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/editar_producto ID, Nombre, Descripción, CategoriaID, IdentificadorID`"
                        );
                    }

                    respuesta =
                            this.fachada.modificarProducto(
                                    camposEditarProducto[0].trim(),
                                    camposEditarProducto[1].trim(),
                                    camposEditarProducto[2].trim(),
                                    camposEditarProducto[3].trim(),
                                    camposEditarProducto[4].trim()
                            );

                    break;


                case "/borrar_producto":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/borrar_producto 1`"
                        );
                    }

                    respuesta =
                            this.fachada.borrarProducto(
                                    arg.trim()
                            );

                    break;


                case "/crear_donacion":

                    String[] camposDonacion =
                            arg.split(",");

                    if (camposDonacion.length < 5) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/crear_donacion DonadorID, DepositoID, Descripción, ProductoID, Cantidad`"
                        );
                    }

                    String donadorID =
                            camposDonacion[0].trim();

                    String depositoID =
                            camposDonacion[1].trim();

                    String descripcionDonacion =
                            camposDonacion[2].trim();

                    String productoIDDonacion =
                            camposDonacion[3].trim();

                    Integer cantidadDonacion =
                            parsearId(
                                    camposDonacion[4].trim()
                            );

                    respuesta =
                            this.fachada.registrarDonacion(
                                    donadorID,
                                    depositoID,
                                    descripcionDonacion,
                                    productoIDDonacion,
                                    cantidadDonacion
                            );

                    break;


                case "/donaciones":

                    respuesta =
                            this.fachada.obtenerDonaciones();

                    break;


                case "/donacion":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/donacion 1`"
                        );
                    }

                    respuesta =
                            this.fachada.buscarDonacionPorId(
                                    arg.trim()
                            );

                    break;


                case "/borrar_donacion":

                    if (arg.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Falta el ID. Ejemplo: `/borrar_donacion 1`"
                        );
                    }

                    respuesta =
                            this.fachada.borrarDonacion(
                                    arg.trim()
                            );

                    break;


                case "/buscar_donaciones":

                    String[] camposBusqueda =
                            arg.split(",");

                    if (camposBusqueda.length < 2) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/buscar_donaciones DonadorID, YYYY-MM-DD`"
                        );
                    }

                    String donadorBusqueda =
                            camposBusqueda[0].trim();

                    LocalDate fechaBusqueda;

                    try {
                        fechaBusqueda =
                                LocalDate.parse(
                                        camposBusqueda[1].trim()
                                );
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "La fecha debe tener formato YYYY-MM-DD."
                        );
                    }

                    respuesta =
                            this.fachada.buscarDonacionesPorDonadorYFecha(
                                    donadorBusqueda,
                                    fechaBusqueda
                            );

                    break;


                case "/cambiar_estado_donacion":

                    String[] camposEstado =
                            arg.split(",");

                    if (camposEstado.length < 2) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/cambiar_estado_donacion ID, ESTADO`"
                        );
                    }

                    respuesta =
                            this.fachada.cambiarEstadoDonacion(
                                    camposEstado[0].trim(),
                                    camposEstado[1].trim()
                            );

                    break;


                case "/queja_donacion":

                    String[] camposQueja =
                            arg.split(",");

                    if (camposQueja.length < 2) {
                        throw new IllegalArgumentException(
                                "Formato esperado:\\n" +
                                        "`/queja_donacion ID, Descripción de la queja`"
                        );
                    }

                    respuesta =
                            this.fachada.registrarQueja(
                                    camposQueja[0].trim(),
                                    camposQueja[1].trim()
                            );

                    break;


                case "/reset_donaciones":

                    respuesta =
                            this.fachada.resetearDonaciones();

                    break;




                default:
                    respuesta = "Comando no reconocido. Enviá /start para abrir el menú principal o /limpiar para reiniciar la pantalla.";
                    break;
            }
        } catch (Exception e) {
            respuesta = "❌ *Error:* " + e.getMessage();
        }

        enviarTexto(chatId, respuesta);
    }



    // --- MÉTODOS AUXILIARES Y FORMATEO ---

    private void borrarUltimosMensajes(long chatId, Integer ultimoMessageId, int cantidad) {
        if (ultimoMessageId == null) return;
        for (int i = 0; i < cantidad; i++) {
            try {
                DeleteMessage delete = new DeleteMessage();
                delete.setChatId(String.valueOf(chatId));
                delete.setMessageId(ultimoMessageId - i);
                execute(delete);
            } catch (Exception ignored) {
                // Omite errores si el mensaje ya no existe o es demasiado viejo
            }
        }
    }

    private String formatearDonador(DonadorDTO d) {
        return "👤 *Donador Encontrado*\n\n" +
                "🆔 *ID:* `" + d.id() + "`\n" +
                "👤 *Nombre:* " + d.nombre() + " " + d.apellido() + "\n" +
                "🎂 *Edad:* " + d.edad() + " años\n" +
                "📧 *Email:* " + d.email() + "\n" +
                "📄 *DNI:* " + d.nroDocumento() + "\n" +
                "🏠 *Domicilio:* " + d.domicilio() + "\n" +
                "✅ *Estado:* " + (d.estado() != null ? d.estado() : "Sin estado") + "\n" +
                "🏷️ *Categoría:* " + (d.categoria() != null ? d.categoria() : "Sin categoría");
    }

    private String formatearListaDonadores(List<DonadorDTO> donadores) {
        if (donadores == null || donadores.isEmpty()) {
            return "⚠️ *No hay donadores registrados en el sistema.*";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 *Lista de Donadores (Total: ").append(donadores.size()).append(")*\n");
        sb.append("─────────────────────────\n\n");

        for (DonadorDTO d : donadores) {
            sb.append("👤 *").append(d.nombre()).append(" ").append(d.apellido()).append("* (ID: `").append(d.id()).append("`)\n")
                    .append("📄 *DNI:* ").append(d.nroDocumento()).append(" | 📧 `").append(d.email()).append("`\n")
                    .append("🏠 *Domicilio:* ").append(d.domicilio()).append("\n")
                    .append("✅ *Estado:* ").append(d.estado() != null ? d.estado() : "Sin estado").append("\n")
                    .append("🏷️ *Categoría:* ").append(d.categoria() != null ? d.categoria() : "Sin categoría").append("\n\n")
                    .append("─────────────────────────\n\n");
        }

        return sb.toString().trim();
    }

    private String formatearListaInsignias(List<InsigniaDTO> insignias) {
        if (insignias == null || insignias.isEmpty()) return "⚠️ *No hay insignias registradas.*";
        StringBuilder sb = new StringBuilder("📋 *Lista de Insignias (" + insignias.size() + ")*\n\n");
        for (InsigniaDTO i : insignias) {
            sb.append("• *").append(i.id()).append("* - ")
                    .append(i.nombre()).append(" - ") // <-- Quitamos el * suelto de acá
                    .append(i.descripcion() != null ? i.descripcion() : "Sin descripción").append("\n");
        }
        return sb.toString();
    }

    private String formatearListaMisiones(List<MisionDTO> misiones) {
        if (misiones == null || misiones.isEmpty()) {
            return "⚠️ *No hay misiones registradas.*";
        }

        StringBuilder sb = new StringBuilder("📋 *Lista de Misiones (" + misiones.size() + ")*\n\n");

        for (MisionDTO m : misiones) {
            sb.append("• *").append(m.nombre()).append("* (ID: `").append(m.id()).append("`)\n")
                    .append("  🔹 *Tipo:* ").append(m.tipo()).append("\n")
                    .append("  🔹 *Insignia ID:* ").append(m.insigniaID()).append("\n")
                    .append("  🔹 *Categoría:* ").append(m.categoriaInicio()).append(" ➡️ ").append(m.categoriaFin()).append("\n\n");
        }

        return sb.toString();
    }

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
            System.err.println("❌ ERROR ENVIANDO MENSAJE A TELEGRAM:");
            e.printStackTrace();
        }
    }

//    private void ejecutarMensaje(SendMessage message) {
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
}