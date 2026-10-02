package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.*;
import ar.edu.utn.dds.k3003.componentes.Request.AsignacionRequest;
import ar.edu.utn.dds.k3003.componentes.Request.DepositoRequest;
import ar.edu.utn.dds.k3003.componentes.Request.DonadorRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class LogisticaClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    public LogisticaClient(@Value("${url.logistica}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // =========================================================================
    // MÓDULO DEPÓSITOS
    // =========================================================================

    // POST /depositos -> Agregar Depósito
    public DepositoDTO agregarDeposito(DepositoRequest depositoRequest) {
        try {
            String url = baseUrl + "/depositos";
            ResponseEntity<DepositoDTO> response = restTemplate.postForEntity(url, depositoRequest, DepositoDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error en microservicio Logística (Guardar Depósito): " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al guardar el depósito", e);
        }
    }

    // GET /depositos/{id} -> Buscar Depósito por ID
    public DepositoDTO buscarDepositoPorId(Integer id) {
        try {
            String url = baseUrl + "/depositos/" + id;
            ResponseEntity<DepositoDTO> response = restTemplate.getForEntity(url, DepositoDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el depósito por ID", e);
        }
    }

    // GET /depositos -> Obtener todos los Depósitos
    public List<DepositoDTO> obtenerDepositos() {
        try {
            String url = baseUrl + "/depositos";
            ResponseEntity<List<DepositoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DepositoDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar la lista de depósitos", e);
        }
    }

    // =========================================================================
    // MÓDULO ASIGNACIONES Y GESTIÓN
    // =========================================================================

    // GET /asignaciones/paquete/{id} -> Buscar Asignación por Paquete ID
    public AsignacionDTO buscarAsignacionPorPaqueteId(Integer id) {
        try {
            String url = baseUrl + "/asignaciones/paquete/" + id;
            ResponseEntity<AsignacionDTO> response = restTemplate.getForEntity(url, AsignacionDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar asignación por paquete ID", e);
        }
    }

    // GET /asignaciones -> Obtener Asignaciones
    public List<AsignacionRequest> obtenerAsignaciones() {
        try {
            String url = baseUrl + "/asignaciones";
            ResponseEntity<List<AsignacionRequest>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AsignacionRequest>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar las asignaciones", e);
        }
    }

    // POST /donaciones/gestionar -> Gestionar Donación en Logística
    public void gestionarDonacion(String depositoID, String donacionID, String productoID, Integer cantidad) {
        try {
            String url = baseUrl + "/depositos/" + depositoID + "/donacion";

            // Creamos el objeto en una sola línea, limpio y tipado
            DonadorRequest request= new DonadorRequest(depositoID, donacionID, productoID, cantidad);

            // RestTemplate se encarga solo de transformarlo a JSON
            restTemplate.postForEntity(url, request, Void.class);

        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al gestionar la donación en Logística", e);
        }
    }


    public List<PaqueteDTO> obtenerPaquetes() {
        String url = baseUrl + "/paquetes";
        ResponseEntity<List<PaqueteDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaqueteDTO>>() {}
        );
        return response.getBody();
    }

    public PaqueteDTO buscarPaquetePorId(String id) {
        String url = baseUrl + "/paquetes/" + id;
        ResponseEntity<PaqueteDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                PaqueteDTO.class
        );
        return response.getBody();
    }


}