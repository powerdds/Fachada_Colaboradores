package ar.edu.utn.dds.k3003.model.DTOs;

import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
/*
* @Data
* @NoArgConstructor*/
public final class ColaboradorDTO {
    private Long id;
    private String nombre;
    private List<FormaDeColaborarEnum> formas;
    private List<Donacion> donaciones;
    private Long heladerasReparadas;
    private Long minimoViandas;
    private Long maximoViandas;
    private boolean incidente;

    public ColaboradorDTO(String nombre, List<FormaDeColaborarEnum> formas , List<Donacion> donaciones , Long heladerasReparadas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = donaciones;
        this.minimoViandas = -1L;
        this.maximoViandas = -1L;
        this.incidente = false;
        this.heladerasReparadas = heladerasReparadas;
    }
    public ColaboradorDTO donar(Donacion donacion){
        donaciones.add(donacion);
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO)) {
            return false;
        } else {
            ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO other;
            label44: {
                other = (ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO)o;
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id == null) {
                        break label44;
                    }
                } else if (this$id.equals(other$id)) {
                    break label44;
                }

                return false;
            }

            Object this$nombre = this.getNombre();
            Object other$nombre = other.getNombre();
            if (this$nombre == null) {
                if (other$nombre != null) {
                    return false;
                }
            } else if (!this$nombre.equals(other$nombre)) {
                return false;
            }

            Object this$formas = this.getFormas();
            Object other$formas = other.getFormas();
            if (this$formas == null) {
                if (other$formas != null) {
                    return false;
                }
            } else if (!this$formas.equals(other$formas)) {
                return false;
            }

            return true;
        }
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $nombre = this.getNombre();
        result = result * 59 + ($nombre == null ? 43 : $nombre.hashCode());
        Object $formas = this.getFormas();
        result = result * 59 + ($formas == null ? 43 : $formas.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "ColaboradorDTO(id=" + var10000 + ", nombre=" + this.getNombre() + ", formas=" + this.getFormas() + ")";
    }

    protected ColaboradorDTO() {
    }
}

