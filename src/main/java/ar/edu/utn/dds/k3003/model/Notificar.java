package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;

public class Notificar {

    public void alerta(Incidente incidente, ColaboradorDTO colaboradorDTO){ //ok
        System.out.print("Se notifica al colaborador "+ colaboradorDTO.getId() + " que la heladera " +
        incidente.getHeladeraId() + " sufrió un incidente de tipo " + incidente.getTipoIncidente() + "\n");
    }
}
