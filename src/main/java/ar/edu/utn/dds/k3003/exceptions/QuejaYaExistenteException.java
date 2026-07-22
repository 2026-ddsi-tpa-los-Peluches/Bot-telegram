package ar.edu.utn.dds.k3003.exceptions;

public class QuejaYaExistenteException extends RuntimeException {
  public QuejaYaExistenteException(String mensaje) {
    super(mensaje);
  }
}
