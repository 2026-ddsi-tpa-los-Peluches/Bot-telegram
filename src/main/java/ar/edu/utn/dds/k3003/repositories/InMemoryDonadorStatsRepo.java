//package ar.edu.utn.dds.k3003.repositories;
//
//import ar.edu.utn.dds.k3003.model.DonadorStats;
//import lombok.val;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class InMemoryDonadorStatsRepo implements DonadorStatsRepository {
//
//  private List<DonadorStats> donadorStats;
//  private AtomicLong idSecuencial = new AtomicLong(1);
//
//  public InMemoryDonadorStatsRepo() {
//    this.donadorStats = new ArrayList<>();
//  }
//
//  @Override
//  public Optional<DonadorStats> findById(String id) {
//    return this.donadorStats.stream().filter(d -> d.getId().equals(id)).findFirst();
//  }
//
//  public ArrayList<DonadorStats> findAll(){
//    return new ArrayList<>(donadorStats);
//  }
//
//  @Override
//  public DonadorStats save(DonadorStats queja) {
//    DonadorStats quejaConID = queja;
//    quejaConID.setId(String.valueOf(idSecuencial.getAndIncrement()));
//
//    this.donadorStats.add(quejaConID);
//    return this.findById(quejaConID.getId()).get();
//  }
//
//  @Override
//  public DonadorStats deleteById(String id) {
//    val queja = this.findById(id);
//    this.donadorStats.remove(queja.get());
//    return queja.get();
//  }
//}
