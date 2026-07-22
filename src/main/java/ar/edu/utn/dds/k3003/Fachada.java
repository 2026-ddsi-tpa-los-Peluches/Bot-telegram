package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Importá tus DTOs según la ubicación donde los tengas
// import ar.edu.utn.dds.k3003.model.DonadorDTO;

@Service
public class Fachada {

  private final RestClient donadoresClient;
  private final RestClient necesidadesClient;

  public Fachada(
          @Value("${servicios.donadores.url:http://localhost:8081}") String donadoresUrl,
          @Value("${servicios.necesidades.url:http://localhost:8082}") String necesidadesUrl) {

    this.donadoresClient = RestClient.builder().baseUrl(donadoresUrl).build();
    this.necesidadesClient = RestClient.builder().baseUrl(necesidadesUrl).build();
  }

  // Consulta al módulo de Donadores
  public DonadorDTO buscarDonadorPorId(Integer id) {
    return donadoresClient.get()
            .uri("/api/donadores/{id}", id)
            .retrieve()
            .body(DonadorDTO.class);
  }

  // Eliminación en el módulo de Necesidades
  public void borrarNecesidadPorID(Integer id) {
    necesidadesClient.delete()
            .uri("/api/necesidades/{id}", id)
            .retrieve()
            .toBodilessEntity();
  }
}