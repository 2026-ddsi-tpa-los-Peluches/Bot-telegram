package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/insigniasDonador")
public class InsigniasController {


    private Fachada fachada;

    public InsigniasController(Fachada fachada) {
        this.fachada = fachada;
    }



    // Opcion 1 utilizando @RequestMapping
    @RequestMapping(method = RequestMethod.POST,value = "/{id}")
    public ResponseEntity<DonadorDTO> agregarInsignia(
            @PathVariable Integer id, @RequestBody Map<String, String> request) {

        String insigniaID = request.get("insigniaID");

        // 2. Llamamos a la fachada para que haga la lógica de negocio
        DonadorDTO donadorActualizado = this.fachada.agregarInsignia(id, insigniaID);

        // 3. Devolvemos el donador actualizado con código 200 OK
        return ResponseEntity.status(HttpStatus.OK).body(donadorActualizado);
    }


}
