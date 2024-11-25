package ar.edu.utn.dds.k3003.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
public class Donacion {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    public int valor;
    @Column
    public Date fecha;

    public Donacion(int valor , Date fecha){
        this.valor = valor;
        this.fecha = fecha;
    }
}
