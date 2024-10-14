package ar.edu.utn.dds.k3003.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor

@Entity
public class Colaborador {

    @Id
    private Long id;
    @Column
    private String nombre;

    //@Transient
    @Column(name = "formasDeColaborar")
    @Convert ( converter = ConversorFormasDeColaborar.class)
    private List<FormaDeColaborarEnum> formas;

    @Transient
    private boolean puntosCalculados;

    public Colaborador(String nombre, List<FormaDeColaborarEnum> formas) {
        this.nombre = nombre;
        this.formas = formas;
        this.puntosCalculados = false;
    }

    public Colaborador() {
        super();
    }

    public boolean getPuntosCalculados(){
        return puntosCalculados;
    }
}
