//package ar.edu.utn.dds.k3003.repositories;
//
//import ar.edu.utn.dds.k3003.model.Donador;
//import lombok.val;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class InMemoryDonadoresRepo implements DonadoresRepository {
//
//  private List<Donador> donadores;
//  private AtomicLong idSecuencial = new AtomicLong(1);
//
//  public InMemoryDonadoresRepo() {
//    this.donadores = new ArrayList<>();
//  }
//
//  @Override
//  public Optional<Donador> findById(String id) {
//    return this.donadores.stream().filter(d -> d.getId().equals(id)).findFirst();
//  }
//
//  @Override
//  public Donador save(Donador donador) {
//    if (donador.getId() != null) {
//      throw new IllegalArgumentException("Donador no debe tener id antes de al guardarse");
//    }
//    donador.setId("donador" + idSecuencial.getAndIncrement());
//    this.donadores.add(donador);
//    return this.findById(donador.getId()).get();
//  }
//  public Donador update(Donador donador) {
//    if (donador.getId() == null) {
//      throw new IllegalArgumentException("donadorID no puede ser nulo.");
//    }
//    this.donadores.removeIf(d -> d.getId().equals(donador.getId()));
//    this.donadores.add(donador);
//    return this.findById(donador.getId()).get();
//  }
//  @Override
//  public List<Donador> findAll() {
//    return this.donadores;
//  }
//
//  @Override
//  public Donador deleteById(String id) {
//    val donador = this.findById(id);
//    this.donadores.remove(donador.get());
//    return donador.get();
//  }
//}
