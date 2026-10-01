package ar.edu.utn.dds.k3003.componentes.Request;

import ar.edu.utn.dds.k3003.catedra.dtos.logistica.EstadoAsginacionEnum;

import java.time.LocalDateTime;

public record AsignacionRequest(
        String id,
        String necesidadID,
        LocalDateTime fecha,
        Integer cantidad,
        EstadoAsginacionEnum estado) {}
