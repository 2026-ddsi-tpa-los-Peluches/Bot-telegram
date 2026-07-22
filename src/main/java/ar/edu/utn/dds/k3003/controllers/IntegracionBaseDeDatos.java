package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.repositories.DonadoresRepository;
import ar.edu.utn.dds.k3003.repositories.EntidadesRepository;
import ar.edu.utn.dds.k3003.repositories.NecesidadesRepository;
import ar.edu.utn.dds.k3003.repositories.QuejaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegracionBaseDeDatos {

    @Autowired
    private DonadoresRepository donadoresRepository;
    @Autowired
    private EntidadesRepository entidadesRepository;
    @Autowired
    private NecesidadesRepository necesidadesRepository;
    @Autowired
    private QuejaRepository quejaRepository;

    @PostMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        quejaRepository.deleteAll();
        necesidadesRepository.deleteAll();
        donadoresRepository.deleteAll();
        entidadesRepository.deleteAll();
        return ResponseEntity.ok("Base de datos reseteada correctamente");
    }
}