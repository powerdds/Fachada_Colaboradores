package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;

import java.util.NoSuchElementException;

public class ColaboradorMapper {
    public ColaboradorDTO map(Colaborador colaborador){
       try{ ColaboradorDTO colaboradorDTO =  new ColaboradorDTO(colaborador.getNombre(), colaborador.getFormas() , colaborador.getPesosDonados() , colaborador.getHeladerasReparadas());
        colaboradorDTO.setId(colaborador.getId());
        return colaboradorDTO;}
       catch(Exception e){
           throw new NoSuchElementException(" ");
       }
    }
}