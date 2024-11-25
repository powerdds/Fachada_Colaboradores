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

    @Column
    @ManyToOne
    private List<Donacion> donaciones;

    @Column
    private  Long heladerasReparadas;

    public Colaborador(String nombre, List<FormaDeColaborarEnum> formas, List<Donacion> donaciones, Long heladerasReparadas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = donaciones;
        this.heladerasReparadas = heladerasReparadas;
    }

    public Colaborador() {
        super();
    }

}
