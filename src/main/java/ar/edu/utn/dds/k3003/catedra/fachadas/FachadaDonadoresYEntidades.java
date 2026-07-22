package ar.edu.utn.dds.k3003.catedra.fachadas;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaDonadoresYEntidades {

  DonadorDTO agregarDonador(DonadorDTO donadorDTO);

  DonadorDTO buscarDonadorPorID(Integer donadorID) throws NoSuchElementException;

  EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO);

  EntidadBeneficaDTO buscarEntidadPorID(Integer entidadID) throws NoSuchElementException;

  NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO);

  QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException;

  Boolean puedeDonar(Integer donadorID) throws NoSuchElementException;

  List<QuejaDTO> obtenerQuejasDe(Integer donadorID) throws NoSuchElementException;

  DonadorDTO modificarEstado(Integer donadorID, EstadoDonadorEnum estado)
      throws NoSuchElementException;

  DonadorDTO modificarCategoria(Integer donadorID, String categoria) throws NoSuchElementException;

  List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID);

  NecesidadMaterialDTO satisfacerNecesidad(Integer necesidadID, Integer cantidad)
      throws NoSuchElementException;

  DonadorStatsDTO estadisticasDonador(Integer donadorID);


}
