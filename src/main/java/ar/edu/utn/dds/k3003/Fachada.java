package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.AsignacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.PaqueteDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.componentes.DonacionesClient;
import ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.componentes.IncentivosClient;
import ar.edu.utn.dds.k3003.componentes.LogisticaClient;
import ar.edu.utn.dds.k3003.componentes.Request.AsignacionRequest;
import ar.edu.utn.dds.k3003.componentes.Request.DepositoRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class Fachada {

  private final DonadoresYEntidadesClient donadoresYEntidadesClient;

  private final LogisticaClient logisticaClient;

  private final IncentivosClient incentivosClient;

  private final DonacionesClient donacionesClient;

  public Fachada(
          DonadoresYEntidadesClient donadoresYEntidadesClient,
          LogisticaClient logisticaClient,
          IncentivosClient incentivosClient,
          DonacionesClient donacionesClient) {

    this.donadoresYEntidadesClient = donadoresYEntidadesClient;
    this.logisticaClient = logisticaClient;
    this.incentivosClient = incentivosClient;
    this.donacionesClient = donacionesClient;
  }

  // =========================================================================
  // Módulo Donadores y Entidades
  // =========================================================================



  // -----------------------------------------
  // DONADORES
  // -----------------------------------------


  public DonadorDTO registrarDonador(DonadorDTO donadorDTO) {
    return this.donadoresYEntidadesClient.guardarDonador(donadorDTO);
  }

  public String obtenerEstadisticasDonador(Integer id) {
    DonadorStatsDTO stats = this.donadoresYEntidadesClient.obtenerEstadisticas(id);
    if (stats == null) {
      return "❌ No se encontraron estadísticas para el donador ID " + id;
    }

    String insignias = (stats.insigniasID() != null && !stats.insigniasID().isEmpty())
            ? String.join(", ", stats.insigniasID())
            : "Sin insignias";

    String mision = (stats.misionActualID() != null) ? stats.misionActualID() : "Sin misión activa";

    return "📊 *Estadísticas del Donador ID " + stats.id() + "*\n\n" +
            "👤 *Nombre:* " + stats.nombre() + " " + stats.apellido() + "\n" +
            "🎂 *Edad:* " + stats.edad() + " años\n" +
            "📌 *Estado:* " + stats.estado() + "\n" +
            "🏆 *Categoría:* " + stats.categoria() + "\n" +
            "🎯 *Misión Actual:* " + mision + "\n" +
            "🏅 *Insignias:* " + insignias;
  }

  public DonadorDTO buscarDonadorPorId(Integer id) {
    return this.donadoresYEntidadesClient.buscarDonadorPorId(id);
  }

  public List<DonadorDTO> buscarTodosLosDonadores() {
    return this.donadoresYEntidadesClient.buscarTodosLosDonadores();
  }

  //-----------------------------------------
  // ENTIDADES BENEFICAS
  // -----------------------------------------

  public String editarEntidad(Integer id, String razonSocial, String domicilio, String telefono, String correo) {
    try {
      EntidadBeneficaDTO dto = new EntidadBeneficaDTO(id, razonSocial, domicilio, telefono, correo);
      EntidadBeneficaDTO editada = this.donadoresYEntidadesClient.editarEntidad(id, dto);

      if (editada == null) {
        return "❌ *Error:* No existe ninguna entidad con el ID *" + id + "*.";
      }

      return "✏️ *Entidad Actualizada con Éxito*\n\n" +
              "🏢 *ID:* `" + editada.id() + "`\n" +
              "📛 *Razón Social:* " + editada.razonSocial() + "\n" +
              "📍 *Domicilio:* " + editada.domicilio() + "\n" +
              "📞 *Teléfono:* " + editada.telefono() + "\n" +
              "✉️ *Correo:* " + editada.correo();
    } catch (Exception e) {
      return "❌ *Error:* No existe ninguna entidad registrada con el ID *" + id + "*.";
    }
  }

  public String crearEntidad(String razonSocial, String domicilio, String telefono, String correo) {
    EntidadBeneficaDTO dto = new EntidadBeneficaDTO(null, razonSocial, domicilio, telefono, correo);
    EntidadBeneficaDTO creada = this.donadoresYEntidadesClient.guardarEntidad(dto);

    if (creada == null) {
      return "❌ Error: No se pudo crear la entidad en el microservicio.";
    }

    return "✅ *Entidad Creada con Éxito*\n\n" +
            "🏢 *ID:* `" + creada.id() + "`\n" +
            "📛 *Razón Social:* " + creada.razonSocial() + "\n" +
            "📍 *Domicilio:* " + creada.domicilio() + "\n" +
            "📞 *Teléfono:* " + creada.telefono() + "\n" +
            "✉️ *Correo:* " + creada.correo();
  }

  public String buscarEntidadPorId(Integer id) {
    EntidadBeneficaDTO entidad = this.donadoresYEntidadesClient.buscarEntidadPorId(id);
    if (entidad == null) {
      return "❌ No se encontró la entidad con ID " + id;
    }

    String domicilio = entidad.domicilio() != null ? entidad.domicilio() : "No especificado";
    String telefono = entidad.telefono() != null ? entidad.telefono() : "No especificado";
    String correo = entidad.correo() != null ? entidad.correo() : "No especificado";

    return "🏢 *Detalle de la Entidad*\n\n" +
            "🆔 *ID:* `" + entidad.id() + "`\n" +
            "📛 *Razón Social:* " + entidad.razonSocial() + "\n" +
            "📍 *Domicilio:* " + domicilio + "\n" +
            "📞 *Teléfono:* " + telefono + "\n" +
            "✉️ *Correo:* " + correo;
  }

  public String buscarTodasLasEntidades() {
    List<EntidadBeneficaDTO> entidades = this.donadoresYEntidadesClient.buscarTodasLasEntidades();

    if (entidades == null || entidades.isEmpty()) {
      return "📭 No hay entidades registradas en el sistema.";
    }

    StringBuilder sb = new StringBuilder("📋 *Lista de Entidades Benéficas (Total: " + entidades.size() + ")*\n");
    sb.append("─────────────────────────\n\n");

    for (EntidadBeneficaDTO ent : entidades) {
      String domicilio = ent.domicilio() != null ? ent.domicilio() : "No especificado";
      String telefono = ent.telefono() != null ? ent.telefono() : "No especificado";
      String correo = ent.correo() != null ? ent.correo() : "No especificado";

      sb.append("🏢 *").append(ent.razonSocial()).append("* (ID: `").append(ent.id()).append("`)\n")
              .append("📍 *Domicilio:* ").append(domicilio).append("\n")
              .append("📞 *Teléfono:* ").append(telefono).append("\n")
              .append("✉️ *Correo:* ").append(correo).append("\n\n")
              .append("─────────────────────────\n\n");
    }

    return sb.toString().trim();
  }

  // =========================================================================
  // Módulo Necesidades
  // =========================================================================

  public String crearNecesidad(String entidadId, Integer nivelDeUrgencia, String descripcion, Integer cantidadObjetivo, String productoSolicitadoId, TipoNecesidadMaterialEnum tipo) {
    NecesidadMaterialDTO dto = new NecesidadMaterialDTO(
            null,                  // id
            entidadId,             // entidadID
            nivelDeUrgencia,       // nivelDeUrgencia
            descripcion,           // descripcion
            cantidadObjetivo,      // cantidadObjetivo
            0,                     // cantidadRecibida (agregado para completar los 8 campos)
            productoSolicitadoId,  // productoSolicitadoID
            tipo                   // tipo
    );

    NecesidadMaterialDTO creada = this.donadoresYEntidadesClient.guardarNecesidad(dto);

    if (creada == null) {
      return "❌ Error: No se pudo registrar la necesidad en el microservicio.";
    }

    int cantidadRecibida = (creada.cantidadRecibida() != null) ? creada.cantidadRecibida() : 0;

    String infoStock = (cantidadRecibida > 0)
            ? "📦 *Stock Asignado:* " + cantidadRecibida + " unidades"
            : "⚠️ *Stock Asignado:* 0 unidades (Sin stock disponible en Logística por el momento)";

    return "✅ *Necesidad Registrada con Éxito*\n\n" +
            "🆔 *ID Necesidad:* `" + creada.id() + "`\n" +
            "🏢 *Entidad ID:* " + creada.entidadID() + "\n" +
            "🔥 *Urgencia:* " + creada.nivelDeUrgencia() + "\n" +
            "📝 *Descripción:* " + creada.descripcion() + "\n" +
            "🔢 *Cantidad Objetivo:* " + creada.cantidadObjetivo() + "\n" +
            "📦 *Producto ID:* " + creada.productoSolicitadoID() + "\n" +
            "🏷️ *Tipo:* " + creada.tipo() + "\n\n" +
            infoStock;
  }



  public String editarNecesidad(Integer id, Integer nivelDeUrgencia, String descripcion) {
    try {
      // Armamos el DTO pasando únicamente los campos que modificamos
      NecesidadMaterialDTO dto = new NecesidadMaterialDTO(
              id,                     // 1. id (para identificar)
              null,                   // 2. entidadID (no se toca)
              nivelDeUrgencia,        // 3. nivelDeUrgencia
              descripcion,            // 4. descripcion
              null,                   // 5. cantidadObjetivo (no se toca)
              null,                   // 6. cantidadRecibida (no se toca)
              null,                   // 7. productoSolicitadoID (no se toca)
              null                    // 8. tipo (no se toca)
      );

      NecesidadMaterialDTO editada = this.donadoresYEntidadesClient.editarNecesidad(id, dto);

      if (editada == null) {
        return "❌ *Error:* No existe ninguna necesidad con el ID *" + id + "*.";
      }

      return "✏️ *Necesidad Actualizada con Éxito*\n\n" +
              "🆔 *ID:* `" + editada.id() + "`\n" +
              "🔥 *Nueva Urgencia:* " + editada.nivelDeUrgencia() + "\n" +
              "📝 *Nueva Descripción:* " + editada.descripcion();

    } catch (Exception e) {
      return "❌ *Error:* No existe ninguna necesidad registrada con el ID *" + id + "*.";
    }
  }



  public String borrarNecesidadPorID(Integer id) {
    try {
      this.donadoresYEntidadesClient.borrarNecesidad(id);
      return "✅ Necesidad ID *" + id + "* eliminada correctamente.";
    } catch (Exception e) {
      return "❌ *Error:* No existe ninguna necesidad registrada con el ID *" + id + "* para eliminar.";
    }
  }

  public String buscarNecesidadPorId(Integer id) {
    NecesidadMaterialDTO necesidad = this.donadoresYEntidadesClient.buscarNecesidadPorId(id);

    if (necesidad == null) {
      return "❌ No se encontró la necesidad con ID " + id;
    }

    return "📦 *Detalle de la Necesidad*\n\n" +
            "🆔 *ID:* `" + necesidad.id() + "`\n" +
            "🏢 *Entidad ID:* " + necesidad.entidadID() + "\n" +
            "🔥 *Urgencia:* " + necesidad.nivelDeUrgencia() + "\n" +
            "📝 *Descripción:* " + necesidad.descripcion() + "\n" +
            "🔢 *Cantidad Objetivo:* " + necesidad.cantidadObjetivo() + "\n" +
            "📦 *Producto ID:* " + necesidad.productoSolicitadoID() + "\n" +
            "🏷️ *Tipo:* " + necesidad.tipo();
  }


// =========================================================================
// Módulo LOGISTICA
// =========================================================================

  public String crearDeposito(String arg) {
    String[] campos = arg.split(",");
    if (campos.length < 4) {
      throw new IllegalArgumentException("Formato esperado:\n`/crear_deposito Nombre, Dirección, CapacidadMáxima, TipoAlgoritmo`");
    }
    String nombre = campos[0].trim();
    String direccion = campos[1].trim();
    Integer capacidad = parsearId(campos[2].trim());
    TipoAlgoritmoEnum algoritmo = TipoAlgoritmoEnum.valueOf(campos[3].trim().toUpperCase());

    // Creamos el request con el nombre de campo correcto que espera Logística
    DepositoRequest nuevoRequest = new DepositoRequest(nombre, direccion, capacidad, algoritmo);

    // El cliente HTTP le pega al endpoint mandando el request
    DepositoDTO creado = this.logisticaClient.agregarDeposito(nuevoRequest);

    return "✅ *Depósito creado con éxito!*\n\n" +
            "🆔 *ID:* `" + creado.id() + "`\n" +
            "🏢 *Nombre:* " + creado.nombre() + "\n" +
            "📍 *Dirección:* " + creado.direccion() + "\n" +
            "📦 *Capacidad:* " + creado.capacidadMaxima();
  }

  public String gestionarDonacion(String arg) {
    String[] campos = arg.split(",");
    if (campos.length < 4) {
      throw new IllegalArgumentException("Formato esperado:\n`/gestionar_donacion DepositoID, DonacionID, ProductoID, Cantidad`");
    }
    String depositoId = campos[0].trim();
    String donacionId = campos[1].trim();
    String productoId = campos[2].trim();
    Integer cantidad = parsearId(campos[3].trim());

    this.logisticaClient.gestionarDonacion(depositoId, donacionId, productoId, cantidad);
    return "📦 *Donación enviada a cola de procesamiento con éxito.*";
  }


  private String formatearDeposito(DepositoDTO d) {

    //si es markdown dessecomentar esto
    String algoritmoStr = d.algoritmo() != null ? d.algoritmo().toString().replace("_", "\\_") : "N/D";
    return "🏢 Depósito\n\n" +
            "🆔 ID: " + d.id() + "\n" +
            "📛 Nombre: " + d.nombre() + "\n" +
            "📍 Dirección: " + d.direccion() + "\n" +
            "📊 Capacidad Max: " + d.capacidadMaxima() + "\n" +
            "⚙️ Algoritmo: " + algoritmoStr;
  }

  private String formatearListaDepositos(List<DepositoDTO> depositos) {
    if (depositos == null || depositos.isEmpty()) return "⚠️ *No hay depósitos registrados.*";
    StringBuilder sb = new StringBuilder("📋 *Lista de Depósitos (" + depositos.size() + ")*\n\n");
    for (DepositoDTO d : depositos) {
      sb.append("• *").append(d.nombre()).append("* (ID: `").append(d.id()).append("`) - ").append(d.direccion()).append("\n");
    }
    return sb.toString();
  }

  private String formatearListaAsignaciones(List<AsignacionRequest> asignaciones) {
    if (asignaciones == null || asignaciones.isEmpty()) {
      return "⚠️ *No hay asignaciones.*";
    }

    StringBuilder sb = new StringBuilder("📋 *Lista de Asignaciones (" + asignaciones.size() + ")*\n\n");

    // Formateador para que la fecha quede tipo: 29/09/2026 23:44
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    for (AsignacionRequest a : asignaciones) {
      String estadoStr = a.estado() != null ? a.estado().toString().replace("_", "\\_") : "N/D";
      String fechaStr = a.fecha() != null ? a.fecha().format(formatter) : "N/D";

      sb.append("• *ID:* `").append(a.id()).append("`\n")
              .append("  📌 *Necesidad:* ").append(a.necesidadID()).append("\n")
              .append("  📅 *Fecha:* ").append(fechaStr).append("\n")
              .append("  📊 *Estado:* ").append(estadoStr).append("\n")
              .append("  🔢 *Cantidad:* ").append(a.cantidad()).append("\n\n");
    }

    return sb.toString();
  }

  private String formatearListaPaquetes(List<PaqueteDTO> paquetes) {
    if (paquetes == null || paquetes.isEmpty()) return "⚠️ *No hay paquetes.*";
    return "📋 *Total de paquetes en stock:* " + paquetes.size();
  }


  public String buscarDepositoPorId(Integer id) {
    DepositoDTO dep = this.logisticaClient.buscarDepositoPorId(id);
    return dep != null ? formatearDeposito(dep) : "❌ Depósito no encontrado.";
  }

  public String obtenerDepositos() {
    List<DepositoDTO> depositos = this.logisticaClient.obtenerDepositos();
    return formatearListaDepositos(depositos);
  }


  public String obtenerAsignaciones() {
    List<AsignacionRequest> asignaciones = this.logisticaClient.obtenerAsignaciones();
    return formatearListaAsignaciones(asignaciones);
  }
  // =========================================================================
  // Módulo Incentivos (Insignias y Misiones)
  // =========================================================================

  public InsigniaDTO agregarInsignia(InsigniaDTO dto) {
    return this.incentivosClient.agregarInsignia(dto);
  }

  public List<InsigniaDTO> getAllInsignias() {
    return this.incentivosClient.getAllInsignias();
  }

  public InsigniaDTO getInsignia(String id) {
    return this.incentivosClient.getInsignia(id);
  }

  public void eliminarInsignia(String id) {
    this.incentivosClient.eliminarInsignia(id);
  }

  public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO) {
    this.incentivosClient.asignarInsigniaADonador(donadorID, insigniaDTO.id());
  }

  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {
    return this.incentivosClient.getInsigniasDeDonador(donadorID);
  }

  public MisionDTO agregarMision(MisionDTO dto) {
    return this.incentivosClient.agregarMision(dto);
  }

  public List<MisionDTO> getAllMisiones() {
    return this.incentivosClient.getAllMisiones();
  }

  public MisionDTO getMision(String id) {
    return this.incentivosClient.getMision(id);
  }

  public void eliminarMision(String id) {
    this.incentivosClient.eliminarMision(id);
  }

  public String categoriaActualDeDonador(String donadorID) {
    DonadorDTO donador = this.donadoresYEntidadesClient.buscarDonadorPorId(Integer.valueOf(donadorID));
    if (donador != null && donador.categoria() != null) {
      return donador.categoria();
    }
    return "";
  }

  public void asignarMisionADonador(String donadorID, MisionDTO misionDTO) {
    this.incentivosClient.asignarMisionADonador(donadorID, misionDTO.id());
  }

  public MisionDTO getMisionEnCursoDeDonador(String donadorID) {
    return this.incentivosClient.getMisionEnCursoDeDonador(donadorID);
  }

  public void quitarMisionDeDonador(String donadorID) {
    this.incentivosClient.quitarMisionDeDonador(donadorID);
  }


  // =========================================================================
  // Métodos Auxiliares
  // =========================================================================

  private Integer parsearId(String texto) {
    try {
      return Integer.valueOf(texto.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("El valor ingresado debe ser un número entero válido.");
    }
  }


// =========================================================================
// Módulo DONACIONES
// =========================================================================

// -----------------------------------------
// CATEGORÍAS
// -----------------------------------------

  public String crearCategoria(String nombre, String descripcion) {
    CategoriaDTO dto = new CategoriaDTO(null, nombre, descripcion);
    CategoriaDTO creada = this.donacionesClient.agregarCategoria(dto);

    return "✅ *Categoría creada correctamente*\n\n" +
            "🆔 *ID:* `" + creada.id() + "`\n" +
            "📛 *Nombre:* " + creada.nombre() + "\n" +
            "📝 *Descripción:* " + creada.descripcion();
  }

  public String obtenerCategorias() {
    List<CategoriaDTO> categorias = this.donacionesClient.obtenerCategorias();

    if (categorias == null || categorias.isEmpty()) {
      return "⚠️ *No hay categorías registradas.*";
    }

    StringBuilder sb = new StringBuilder("📋 *Categorías registradas*\n\n");

    for (CategoriaDTO c : categorias) {
      sb.append("• *")
              .append(c.nombre())
              .append("* — ID: `")
              .append(c.id())
              .append("`\n")
              .append("  📝 ")
              .append(c.descripcion())
              .append("\n\n");
    }

    return sb.toString();
  }

  public String borrarCategoria(String id) {
    this.donacionesClient.eliminarCategoria(id);
    return "✅ Categoría `" + id + "` eliminada correctamente.";
  }

// -----------------------------------------
// IDENTIFICADORES
// -----------------------------------------

  public String crearIdentificador(String tipo, String descripcion) {
    TipoIdentificadorEnum tipoEnum;

    try {
      tipoEnum = TipoIdentificadorEnum.valueOf(tipo.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("El tipo debe ser QR o CODIGODEBARRAS.");
    }

    IdentificadorDTO dto = new IdentificadorDTO(null, tipoEnum, descripcion);
    IdentificadorDTO creado = this.donacionesClient.agregarIdentificador(dto);

    return "✅ *Identificador creado correctamente*\n\n" +
            "🆔 *ID:* `" + creado.id() + "`\n" +
            "🏷️ *Tipo:* " + creado.tipo() + "\n" +
            "📝 *Descripción:* " + creado.descripcion();
  }

  public String obtenerIdentificadores() {
    List<IdentificadorDTO> identificadores = this.donacionesClient.obtenerIdentificadores();

    if (identificadores == null || identificadores.isEmpty()) {
      return "⚠️ *No hay identificadores registrados.*";
    }

    StringBuilder sb = new StringBuilder("📋 *Identificadores registrados*\n\n");

    for (IdentificadorDTO i : identificadores) {
      sb.append("• *ID:* `")
              .append(i.id())
              .append("`\n")
              .append("  🏷️ *Tipo:* ")
              .append(i.tipo())
              .append("\n")
              .append("  📝 ")
              .append(i.descripcion())
              .append("\n\n");
    }

    return sb.toString();
  }

  public String borrarIdentificador(String id) {
    this.donacionesClient.eliminarIdentificador(id);
    return "✅ Identificador `" + id + "` eliminado correctamente.";
  }

// -----------------------------------------
// PRODUCTOS
// -----------------------------------------

  public String crearProducto(
          String nombre,
          String descripcion,
          String categoriaID,
          String identificadorID) {

    ProductoDTO dto = new ProductoDTO(
            null,
            nombre,
            descripcion,
            categoriaID,
            identificadorID
    );

    ProductoDTO creado = this.donacionesClient.agregarProducto(dto);
    return formatearProducto(creado);
  }

  public String obtenerProductos() {
    List<ProductoDTO> productos = this.donacionesClient.obtenerProductos();
    return formatearListaProductos(productos);
  }

  public String buscarProductoPorId(String id) {
    ProductoDTO producto = this.donacionesClient.buscarProductoPorId(id);

    if (producto == null) {
      return "❌ No se encontró el producto con ID `" + id + "`.";
    }

    return formatearProducto(producto);
  }

  public String modificarProducto(
          String id,
          String nombre,
          String descripcion,
          String categoriaID,
          String identificadorID) {

    ProductoDTO dto = new ProductoDTO(
            id,
            nombre,
            descripcion,
            categoriaID,
            identificadorID
    );

    ProductoDTO actualizado = this.donacionesClient.actualizarProducto(id, dto);

    return "✅ *Producto actualizado correctamente*\n\n" +
            formatearProducto(actualizado);
  }

  public String borrarProducto(String id) {
    this.donacionesClient.eliminarProducto(id);
    return "✅ Producto `" + id + "` eliminado correctamente.";
  }

// -----------------------------------------
// DONACIONES
// -----------------------------------------

  public String registrarDonacion(
          String donadorID,
          String depositoID,
          String descripcion,
          String productoID,
          Integer cantidad) {

    DonacionDTO dto = new DonacionDTO(
            null,
            donadorID,
            depositoID,
            descripcion,
            productoID,
            cantidad,
            EstadoDonacionEnum.INGRESADA
    );

    DonacionDTO creada = this.donacionesClient.registrarDonacion(dto);
    return formatearDonacion(creada);
  }

  public String obtenerDonaciones() {
    List<DonacionDTO> donaciones = this.donacionesClient.obtenerDonaciones();
    return formatearListaDonaciones(donaciones);
  }

  public String buscarDonacionPorId(String id) {
    DonacionDTO donacion = this.donacionesClient.buscarDonacionPorId(id);

    if (donacion == null) {
      return "❌ No se encontró la donación con ID `" + id + "`.";
    }

    return formatearDonacion(donacion);
  }

  public String borrarDonacion(String id) {
    this.donacionesClient.eliminarDonacion(id);
    return "✅ Donación `" + id + "` eliminada correctamente.";
  }

  public String buscarDonacionesPorDonadorYFecha(
          String donadorID,
          LocalDate fechaInicio) {

    List<DonacionDTO> donaciones = this.donacionesClient.buscarPorDonadorYFecha(
            donadorID,
            fechaInicio
    );

    return formatearListaDonaciones(donaciones);
  }

  public String cambiarEstadoDonacion(String id, String estado) {
    EstadoDonacionEnum estadoEnum;

    try {
      estadoEnum = EstadoDonacionEnum.valueOf(estado.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Estado inválido. Usá INGRESADA, ACEPTADA o CONQUEJA.");
    }

    DonacionDTO actualizada = this.donacionesClient.cambiarEstadoDeDonacion(id, estadoEnum);
    return formatearDonacion(actualizada);
  }

  public String registrarQueja(String id, String descripcion) {
    DonacionDTO actualizada = this.donacionesClient.registrarQueja(id, descripcion);
    return formatearDonacion(actualizada);
  }

  public String resetearDonaciones() {
    return this.donacionesClient.resetDatabase();
  }

// -----------------------------------------
// MÉTODOS PRIVADOS DE FORMATEO
// -----------------------------------------

  private String formatearProducto(ProductoDTO creado) {
    if (creado == null) {
      return "❌ Producto no encontrado.";
    }

    return "✅ *¡Producto creado correctamente!*\n\n" +
            "🆔 *ID:* `" + creado.id() + "`\n" +
            "📛 *Nombre:* " + creado.nombre() + "\n" +
            "📝 *Descripción:* " + (creado.descripcion() != null ? creado.descripcion() : "Sin descripción") + "\n" +
            "📂 *Categoría ID:* `" + creado.categoriaID() + "`\n" +
            "🏷️ *Identificador ID:* `" + creado.identificadorID() + "`";
  }

  private String formatearListaProductos(List<ProductoDTO> productos) {
    if (productos == null || productos.isEmpty()) {
      return "⚠️ *No hay productos registrados.*";
    }

    StringBuilder sb = new StringBuilder("📋 *Productos registrados (" + productos.size() + ")*\n\n");

    for (ProductoDTO p : productos) {
      sb.append("• *")
              .append(p.nombre())
              .append("* — ID: `")
              .append(p.id())
              .append("`\n")
              .append("  📝 ")
              .append(p.descripcion())
              .append("\n\n");
    }

    return sb.toString();
  }

  private String formatearDonacion(DonacionDTO d) {
    if (d == null) {
      return "❌ Donación no encontrada.";
    }

    return "🎁 *Donación*\n\n" +
            "🆔 *ID:* `" + d.id() + "`\n" +
            "👤 *Donador ID:* " + d.donadorID() + "\n" +
            "🏢 *Depósito ID:* " + d.depositoID() + "\n" +
            "📦 *Producto ID:* " + d.productoID() + "\n" +
            "🔢 *Cantidad:* " + d.cantidad() + "\n" +
            "📊 *Estado:* " + d.estado() + "\n" +
            "📝 *Descripción:* " + d.descripcion();
  }

  private String formatearListaDonaciones(List<DonacionDTO> donaciones) {
    if (donaciones == null || donaciones.isEmpty()) {
      return "⚠️ *No hay donaciones registradas.*";
    }

    StringBuilder sb = new StringBuilder("📋 *Donaciones (" + donaciones.size() + ")*\n\n");

    for (DonacionDTO d : donaciones) {
      sb.append("• *ID:* `")
              .append(d.id())
              .append("`\n")
              .append("  👤 Donador: ")
              .append(d.donadorID())
              .append("\n")
              .append("  📦 Producto: ")
              .append(d.productoID())
              .append("\n")
              .append("  🔢 Cantidad: ")
              .append(d.cantidad())
              .append("\n")
              .append("  📊 Estado: ")
              .append(d.estado())
              .append("\n\n");
    }

    return sb.toString();
  }
}