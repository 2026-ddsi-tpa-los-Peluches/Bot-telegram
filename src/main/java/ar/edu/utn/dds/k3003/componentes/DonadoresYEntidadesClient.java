package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DonadoresYEntidadesClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public DonadoresYEntidadesClient(@Value("${url.donadoresYEntidades}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // =========================================================================
    // MÓDULO DONADORES
    // =========================================================================

    // POST /donadores -> Alta de Donador
    public DonadorDTO guardarDonador(DonadorDTO donadorDTO) {
        try {
            String url = baseUrl + "/donadores";
            ResponseEntity<DonadorDTO> response = restTemplate.postForEntity(url, donadorDTO, DonadorDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio Donadores: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al guardar el donador", e);
        }
    }

    // GET /donadores/{id}/estadisticas -> Estadísticas
    public DonadorStatsDTO obtenerEstadisticas(Integer id) {
        try {
            String url = baseUrl + "/donadores/" + id + "/estadisticas";
            ResponseEntity<DonadorStatsDTO> response = restTemplate.getForEntity(url, DonadorStatsDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar estadísticas: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al consultar estadísticas", e);
        }
    }

    // GET /donadores/{id} -> Buscar Donador por ID
    public DonadorDTO buscarDonadorPorId(Integer id) {
        try {
            String url = baseUrl + "/donadores/" + id;
            ResponseEntity<DonadorDTO> response = restTemplate.getForEntity(url, DonadorDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el donador por ID", e);
        }
    }

    // GET /donadores -> Obtener todos los Donadores
    public List<DonadorDTO> buscarTodosLosDonadores() {
        try {
            String url = baseUrl + "/donadores";
            ResponseEntity<List<DonadorDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DonadorDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la lista de donadores", e);
        }
    }

    // =========================================================================
    // MÓDULO ENTIDADES BENÉFICAS
    // =========================================================================

    // POST /entidades -> Alta de Entidad
    public EntidadBeneficaDTO guardarEntidad(EntidadBeneficaDTO entidadDTO) {
        try {
            String url = baseUrl + "/entidades";
            ResponseEntity<EntidadBeneficaDTO> response = restTemplate.postForEntity(url, entidadDTO, EntidadBeneficaDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio al crear entidad: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al crear entidad", e);
        }
    }

    // GET /entidades/{id} -> Buscar Entidad por ID
    public EntidadBeneficaDTO buscarEntidadPorId(Integer id) {
        try {
            String url = baseUrl + "/entidades/" + id;
            ResponseEntity<EntidadBeneficaDTO> response = restTemplate.getForEntity(url, EntidadBeneficaDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar entidad por ID " + id, e);
        }
    }

    // GET /entidades -> Obtener todas las Entidades
    public List<EntidadBeneficaDTO> buscarTodasLasEntidades() {
        try {
            String url = baseUrl + "/entidades";
            ResponseEntity<List<EntidadBeneficaDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EntidadBeneficaDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al consultar todas las entidades", e);
        }
    }

    public EntidadBeneficaDTO editarEntidad(Integer id, EntidadBeneficaDTO dto) {
        try {
            String url = baseUrl + "/entidades/" + id;

            HttpEntity<EntidadBeneficaDTO> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<EntidadBeneficaDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    EntidadBeneficaDTO.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al editar entidad en el microservicio: " + e.getMessage());
            return null;
        }


    }
    public DonadorDTO editarDonador(Integer id, DonadorDTO donadorDTO) {
        String url = baseUrl + "/donadores/" + id;
        HttpEntity<DonadorDTO> requestEntity = new HttpEntity<>(donadorDTO);
        ResponseEntity<DonadorDTO> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                DonadorDTO.class
        );
        return response.getBody();
    }


    // =========================================================================
// MÓDULO NECESIDADES
// =========================================================================

    // POST /necesidades -> Registrar necesidad
    public NecesidadMaterialDTO guardarNecesidad(NecesidadMaterialDTO necesidadDTO) {
        try {
            String url = baseUrl + "/necesidades";
            ResponseEntity<NecesidadMaterialDTO> response = restTemplate.postForEntity(url, necesidadDTO, NecesidadMaterialDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio al registrar necesidad: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al registrar la necesidad", e);
        }
    }

    // GET /necesidades/{productoID} -> Obtener necesidades insatisfechas por producto
    public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoID) {
        try {
            String url = baseUrl + "/necesidades/" + productoID;
            ResponseEntity<List<NecesidadMaterialDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NecesidadMaterialDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar necesidades insatisfechas", e);
        }
    }

    // POST /necesidades/{necesidadID}/satisfaccion -> Satisfacer necesidad
    public NecesidadMaterialDTO satisfacerNecesidad(Integer necesidadID, Integer cantidad) {
        try {
            String url = baseUrl + "/necesidades/" + necesidadID + "/satisfaccion";
            Map<String, Integer> request = Map.of("cantidad", cantidad);
            ResponseEntity<NecesidadMaterialDTO> response = restTemplate.postForEntity(url, request, NecesidadMaterialDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al satisfacer necesidad: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al satisfacer la necesidad", e);
        }
    }



    // GET /necesidades/detalle/{id}
    public NecesidadMaterialDTO buscarNecesidadPorId(Integer id) {
        try {
            String url = baseUrl + "/necesidades/detalle/" + id;
            ResponseEntity<NecesidadMaterialDTO> response = restTemplate.getForEntity(url, NecesidadMaterialDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la necesidad con ID " + id, e);
        }
    }

    // PUT /necesidades/{id}
    public NecesidadMaterialDTO editarNecesidad(Integer id, NecesidadMaterialDTO dto) {
        try {
            String url = baseUrl + "/necesidades/" + id;
            org.springframework.http.HttpEntity<NecesidadMaterialDTO> requestEntity = new org.springframework.http.HttpEntity<>(dto);
            ResponseEntity<NecesidadMaterialDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    NecesidadMaterialDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al editar la necesidad con ID " + id, e);
        }
    }

    // DELETE /necesidades/{id}
    public void borrarNecesidad(Integer id) {
        try {
            String url = baseUrl + "/necesidades/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la necesidad con ID " + id, e);
        }
    }

}