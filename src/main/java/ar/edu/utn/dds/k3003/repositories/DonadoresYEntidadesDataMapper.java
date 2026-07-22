package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.model.*;

public class DonadoresYEntidadesDataMapper {

    public DonadorDTO toDonadorDTO(Donador donador) {
        return new DonadorDTO(
                donador.getId(),
                donador.getNombre(),
                donador.getApellido(),
                donador.getEdad(),
                donador.getEmail(),
                donador.getNroDocumento(),
                donador.getDomicilio(),
                donador.getEstado(),
                donador.getCategoria());
    }

    public Donador toDonador(DonadorDTO donadorDTO) {
        return new Donador(
                donadorDTO.id(),
                donadorDTO.nombre(),
                donadorDTO.apellido(),
                donadorDTO.edad(),
                donadorDTO.email(),
                donadorDTO.nroDocumento(),
                donadorDTO.domicilio());
    }

    public EntidadBeneficaDTO toEntidadBeneficaDTO(EntidadBenefica entidad) {
        return new EntidadBeneficaDTO(
                entidad.getId(),
                entidad.getRazonSocial(),
                entidad.getDomicilio(),
                entidad.getTelefono(),
                entidad.getEmail());

    }

    public EntidadBenefica toEntidadBenefica(EntidadBeneficaDTO entidadBeneficaDTO) {
        return new EntidadBenefica(
                entidadBeneficaDTO.razonSocial(),
                entidadBeneficaDTO.domicilio(),
                entidadBeneficaDTO.telefono(),
                entidadBeneficaDTO.correo()
        );
    }

    public NecesidadMaterialDTO toNecesidadMaterialDTO(NecesidadMaterial necesidad) {
        TipoNecesidadMaterialEnum tipo;
        if (necesidad instanceof NecesidadExtraordinaria) {
            tipo = TipoNecesidadMaterialEnum.EXTRAORDINARIA;
        } else if (necesidad instanceof NecesidadRecurrente) {
            tipo = TipoNecesidadMaterialEnum.RECURRENTE;
        } else {
            throw new IllegalArgumentException("No existe el tipo de NecesidadMaterial");
        }

        return new NecesidadMaterialDTO(
                necesidad.getId(),
                necesidad.getEntidadID(),
                necesidad.getNivelDeUrgencia(),
                necesidad.getDescripcion(),
                necesidad.getCantidadObjetivo(),
                necesidad.getProductoSolicitadoID(),
                tipo
        );
    }

    public NecesidadMaterial toNecesidadMaterial(NecesidadMaterialDTO necesidadMaterialDTO) {
        return switch (necesidadMaterialDTO.tipo()) {
            case EXTRAORDINARIA -> new NecesidadExtraordinaria(
                    necesidadMaterialDTO.entidadID(),
                    necesidadMaterialDTO.nivelDeUrgencia(),
                    necesidadMaterialDTO.descripcion(),
                    necesidadMaterialDTO.productoSolicitadoID(),
                    necesidadMaterialDTO.cantidadObjetivo()
            );
            case RECURRENTE -> new NecesidadRecurrente(
                    necesidadMaterialDTO.entidadID(),
                    necesidadMaterialDTO.nivelDeUrgencia(),
                    necesidadMaterialDTO.descripcion(),
                    necesidadMaterialDTO.productoSolicitadoID(),
                    necesidadMaterialDTO.cantidadObjetivo()
            );
        };
    }

    public QuejaDTO toQuejaDTO(Queja queja) {
        return new QuejaDTO(
                queja.getId(),
                queja.getDonacionId(),
                queja.getDonadorId(),
                queja.getFecha(),
                queja.getMotivo());
    }

    public Queja toQueja(QuejaDTO quejaDTO) {
        return new Queja(
                quejaDTO.id(),
                quejaDTO.donacionID(),
                quejaDTO.donadorID(),
                quejaDTO.descripcion(),
                quejaDTO.fecha());
    }





}
