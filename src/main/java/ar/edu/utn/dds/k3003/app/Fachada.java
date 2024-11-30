package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.model.DTOs.AlertaDTO;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;

import ar.edu.utn.dds.k3003.model.DTOs.DonacionDTO;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.persist.ColaboradorRepository;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum.DONADORDINERO;
import static ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum.TECNICO;

public class Fachada {
    public final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper colaboradorMapper;
    public Double pesosDonadosPeso;
    public Double viandasDistribuidasPeso;
    public Double viandasDonadasPeso;
    public Double heladerasReparadasPeso;
    private FachadaViandas fachadaViandas;
    private FachadaLogistica fachadaLogistica;
    private HeladeraProxy fachadaHeladeras;
    public Notificar notificador;

    public Fachada(EntityManagerFactory entityManagerFactory) {
        this.colaboradorRepository = new ColaboradorRepository(entityManagerFactory);
        this.colaboradorMapper = new ColaboradorMapper();
        this.notificador = new Notificar();
    }

    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDto) {
        Colaborador colaborador = new Colaborador(colaboradorDto.getNombre() , colaboradorDto.getFormas());
        colaborador = colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }

    public ColaboradorDTO buscarXId(Long colaboradorId)throws NoSuchElementException {
       Colaborador colaborador = colaboradorRepository.findById(colaboradorId);//.orElseThrow(()
        return colaboradorMapper.map(colaborador);
    }

    public ColaboradorDTO modificarFormas(Long colaboradorId, List<FormaDeColaborarEnum> formaDeColaborar){
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId);
        try{
            colaborador.setFormas(formaDeColaborar);
            colaboradorRepository.update(colaborador);
            return buscarXId(colaboradorId);
        }

        catch(Exception e){
            throw new NoSuchElementException("No se pudo modificar al colaborador \n");
        }
    }

    public ColaboradorDTO donar(Long colaboradorId , DonacionDTO donacionDTO){
        Colaborador donante = colaboradorRepository.findById(colaboradorId);
        Donacion donacion = new Donacion(donacionDTO.valor, donacionDTO.fecha, donante);
        if(colaboradorEs(colaboradorId , DONADORDINERO)) {
            colaboradorRepository.saveDonacion(donacion);
            donante.donar(donacion);
            colaboradorRepository.update(donante);
            return buscarXId(colaboradorId);
        }
        else throw new NoSuchElementException("El colaborador no es un DONADORDINERO \n");
    }

    public ColaboradorDTO repararHeladera(Long colaboradorId , Long heladeraId){//revisar

        if(colaboradorEs(colaboradorId , TECNICO)) {
            fachadaHeladeras.reparar(heladeraId);
            return añadirReparo(colaboradorId);
        }
        else throw new NoSuchElementException("El colaborador no es un TECNICO \n");
    }

    public boolean colaboradorEs(Long id, FormaDeColaborarEnum forma){
        ColaboradorDTO colaboradorDTO = buscarXId(id);
        return colaboradorDTO.getFormas().contains(forma);
    }

    public ColaboradorDTO añadirReparo(Long colaboradorId){
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId);
        colaborador.incrementHeladerasReparadas();
        colaboradorRepository.update(colaborador);
        return buscarXId(colaboradorId);
    }

    /*public ColaboradorDTO suscribirseMin(Long colaboradorId , Long min, Long heladeraId){
        ColaboradorDTO colaboradorDTO = buscarXId(colaboradorId);
        colaboradorDTO.setMinimoViandas(min);
        fachadaHeladeras.suscribir(heladeraId, colaboradorDTO);
        return colaboradorDTO;
    }*/


    public void actualizarPesosPuntos(Double pesosDonados , Double viandasDistribuidas, Double viandasDonadas,
                                      Double heladerasReparadas){
        pesosDonadosPeso = pesosDonados;
        viandasDistribuidasPeso = viandasDistribuidas;
        viandasDonadasPeso = viandasDonadas;
        heladerasReparadasPeso = heladerasReparadas;
    }

    public Double puntosAnioMes(Long colaboradorId, Integer mes, Integer anio){
        return viandasDistribuidas(colaboradorId,mes,anio) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId,mes,anio) * viandasDonadasPeso
                + pesosDonados(colaboradorId) * pesosDonadosPeso +
                heladerasReparadas(colaboradorId) * heladerasReparadasPeso
                ;}

    public Long viandasDonadas(Long colaboradorId, Integer mes, Integer anio){
        List<ViandaDTO> viandas =  fachadaViandas.viandasDeColaborador(colaboradorId,mes,anio);
        return (viandas != null) ? (long) viandas.size() : 0L;
    }
    public Long viandasDistribuidas(Long colaboradorId, Integer mes, Integer anio){
        List<TrasladoDTO> traslados =  fachadaLogistica.trasladosDeColaborador(colaboradorId,mes,anio);
        return (traslados != null) ? (long) traslados.size() : 0L;
    }
    public Long pesosDonados(Long colaboradorId){
        return (long) colaboradorRepository.findById(colaboradorId).getValorDonaciones();
    }

    public Long heladerasReparadas(Long colaboradorId){
        return colaboradorRepository.findById(colaboradorId).getHeladerasReparadas();
    }

    public void notificarIncidente(AlertaDTO alerta){
        Incidente incidente = new Incidente(Long.valueOf(alerta.getHeladeraId()),alerta.getTipoAlerta());
        alerta.getColaboradoresId().forEach(id -> {notificador.alerta(incidente,buscarXId(Long.valueOf(id)));});
    }

    public void notificarTraslado(Long colaboradorId , TrasladoDTO trasladoDTO){
        ColaboradorDTO colaboradorDTO = buscarXId(colaboradorId);
        notificador.notiTraslado(colaboradorDTO , trasladoDTO.getId());
    }

    public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {
        this.fachadaLogistica = fachadaLogistica;
    }

    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    public void setHeladerasProxy(HeladeraProxy fachadaHeladeras) {
        this.fachadaHeladeras = fachadaHeladeras;
    }

    }
