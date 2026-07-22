package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donadores")
public class DonadorController {

  private Fachada fachada;

  public DonadorController(Fachada fachada) {
    this.fachada = fachada;
  }

  // Opcion 1 utilizando @RequestMapping
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<DonadorDTO> postDonador(@RequestBody DonadorDTO donadorDTO) {
    DonadorDTO donadorAgregado = fachada.agregarDonador(donadorDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(donadorAgregado);
  }



  @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/misionActual")
  public ResponseEntity<DonadorDTO> actualizarMision(
          @PathVariable Integer id, @RequestBody Map<String, String> request) {

    // 1. Validamos que nos pasen la misionID en el JSON
    if (!request.containsKey("misionActualID")) {
      throw new IllegalArgumentException("Falta campo misionActualID en el body de la petición");
    }

    String misionActualID = request.get("misionActualID");

    // 2. Llamamos a la fachada para que asigne la misión
    DonadorDTO donadorActualizado = this.fachada.asignarMision(id, misionActualID);

    // 3. Devolvemos el donador actualizado con código 200 OK
    return ResponseEntity.status(HttpStatus.OK).body(donadorActualizado);
  }





  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public ResponseEntity<DonadorDTO> getDonador(@PathVariable Integer id) {
    DonadorDTO donadorBuscado = fachada.buscarDonadorPorID(id);
    return ResponseEntity.status(HttpStatus.OK).body(donadorBuscado);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<DonadorDTO>> getDonadores() {
    List<DonadorDTO> donadores = fachada.obtenerDonadores();
    return ResponseEntity.status(HttpStatus.OK).body(donadores);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/puede-donar")
  public ResponseEntity<Map<String, Boolean>> puedeDonar(@PathVariable Integer id) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(Map.of("puedeDonar", this.fachada.puedeDonar(id)));
  }

  @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/estado")
  public ResponseEntity<DonadorDTO> cambiarEstado(
      @PathVariable Integer id, @RequestBody Map<String, String> request) {

    if (!request.containsKey("estado")
        || Arrays.stream(EstadoDonadorEnum.values())
            .noneMatch(e -> e.name().equals(request.get("estado")))) {
      throw new IllegalArgumentException(
          "Falta campo estado, o bien esta presente pero su valor es incorrecto");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.fachada.modificarEstado(id, EstadoDonadorEnum.valueOf(request.get("estado"))));
  }

  @RequestMapping(method = RequestMethod.PATCH, value = "/{id}/categoria")
  public ResponseEntity<DonadorDTO> cambiarCategoria(
      @PathVariable Integer id, @RequestBody Map<String, String> request) {

    if (!request.containsKey("categoria")
        || Arrays.stream(CategoriaDonadorEnum.values())
            .noneMatch(e -> e.name().equals(request.get("categoria")))) {
      throw new IllegalArgumentException("Falta campo categoria, o bien su valor es incorrecto");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.fachada.modificarCategoria(id, request.get("categoria")));
  }

  // Opcion 2 utilizando @GetMapping

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/quejas")
  public ResponseEntity<List<QuejaDTO>> getQuejas(@PathVariable Integer id) {
    List<QuejaDTO> quejas = this.fachada.obtenerQuejasDe(id);
    return ResponseEntity.status(HttpStatus.OK).body(quejas);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{donadorID}/quejas")
  public ResponseEntity<QuejaDTO> agregarQueja(
      @PathVariable String donadorID, @RequestBody Map<String, String> request) {
    if (!request.containsKey("donacionID") || !request.containsKey("descripcion")) {
      throw new IllegalArgumentException("Falta campo donacionID o descripcion");
    }
    QuejaDTO nuevaQueja =
        new QuejaDTO(
            null,
            request.get("donacionID"),
            donadorID,
            LocalDate.now(),
            request.get("descripcion"));
    return ResponseEntity.status(HttpStatus.CREATED).body(this.fachada.agregarQueja(nuevaQueja));
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/estadisticas")
  public ResponseEntity<DonadorStatsDTO> getEstadisticas(@PathVariable Integer id) {
    return ResponseEntity.status(HttpStatus.OK).body(this.fachada.estadisticasDonador(id));
  }

  // @GetMapping
  // public ResponseEntity<DonadorDTO> getDonadorByID(@RequestParam String donadorID) {
  //  return ResponseEntity.status(HttpStatus.OK).body(this.fachada.buscarDonadorPorID(donadorID));
  // }
}
