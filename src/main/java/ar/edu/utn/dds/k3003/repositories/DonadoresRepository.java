package ar.edu.utn.dds.k3003.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonadoresRepository extends JpaRepository<Donador, Integer> {
}



