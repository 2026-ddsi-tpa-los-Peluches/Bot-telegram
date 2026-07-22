package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuejaRepository extends JpaRepository<Queja, Integer> {

}

//Optional<Queja> findById(String id);
//ArrayList<Queja> findAll();
//
//Queja save(Queja queja);
//
//Queja deleteById(String id);