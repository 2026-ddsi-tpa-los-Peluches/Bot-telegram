package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;

@Service
public class IncentivosClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public IncentivosClient(@Value("${INCENTIVOS_SERVICE_URL:http://localhost:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Corresponde al endpoint GET /insignias/{donadorID} del Swagger
    public List<InsigniaDTO> getInsigniasDeDonador(Integer donadorId) {

        try {
            // Armamos la URL exacta como dice el Swagger
            String url = baseUrl + "/insignias/" + donadorId;
            InsigniaDTO[] insignias = restTemplate.getForObject(url, InsigniaDTO[].class);
            return Arrays.asList(insignias);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar insignias en Incentivos", e);
        }
    }

    // Corresponde al endpoint GET /misiones/{donadorID} del Swagger
    public MisionDTO getMisionEnCursoDeDonador(Integer donadorId) {

        try {
            // Armamos la URL exacta como dice el Swagger
            String url = baseUrl + "/misiones/" + donadorId;
            return restTemplate.getForObject(url, MisionDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar misiones en Incentivos", e);
        }
    }
}