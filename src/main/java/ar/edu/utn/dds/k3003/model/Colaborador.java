package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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

    @Column(name = "formasDeColaborar")
    @Convert ( converter = ConversorFormasDeColaborar.class)
    private List<FormaDeColaborarEnum> formas;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "colaborador", cascade = CascadeType.ALL)
    private List<Donacion> donaciones;

    @Column
    private Long heladerasReparadas;

    public Colaborador(String nombre, List<FormaDeColaborarEnum> formas, List<Donacion> donaciones, Long heladerasReparadas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = donaciones;
        this.heladerasReparadas = heladerasReparadas;
    }
    public Colaborador(String nombre, List<FormaDeColaborarEnum> formas) {
        this.nombre = nombre;
        this.formas = formas;
        this.donaciones = new ArrayList<Donacion>();
        this.heladerasReparadas = 0L;
    }

    public Colaborador() {
        super();
    }


    public void donar(Donacion donacion){
        donaciones.add(donacion);
    }

    public int getValorDonaciones(){
        return donaciones.stream().mapToInt(Donacion::getValor).sum();
    }

    public void incrementHeladerasReparadas(){
        heladerasReparadas++;
    }

}
