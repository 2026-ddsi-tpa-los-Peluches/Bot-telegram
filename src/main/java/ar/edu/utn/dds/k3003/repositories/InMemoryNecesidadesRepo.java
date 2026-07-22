//package ar.edu.utn.dds.k3003.repositories;
//
//import ar.edu.utn.dds.k3003.model.NecesidadMaterial;
//import lombok.val;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class InMemoryNecesidadesRepo implements NecesidadesRepository {
//
//  private List<NecesidadMaterial> necesidades;
//  private AtomicLong idSecuencial = new AtomicLong(1);
//
//  public InMemoryNecesidadesRepo() {
//    this.necesidades = new ArrayList<>();
//  }
//
//  @Override
//  public Optional<NecesidadMaterial> findById(String id) {
//    return this.necesidades.stream().filter(d -> d.getId().equals(id)).findFirst();
//  }
//  @Override
//  public ArrayList<NecesidadMaterial> findAll() {
//    return new ArrayList<>(necesidades);
//  }
//  @Override
//  public NecesidadMaterial save(NecesidadMaterial necesidad) {
//    NecesidadMaterial necesidadConID = necesidad;
//    necesidadConID.setId(String.valueOf(idSecuencial.getAndIncrement()));
//    this.necesidades.add(necesidadConID);
//    return this.findById(necesidadConID.getId()).get();
//  }
//
//  @Override
//  public  NecesidadMaterial deleteById(String id) {
//    val necesidad = this.findById(id);
//    this.necesidades.remove(necesidad.get());
//    return necesidad.get();
//  }
//}
