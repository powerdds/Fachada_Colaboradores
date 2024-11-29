package ar.edu.utn.dds.k3003.model.DTOs;

import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public final class ColaboradorRtaDTO {
    private String nombre;
    private List<FormaDeColaborarEnum> formas;
    private List<Donacion> donaciones;
    private Long heladerasReparadas;

    public ColaboradorRtaDTO(String nombre, List<FormaDeColaborarEnum> formas , List<Donacion> donaciones , Long heladerasReparadas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = donaciones;
        this.heladerasReparadas = heladerasReparadas;
    }

    public ColaboradorRtaDTO(ColaboradorDTO colaboradorDTO) {
        this.nombre = colaboradorDTO.getNombre();
        this.formas = colaboradorDTO.getFormas();
        this.donaciones = colaboradorDTO.getDonaciones();
        this.heladerasReparadas = colaboradorDTO.getHeladerasReparadas();
    }

    protected ColaboradorRtaDTO() {
    }
}
