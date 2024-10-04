package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.repositories.ColaboradorMapper;
import ar.edu.utn.dds.k3003.persist.ColaboradorRepository;

import javax.persistence.EntityManagerFactory;
import java.util.List;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaColaboradores {
    public final ColaboradorRepository colaboradorRepository;
    private final ColaboradorMapper colaboradorMapper;
    public Double pesosDonadosPeso;
    public Double viandasDistribuidasPeso;
    public Double viandasDonadasPeso;
    public Double tarjetasRepartidasPeso;
    public Double heladerasActivasPeso;
    private FachadaViandas fachadaViandas;
    private FachadaLogistica fachadaLogistica;

    public Fachada(EntityManagerFactory entityManagerFactory) {
        this.colaboradorRepository = new ColaboradorRepository(entityManagerFactory);
        this.colaboradorMapper = new ColaboradorMapper();
    }

    @Override
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

    @Override
    public ColaboradorDTO buscarXId(Long colaboradorId) {
        Colaborador colaborador = colaboradorRepository.findById(colaboradorId);
        return colaboradorMapper.map(colaborador);
    }

    @Override
    public void actualizarPesosPuntos(Double pesosDonados , Double viandasDistribuidas, Double viandasDonadas,
                                      Double tarjetasRepartidas, Double heladerasActivas){
        pesosDonadosPeso = pesosDonados;
        viandasDistribuidasPeso = viandasDistribuidas;
        viandasDonadasPeso =viandasDonadas;
        tarjetasRepartidasPeso = tarjetasRepartidas;
        heladerasActivasPeso = heladerasActivas;
    }
    @Override
    public Double puntos(Long colaboradorId){// Calcular puntos
        return viandasDistribuidas(colaboradorId) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId) * viandasDonadasPeso
                /* + pesosDonados(colaboradorId) * pesosDonadosPeso +
                tarjetasRepartidas(colaboradorId) * tarjetasRepartidasPeso +
                heladerasActivas(colaboradorId) * heladerasActivasPeso*/
                ;}

    public Long viandasDonadas(Long colaboradorId){
        List<ViandaDTO> viandas =  fachadaViandas.viandasDeColaborador(colaboradorId,10,2024);
        return (long) viandas.size();
    }
    public Long viandasDistribuidas(Long colaboradorId){
        List<TrasladoDTO> traslados =  fachadaLogistica.trasladosDeColaborador(colaboradorId,10,2024);
        return (long) traslados.size();
    }
    public Double puntosAnioMes(Long colaboradorId, Integer mes, Integer anio){
        return viandasDistribuidas(colaboradorId,mes,anio) * viandasDistribuidasPeso +
                viandasDonadas(colaboradorId,mes,anio) * viandasDonadasPeso
                /* + pesosDonados(colaboradorId) * pesosDonadosPeso +
                tarjetasRepartidas(colaboradorId) * tarjetasRepartidasPeso +
                heladerasActivas(colaboradorId) * heladerasActivasPeso*/
                ;}

    public Long viandasDonadas(Long colaboradorId, Integer mes, Integer anio){
        List<ViandaDTO> viandas =  fachadaViandas.viandasDeColaborador(colaboradorId,mes,anio);
        return (long) viandas.size();
    }
    public Long viandasDistribuidas(Long colaboradorId, Integer mes, Integer anio){
        List<TrasladoDTO> traslados =  fachadaLogistica.trasladosDeColaborador(colaboradorId,mes,anio);
        return (long) traslados.size();
    }
    /*public Long pesosDonados(Long colaboradorId){
        return 0L;
    }

    public Long tarjetasRepartidas(Long colaboradorId){
        return 0L;
    }

    public Long heladerasActivas(Long colaboradorId){
        //[CANTIDAD_HELADERAS_ACTIVAS] * [∑ MESES_ACTIVAS]
        return 0L;
    }*/


    @Override
    public ColaboradorDTO modificar(Long colaboradorId, List<FormaDeColaborarEnum> formaDeColaborar){
        ColaboradorDTO colaborador = buscarXId(colaboradorId);
        colaboradorRepository.remove(colaboradorId);
        ColaboradorDTO colaboradorCambiado = new ColaboradorDTO(colaborador.getNombre(), formaDeColaborar);
        colaboradorCambiado.setId(colaboradorId);
        return agregarConID(colaboradorCambiado,colaboradorId);
    }

    @Override
    public void setLogisticaProxy(FachadaLogistica fachadaLogistica) {
        this.fachadaLogistica = fachadaLogistica;
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
        this.fachadaViandas = fachadaViandas;
    }
}