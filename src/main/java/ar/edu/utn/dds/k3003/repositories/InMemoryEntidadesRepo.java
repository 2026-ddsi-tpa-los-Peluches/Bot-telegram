//package ar.edu.utn.dds.k3003.repositories;
//
//import ar.edu.utn.dds.k3003.model.EntidadBenefica;
//import lombok.val;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class InMemoryEntidadesRepo implements EntidadesRepository {
//
//    private List<EntidadBenefica> entidades;
//    private AtomicLong idSecuencial = new AtomicLong(1);
//
//    public InMemoryEntidadesRepo() {
//        this.entidades = new ArrayList<>();
//    }
//
//    @Override
//    public Optional<EntidadBenefica> findById(String id) {
//        return this.entidades.stream().filter(d -> d.getId().equals(id)).findFirst();
//    }
//    @Override
//    public ArrayList<EntidadBenefica> findAll() {
//        return new ArrayList<>(entidades);
//    }
//
//    @Override
//    public EntidadBenefica save(EntidadBenefica entidad) {
//        EntidadBenefica entidadConID = entidad;
//        entidadConID.setId("entidad" +idSecuencial.getAndIncrement());
//
//        this.entidades.add(entidadConID);
//        return this.findById(entidadConID.getId()).get();
//    }
//
//    @Override
//    public EntidadBenefica deleteById(String id) {
//        val entidad = this.findById(id);
//        this.entidades.remove(entidad.get());
//        return entidad.get();
//    }
//}
