package ar.edu.utn.dds.k3003.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

public enum FormaDeColaborarEnum {
    /*@ManyToMany
    @JoinColumn(name = "formasDeColaborar", nullable = false)*/
    DONADORVIANDA,
    TRANSPORTADOR,
    TECNICO,
    DONADORDINERO;


    private FormaDeColaborarEnum() {
    }
}