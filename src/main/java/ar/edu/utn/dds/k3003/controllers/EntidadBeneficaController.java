package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entidades")
public class EntidadBeneficaController {

  private Fachada fachada;

  public EntidadBeneficaController(Fachada fachada) {
    this.fachada = fachada;
  }

  // Opcion 1 utilizando @RequestMapping
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<EntidadBeneficaDTO> postEntidadBenefica(
      @RequestBody EntidadBeneficaDTO entidadBeneficaDTO) {
    try {

      EntidadBeneficaDTO entidadAgregada = fachada.agregarEntidad(entidadBeneficaDTO);
      return ResponseEntity.status(HttpStatus.CREATED).body(entidadAgregada);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public ResponseEntity<EntidadBeneficaDTO> getEntidadBenefica(@PathVariable Integer id) {
    try {
      EntidadBeneficaDTO entidadBuscada = fachada.buscarEntidadPorID(id);
      return ResponseEntity.status(HttpStatus.OK).body(entidadBuscada);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<EntidadBeneficaDTO>> getEntidadBeneficas() {
    List<EntidadBeneficaDTO> entidades = fachada.obtenerEntidades();
    return ResponseEntity.status(HttpStatus.OK).body(entidades);
  }
  // Opcion 2 utilizando @GetMapping

  // @GetMapping
  // public ResponseEntity<EntidadBeneficaDTO> getDonadorByID(@RequestParam String donadorID) {
  //  return ResponseEntity.status(HttpStatus.OK).body(this.fachada.buscarDonadorPorID(donadorID));
  // }
}
