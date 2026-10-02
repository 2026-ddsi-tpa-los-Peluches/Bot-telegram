package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.componentes.Request.InsigniaIDRequest;
import ar.edu.utn.dds.k3003.componentes.Request.MisionIDRequest;
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

@Service
public class IncentivosClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public IncentivosClient(@Value("${url.incentivos}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // =========================================================================
    // MÓDULO INSIGNIAS
    // =========================================================================

    // POST /insignias -> Crear insignia
    public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO) {
        try {
            String url = baseUrl + "/insignias";
            ResponseEntity<InsigniaDTO> response = restTemplate.postForEntity(url, insigniaDTO, InsigniaDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio Incentivos al agregar insignia: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al agregar la insignia", e);
        }
    }

    // GET /insignias -> Obtener todas las insignias
    public List<InsigniaDTO> getAllInsignias() {
        try {
            String url = baseUrl + "/insignias";
            ResponseEntity<List<InsigniaDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<InsigniaDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la lista de insignias", e);
        }
    }

    // GET /insignias/{id} -> Buscar insignia por ID
    public InsigniaDTO getInsignia(String id) {
        try {
            String url = baseUrl + "/insignias/" + id;
            ResponseEntity<InsigniaDTO> response = restTemplate.getForEntity(url, InsigniaDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la insignia con ID " + id, e);
        }
    }

    // DELETE /insignias/{id} -> Eliminar insignia
    public void eliminarInsignia(String id) {
        try {
            String url = baseUrl + "/insignias/" + id;
            restTemplate.delete(url);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No se encontró la insignia con ID " + id, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la insignia con ID " + id, e);
        }
    }

    // =========================================================================
    // MÓDULO INSIGNIAS POR DONADOR (/insigniasDonador/{donadorID})
    // =========================================================================

    // POST /insigniasDonador/{donadorID} -> Asignar insignia a donador
    public void asignarInsigniaADonador(String donadorID, String insigniaID) {
        try {
            String url = baseUrl + "/insigniasDonador/" + donadorID;
            InsigniaIDRequest requestBody = new InsigniaIDRequest(insigniaID);
            requestBody.setInsigniaID(insigniaID);

            HttpEntity<InsigniaIDRequest> requestEntity = new HttpEntity<>(requestBody);
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 412) {
                throw new IllegalStateException("Error 412: Estado inválido para asignar la insignia.");
            } else if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("Error 404: Donador o Insignia inexistente.");
            }
            throw new RuntimeException("Error al asignar insignia a donador: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al asignar insignia a donador", e);
        }
    }

    // GET /insigniasDonador/{donadorID} -> Obtener insignias de un donador
    public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {
        try {
            String url = baseUrl + "/insigniasDonador/" + donadorID;
            ResponseEntity<List<InsigniaDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<InsigniaDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar las insignias del donador " + donadorID, e);
        }
    }

    // =========================================================================
    // MÓDULO MISIONES
    // =========================================================================

    // POST /misiones -> Crear misión
    public MisionDTO agregarMision(MisionDTO misionDTO) {
        try {
            String url = baseUrl + "/misiones";
            ResponseEntity<MisionDTO> response = restTemplate.postForEntity(url, misionDTO, MisionDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio Incentivos al agregar misión: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al agregar la misión", e);
        }
    }

    // GET /misiones -> Obtener todas las misiones
    public List<MisionDTO> getAllMisiones() {
        try {
            String url = baseUrl + "/misiones";
            ResponseEntity<List<MisionDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MisionDTO>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la lista de misiones", e);
        }
    }

    // GET /misiones/{id} -> Buscar misión por ID
    public MisionDTO getMision(String id) {
        try {
            String url = baseUrl + "/misiones/" + id;
            ResponseEntity<MisionDTO> response = restTemplate.getForEntity(url, MisionDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la misión con ID " + id, e);
        }
    }

    // DELETE /misiones/{id} -> Eliminar misión
    public void eliminarMision(String id) {
        try {
            String url = baseUrl + "/misiones/" + id;
            restTemplate.delete(url);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No se encontró la misión con ID " + id, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la misión con ID " + id, e);
        }
    }

    // =========================================================================
    // MÓDULO MISIONES POR DONADOR (/misionesDonador/{donadorID})
    // =========================================================================

    // POST /misionesDonador/{donadorID} -> Asignar misión a donador
    public void asignarMisionADonador(String donadorID, String misionID) {
        try {
            String url = baseUrl + "/misionesDonador/" + donadorID;
            MisionIDRequest requestBody = new MisionIDRequest(misionID);
            requestBody.setMisionID(misionID);

            HttpEntity<MisionIDRequest> requestEntity = new HttpEntity<>(requestBody);
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new IllegalStateException("Error 409: La categoría inicial de la misión no coincide con la del donador.");
            } else if (e.getStatusCode().value() == 412) {
                throw new IllegalArgumentException("Error 412: Misión inexistente o ID inválido.");
            } else if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("Error 404: Donador no encontrado.");
            }
            throw new RuntimeException("Error al asignar misión a donador: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al asignar misión a donador", e);
        }
    }

    // GET /misionesDonador/{donadorID} -> Obtener misión en curso del donador
    public MisionDTO getMisionEnCursoDeDonador(String donadorID) {
        try {
            String url = baseUrl + "/misionesDonador/" + donadorID;
            ResponseEntity<MisionDTO> response = restTemplate.getForEntity(url, MisionDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la misión en curso del donador " + donadorID, e);
        }
    }

    // DELETE /misionesDonador/{donadorID} -> Cancelar / quitar misión en curso del donador
    public void quitarMisionDeDonador(String donadorID) {
        try {
            String url = baseUrl + "/misionesDonador/" + donadorID;
            restTemplate.delete(url);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No se encontró misión en curso para el donador " + donadorID, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al quitar la misión del donador", e);
        }
    }


}