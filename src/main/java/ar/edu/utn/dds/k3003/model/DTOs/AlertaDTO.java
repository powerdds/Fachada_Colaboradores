package ar.edu.utn.dds.k3003.model.DTOs;

import ar.edu.utn.dds.k3003.model.TipoAlerta;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Getter
public class AlertaDTO {
    private Integer heladeraId;
    private TipoAlerta tipoAlerta;
    private List<Integer> colaboradoresId;

    public AlertaDTO(Integer heladeraId, TipoAlerta tipoIncidente) {
        this.heladeraId = heladeraId;
        this.tipoAlerta = tipoIncidente;
    }
}
