package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.DTOs.AlertaDTO;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;

import ar.edu.utn.dds.k3003.model.Incidente;
import ar.edu.utn.dds.k3003.model.Notificar;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.persist.ColaboradorRepository;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.NoSuchElementException;

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
        Colaborador colaborador = new Colaborador(colaboradorDto.getNombre() , colaboradorDto.getFormas(),0L,0L);
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }
    public ColaboradorDTO agregarConID(ColaboradorDTO colaboradorDto, Long id) {
        Colaborador colaborador = new Colaborador(colaboradorDto.getNombre() , colaboradorDto.getFormas(),colaboradorDto.getPesosDonados(), colaboradorDto.getHeladerasReparadas());
        colaborador.setId(id);
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }

    public ColaboradorDTO buscarXId(Long colaboradorId)throws NoSuchElementException {
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId);/*.orElseThrow(() ->
               new NoSuchElementException("Colaborador no encontrado id: " + colaboradorId));*/
        return colaboradorMapper.map(colaborador);
    }

    public ColaboradorDTO modificarFormas(Long colaboradorId, List<FormaDeColaborarEnum> formaDeColaborar){
        ColaboradorDTO colaboradorDTO = buscarXId(colaboradorId);
        colaboradorRepository.remove(colaboradorId);
        /*ColaboradorDTO colaboradorCambiado =
                new ColaboradorDTO(colaboradorDTO.getNombre(),
                        formaDeColaborar);
        colaboradorCambiado.setId(colaboradorId);
        if (colaboradorDTO.getPesosDonados()>0){
        colaboradorCambiado.setPesosDonados(colaboradorDTO.getPesosDonados());}*/
        colaboradorDTO.setFormas(formaDeColaborar);
        return agregarConID(colaboradorDTO,colaboradorId);
    }

    public ColaboradorDTO modificarPesos(Long colaboradorId, Long pesos){
        ColaboradorDTO colaboradorDTO = buscarXId(colaboradorId);
        colaboradorRepository.remove(colaboradorId);
        colaboradorDTO.incrementPesosDonados(pesos);
        return agregarConID(colaboradorDTO,colaboradorId);
    }

    /*public boolean colaboradorEs(Long colaboradorId , FormaDeColaborarEnum forma){
        ColaboradorDTO colab = buscarXId(colaboradorId);
        return colab.getFormas().contains(forma);
    }*/

    public void repararHeladera(Long colaboradorId , Long heladeraId){//revisar
        
        if(colaboradorEs(colaboradorId , TECNICO)) {
            //fachadaHeladeras.reparar(heladeraId);
            añadirReparo(colaboradorId);//ok
        }
        else {
            System.out.print("El colaborador no es un tecnico");
        }
    }

    public boolean colaboradorEs(Long id, FormaDeColaborarEnum forma){
        ColaboradorDTO colaboradorDTO = buscarXId(id);
        return colaboradorDTO.getFormas().contains(forma);
    }

    public void añadirReparo(Long colaboradorId){
        ColaboradorDTO colaboradorDTO = buscarXId(colaboradorId);
        colaboradorRepository.remove(colaboradorId);
        colaboradorDTO.incrementHeladerasReparadas();
        agregarConID(colaboradorDTO,colaboradorId);
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
        viandasDonadasPeso =viandasDonadas;
        heladerasReparadasPeso = heladerasReparadas;
    }

    public Double puntos(Long colaboradorId){
        return viandasDistribuidas(colaboradorId,10,2024) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId ,10,2024) * viandasDonadasPeso
                /* + pesosDonados(colaboradorId) * pesosDonadosPeso +
                heladerasReparadas(colaboradorId) * heladerasReparadasPeso*/
                ;}

    public Double puntosAnioMes(Long colaboradorId, Integer mes, Integer anio){
        return viandasDistribuidas(colaboradorId,mes,anio) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId,mes,anio) * viandasDonadasPeso
                 + pesosDonados(colaboradorId) * pesosDonadosPeso +
                heladerasReparadas(colaboradorId) * heladerasReparadasPeso
                ;}

    public Long viandasDonadas(Long colaboradorId, Integer mes, Integer anio){
        List<ViandaDTO> viandas =  fachadaViandas.viandasDeColaborador(colaboradorId,mes,anio);
        return (long) viandas.size();
    }
    public Long viandasDistribuidas(Long colaboradorId, Integer mes, Integer anio){
        List<TrasladoDTO> traslados =  fachadaLogistica.trasladosDeColaborador(colaboradorId,mes,anio);
        return (long) traslados.size();
    }
    public Long pesosDonados(Long colaboradorId){
        return buscarXId(colaboradorId).getPesosDonados();
    }

    public Long heladerasReparadas(Long colaboradorId){
        return buscarXId(colaboradorId).getHeladerasReparadas();

    }

    public void notificarIncidente(AlertaDTO alerta){
        Incidente incidente = new Incidente(Long.valueOf(alerta.getHeladeraId()),alerta.getTipoAlerta());
        alerta.getColaboradoresId().forEach(id -> {notificador.alerta(incidente,buscarXId(Long.valueOf(id)));});
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
