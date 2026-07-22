package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntidadesRepository  extends JpaRepository<EntidadBenefica, Integer>  {

}
//Optional<EntidadBenefica> findById(String id);
//
//ArrayList<EntidadBenefica> findAll();
//EntidadBenefica save(EntidadBenefica entidad);
//
//EntidadBenefica deleteById(String id);