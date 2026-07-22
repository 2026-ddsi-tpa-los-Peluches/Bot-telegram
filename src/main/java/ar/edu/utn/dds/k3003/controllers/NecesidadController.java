package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ar.edu.utn.dds.k3003.exceptions.NecesidadYaExistenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/necesidades")
public class NecesidadController {

  private Fachada fachada;

  public NecesidadController(Fachada fachada) {
    this.fachada = fachada;
  }

  // Opcion 1 utilizando @RequestMapping
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Object> postNecesidad(
          @RequestBody NecesidadMaterialDTO necesidadMaterialDTO) {

    try {

      NecesidadMaterialDTO necesidadAgregada =
              this.fachada.registrarNecesidad(necesidadMaterialDTO);

      return ResponseEntity.status(HttpStatus.CREATED)
              .body(necesidadAgregada);

    } catch (NecesidadYaExistenteException e) {

      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(e.getMessage());

    } catch (IllegalArgumentException e) {

      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(e.getMessage());

    } catch (NoSuchElementException e) {

      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(e.getMessage());

    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(e.getMessage());
    }
  }

  @RequestMapping(method = RequestMethod.GET,value = "/{productoID}")
  public ResponseEntity<List<NecesidadMaterialDTO>> getNecesidades(
      @PathVariable String productoID) {
    try {
      List<NecesidadMaterialDTO> necesidades =
          this.fachada.obtenerNecesidadesInsatisfechasDe(productoID);
      return ResponseEntity.status(HttpStatus.OK).body(necesidades);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "/{necesidadID}/satisfaccion")
  public ResponseEntity<NecesidadMaterialDTO> satisfacerNecesidad(
      @PathVariable Integer necesidadID, @RequestBody Map<String, Integer> request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(this.fachada.satisfacerNecesidad(necesidadID, request.get("cantidad")));
  }

  // Opcion 2 utilizando @GetMapping

  // @GetMapping
  // public ResponseEntity<NecesidadDTO> getDonadorByID(@RequestParam String donadorID) {
  //  return ResponseEntity.status(HttpStatus.OK).body(this.fachada.buscarDonadorPorID(donadorID));
  // }
}
