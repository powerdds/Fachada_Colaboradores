package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;

import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.persist.ColaboradorRepository;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.NoSuchElementException;

public class Fachada {
    public final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper colaboradorMapper;
    public Double pesosDonadosPeso;
    public Double viandasDistribuidasPeso;
    public Double viandasDonadasPeso;
    public Double heladerasReparadasPeso;
    private FachadaViandas fachadaViandas;
    private FachadaLogistica fachadaLogistica;
    private FachadaHeladeras fachadaHeladeras;

    public Fachada(EntityManagerFactory entityManagerFactory) {
        this.colaboradorRepository = new ColaboradorRepository(entityManagerFactory);
        this.colaboradorMapper = new ColaboradorMapper();
    }

    public ColaboradorDTO agregar(ColaboradorDTO colaboradorDto) {
        Colaborador colaborador = new Colaborador(colaboradorDto.getNombre() , colaboradorDto.getFormas());
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }
    public ColaboradorDTO agregarConID(ColaboradorDTO colaboradorDto, Long id) {
        Colaborador colaborador = new Colaborador(colaboradorDto.getNombre() , colaboradorDto.getFormas());
        colaborador.setId(id);
        colaborador = this.colaboradorRepository.save(colaborador);
        return colaboradorMapper.map(colaborador);
    }

    public ColaboradorDTO buscarXId(Long colaboradorId)throws NoSuchElementException {
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId) ;//.orElseThrow(() ->
               // new NoSuchElementException("Colaborador no encontrado id: " + colaboradorId));
        return colaboradorMapper.map(colaborador);
    }

    public void actualizarPesosPuntos(Double pesosDonados , Double viandasDistribuidas, Double viandasDonadas,
                                      Double heladerasReparadas){
        pesosDonadosPeso = pesosDonados;
        viandasDistribuidasPeso = viandasDistribuidas;
        viandasDonadasPeso =viandasDonadas;
        heladerasReparadasPeso = heladerasReparadas;
    }

    public Double puntos(Long colaboradorId){
        return viandasDistribuidas(colaboradorId,9,2024) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId ,9,2024) * viandasDonadasPeso
                /* + pesosDonados(colaboradorId) * pesosDonadosPeso +
                heladerasReparadas(colaboradorId) * heladerasReparadasPeso*/
                ;}

    public Double puntosAnioMes(Long colaboradorId, Integer mes, Integer anio){
        return viandasDistribuidas(colaboradorId,mes,anio) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId,mes,anio) * viandasDonadasPeso
                /* + pesosDonados(colaboradorId) * pesosDonadosPeso +
                heladerasReparadas(colaboradorId) * heladerasReparadasPeso*/
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
        return 0L;
    }

    public Long heladerasReparadas(Long colaboradorId){
        //[CANTIDAD_HELADERAS_REPADARAS]
        return 0L;
    }

    public ColaboradorDTO modificar(Long colaboradorId, List<FormaDeColaborarEnum> formaDeColaborar){
        ColaboradorDTO colaborador = buscarXId(colaboradorId);
        colaboradorRepository.remove(colaboradorId);
        ColaboradorDTO colaboradorCambiado = new ColaboradorDTO(colaborador.getNombre(), formaDeColaborar);
        colaboradorCambiado.setId(colaboradorId);
        return agregarConID(colaboradorCambiado,colaboradorId);
    }

    public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {
        this.fachadaLogistica = fachadaLogistica;
    }

    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }

    public void setHeladerasProxy(FachadaHeladeras fachadaHeladeras) {
        this.fachadaHeladeras = fachadaHeladeras;
    }
}