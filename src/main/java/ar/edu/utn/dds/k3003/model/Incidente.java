package ar.edu.utn.dds.k3003.model;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class Incidente {
    private Long heladeraId;

    private LocalDateTime fechaOcurrencia;

    private TipoAlerta tipoIncidente;

    public Incidente(Long heladeraId, TipoAlerta tipoIncidente){
        this.heladeraId = heladeraId;
        this.fechaOcurrencia = LocalDateTime.now();
        this.tipoIncidente = tipoIncidente;
    }
}