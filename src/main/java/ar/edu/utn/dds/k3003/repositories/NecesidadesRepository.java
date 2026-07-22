package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NecesidadesRepository extends JpaRepository<NecesidadMaterial, Integer> {

}
//Optional<NecesidadMaterial> findById(String id);
//
//ArrayList<NecesidadMaterial> findAll();
//NecesidadMaterial save(NecesidadMaterial necesidad);
//
//NecesidadMaterial deleteById(String id);