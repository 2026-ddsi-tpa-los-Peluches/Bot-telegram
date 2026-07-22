package ar.edu.utn.dds.k3003.repositories;

//
//public class InMemoryQuejaRepo implements QuejaRepository {
//
//  private List<Queja> quejas;
//  private AtomicLong idSecuencial = new AtomicLong(1);
//
//  public InMemoryQuejaRepo() {
//    this.quejas = new ArrayList<>();
//  }
//
//  @Override
//  public Optional<Queja> findById(String id) {
//    return this.quejas.stream().filter(d -> d.getId().equals(id)).findFirst();
//  }
//
//  public ArrayList<Queja> findAll(){
//    return new ArrayList<>(quejas);
//  }
//
//  @Override
//  public Queja save(Queja queja) {
//    Queja quejaConID = queja;
//    quejaConID.setId(String.valueOf(idSecuencial.getAndIncrement()));
//
//    this.quejas.add(quejaConID);
//    return this.findById(quejaConID.getId()).get();
//  }
//
//  @Override
//  public Queja deleteById(String id) {
//    val queja = this.findById(id);
//    this.quejas.remove(queja.get());
//    return queja.get();
//  }
//}
