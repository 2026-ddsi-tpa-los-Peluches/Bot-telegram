package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.TipoNecesidadMaterialEnum;
import ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Fachada {

  private final DonadoresYEntidadesClient donadoresYEntidadesClient;

  public Fachada(DonadoresYEntidadesClient donadoresYEntidadesClient) {
    this.donadoresYEntidadesClient = donadoresYEntidadesClient;
  }

  // =========================================================================
  // Módulo Donadores
  // =========================================================================

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

  // =========================================================================
  // Módulo Entidades Benéficas
  // =========================================================================

  public String editarEntidad(Integer id, String nuevoNombre) {
    return "✏️ Entidad ID " + id + " actualizada a '" + nuevoNombre + "'.";
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

    StringBuilder sb = new StringBuilder("📋 *Lista de Entidades Benéficas:*\n\n");
    for (EntidadBeneficaDTO ent : entidades) {
      sb.append("• *ID:* ").append(ent.id())
              .append(" | *Razón Social:* ").append(ent.razonSocial())
              .append("\n");
    }

    return sb.toString();
  }

  // =========================================================================
  // Módulo Necesidades
  // =========================================================================

  // --- Métodos consumidos por TelegramBotComponent ---

  public String crearNecesidad(String entidadId, Integer nivelDeUrgencia, String descripcion, Integer cantidadObjetivo, String productoSolicitadoId, TipoNecesidadMaterialEnum tipo) {
    NecesidadMaterialDTO dto = new NecesidadMaterialDTO(
            null,
            entidadId,
            nivelDeUrgencia,
            descripcion,
            cantidadObjetivo,
            productoSolicitadoId,
            tipo
    );

    NecesidadMaterialDTO creada = this.donadoresYEntidadesClient.guardarNecesidad(dto);

    if (creada == null) {
      return "❌ Error: No se pudo registrar la necesidad en el microservicio.";
    }

    return "✅ *Necesidad Registrada con Éxito*\n\n" +
            "🆔 *ID Necesidad:* `" + creada.id() + "`\n" +
            "🏢 *Entidad ID:* " + creada.entidadID() + "\n" +
            "🔥 *Urgencia:* " + creada.nivelDeUrgencia() + "\n" +
            "📝 *Descripción:* " + creada.descripcion() + "\n" +
            "🔢 *Cantidad Objetivo:* " + creada.cantidadObjetivo() + "\n" +
            "📦 *Producto ID:* " + creada.productoSolicitadoID() + "\n" +
            "🏷️ *Tipo:* " + creada.tipo();
  }

  public String editarNecesidad(Integer id, String nuevaDescripcion) {
    NecesidadMaterialDTO dto = new NecesidadMaterialDTO(
            id, null, null, nuevaDescripcion, null, null, null
    );
    NecesidadMaterialDTO editada = this.donadoresYEntidadesClient.editarNecesidad(id, dto);

    if (editada == null) {
      return "❌ Error: No se pudo editar la necesidad con ID " + id;
    }

    return "✏️ *Necesidad Actualizada*\n\n" +
            "🆔 *ID:* `" + editada.id() + "`\n" +
            "📝 *Nueva Descripción:* " + editada.descripcion();
  }

  public void borrarNecesidadPorID(Integer id) {
    this.donadoresYEntidadesClient.borrarNecesidad(id);
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


}