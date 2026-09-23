package ar.edu.utn.dds.k3003.componentes.Request;

public record DonadorRequest(
        String depositoID,
        String donacionID,
        String productoID,
        Integer cantidad
) {}