package ar.edu.utn.dds.k3003.componentes.Request;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;

public record DepositoRequest(
        String nombre,
        String direccion,
        Integer capacidadMaxima,
        TipoAlgoritmoEnum tipoAlgoritmo
) {
}