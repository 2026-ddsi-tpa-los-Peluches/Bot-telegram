package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.componentes.Request.QuejaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DonacionesClient {

    private final String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public DonacionesClient(
            @Value("${url.donaciones}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ============================================================
    // CATEGORÍAS
    // ============================================================

    public CategoriaDTO agregarCategoria(CategoriaDTO categoriaDTO) {
        // 1. Configuramos las cabeceras para indicar que enviamos JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Creamos la entidad con el DTO y las cabeceras
        HttpEntity<CategoriaDTO> requestEntity = new HttpEntity<>(categoriaDTO, headers);

        // 3. Enviamos la petición POST con la entidad
        return restTemplate.postForObject(
                baseUrl + "/categorias",
                requestEntity,
                CategoriaDTO.class
        );
    }
    public List<CategoriaDTO> obtenerCategorias() {
        ResponseEntity<List<CategoriaDTO>> response =
                restTemplate.exchange(
                        baseUrl + "/categorias",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<CategoriaDTO>>() {}
                );

        return response.getBody();
    }

    public void eliminarCategoria(String id) {
        restTemplate.delete(baseUrl + "/categorias/" + id);
    }

    // ============================================================
    // IDENTIFICADORES
    // ============================================================

    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IdentificadorDTO> requestEntity = new HttpEntity<>(identificadorDTO, headers);

        return restTemplate.postForObject(
                baseUrl + "/identificadores",
                requestEntity,
                IdentificadorDTO.class
        );
    }

    public List<IdentificadorDTO> obtenerIdentificadores() {
        ResponseEntity<List<IdentificadorDTO>> response =
                restTemplate.exchange(
                        baseUrl + "/identificadores",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<IdentificadorDTO>>() {}
                );

        return response.getBody();
    }

    public void eliminarIdentificador(String id) {
        restTemplate.delete(baseUrl + "/identificadores/" + id);
    }

    // ============================================================
    // PRODUCTOS
    // ============================================================

    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductoDTO> requestEntity = new HttpEntity<>(productoDTO, headers);

        return restTemplate.postForObject(
                baseUrl + "/productos",
                requestEntity,
                ProductoDTO.class
        );
    }

    public List<ProductoDTO> obtenerProductos() {
        ResponseEntity<List<ProductoDTO>> response =
                restTemplate.exchange(
                        baseUrl + "/productos",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<ProductoDTO>>() {}
                );

        return response.getBody();
    }

    public ProductoDTO buscarProductoPorId(String id) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/productos/" + id,
                    ProductoDTO.class
            );
        } catch (Exception e) {
            return null;
        }
    }

    public ProductoDTO actualizarProducto(String id, ProductoDTO productoDTO) {
        return restTemplate.exchange(
                baseUrl + "/productos/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(productoDTO),
                ProductoDTO.class
        ).getBody();
    }

    public void eliminarProducto(String id) {
        restTemplate.delete(baseUrl + "/productos/" + id);
    }

    // ============================================================
    // DONACIONES
    // ============================================================

    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DonacionDTO> requestEntity = new HttpEntity<>(donacionDTO, headers);

        return restTemplate.postForObject(
                baseUrl + "/donaciones",
                requestEntity,
                DonacionDTO.class
        );
    }

    public List<DonacionDTO> obtenerDonaciones() {
        ResponseEntity<List<DonacionDTO>> response =
                restTemplate.exchange(
                        baseUrl + "/donaciones",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<DonacionDTO>>() {}
                );

        return response.getBody();
    }

    public DonacionDTO buscarDonacionPorId(String id) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/donaciones/" + id,
                    DonacionDTO.class
            );
        } catch (Exception e) {
            return null;
        }
    }

    public void eliminarDonacion(String id) {
        restTemplate.delete(baseUrl + "/donaciones/" + id);
    }

    public List<DonacionDTO> buscarPorDonadorYFecha(
            String donadorID,
            LocalDate fechaInicio) {

        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/donaciones/search")
                .queryParam("donadorID", donadorID)
                .queryParam("fechaInicio", fechaInicio)
                .toUriString();

        ResponseEntity<List<DonacionDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<DonacionDTO>>() {}
                );

        return response.getBody();
    }

    public DonacionDTO cambiarEstadoDeDonacion(
            String id,
            EstadoDonacionEnum estado) {

        Map<String, String> body = Map.of(
                "estado", estado.name()
        );

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(body);

        return restTemplate.exchange(
                baseUrl + "/donaciones/" + id + "/estado",
                HttpMethod.PATCH,
                request,
                DonacionDTO.class
        ).getBody();
    }

    public DonacionDTO registrarQueja(
            String id,
            String descripcion) {

        QuejaRequest body =
                new QuejaRequest(descripcion);

        return restTemplate.postForObject(
                baseUrl + "/donaciones/" + id + "/queja",
                body,
                DonacionDTO.class
        );
    }

    // ============================================================
    // RESET
    // ============================================================

    public String resetDatabase() {
        return restTemplate.postForObject(
                baseUrl + "/reset",
                null,
                String.class
        );
    }
}