package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class Donacion {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    public int valor;
    @Column
    public Date fecha;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "donacionColaborador")
    private Colaborador colaborador;

    public Donacion( int valor , Date fecha, Colaborador colaborador) {
        this.valor = valor;
        this.fecha = fecha;
        this.colaborador = colaborador;
    }

    public Donacion() {
        super();
    }
}
