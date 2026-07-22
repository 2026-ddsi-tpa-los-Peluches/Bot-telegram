````mermaid
classDiagram
	class Donador {
	-id String
	-nombre String
	-apellido String
	-edad Integer
	-email String
	-nroDocumento String
	-domicilio String
	-estado EstadoDonadorEnum
	-categoria String
	-historial List~EstadoDonadorEnum~
	+puedeDonar() boolean
	-recalcularEstado()
	}


	class EstadoDonadorEnum {
	<<enum>>
	VERIFICADO
	SOSPECHOSO
	BANEADO
	}

	class Queja {
	-String donacionId
	-String motivo
	-LocalDate fecha
	}

	Donador --> EstadoDonadorEnum : estado
    Donador "1" <-- "*" Queja
	class EntidadBenefica {
	  -id: Long
	  -razonSocial: String
	  -domicilio: String
	  -telefono: String
	  -email: String

	}

	class NecesidadMaterial {
	  <<abstract>>
	  -id: String
	  -entidadID: String
	  -nivelDeUrgencia: Integer
	  -descripcion: String
	  -cantidadNecesaria: int
	  -cantidadObjetivo: int
	  -urgencia: int
	  -productoSolicitadoID: String
	  +estaSatisfecha(): boolean
	  +satisfacer(cantidad: int)
	}

	class NecesidadExtraordinaria {
	  +satisfacer(cantidad: int)
	}

	class NecesidadRecurrente{
	  +satisfacer(cantidad: int)
	}
	NecesidadMaterial <|-- NecesidadExtraordinaria
	NecesidadMaterial <|-- NecesidadRecurrente
	EntidadBenefica "1" o-- "*" NecesidadMaterial


````
