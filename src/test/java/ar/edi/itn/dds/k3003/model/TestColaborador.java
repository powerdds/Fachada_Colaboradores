package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaColaboradores;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;

import static ar.edu.utn.dds.k3003.tests.TestTP.PAQUETE_BASE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestColaborador {
    Fachada fColaboradores;
    ColaboradorDTO colaborador;
    ColaboradorDTO colaborador2;
    List<FormaDeColaborarEnum> formaDeColaborar;
    @Mock FachadaLogistica logistica;
    @Mock FachadaViandas viandas;
    String nombre1;
    public static EntityManagerFactory entityManagerFactory; //agregado
    @BeforeEach
    void setUp() {
        fColaboradores = new Fachada(entityManagerFactory); // this.instance();
        formaDeColaborar = List.of(FormaDeColaborarEnum.DONADOR);
        colaborador = new ColaboradorDTO("Juana" , formaDeColaborar);
        nombre1 = "Juan";
        //logistica = new FachadaLogistica();
        fColaboradores.setLogisticaProxy(logistica);
        fColaboradores.setViandasProxy(viandas);
    }

    @Test
    public void agregarColaboradorTest(){
        ColaboradorDTO colaborador1 = fColaboradores.agregar(colaborador);
        assertNotNull(colaborador1.getId(),"Se agrego el colaborador");
    }

    @Test
    public void buscarXIdTest(){
        ColaboradorDTO colaborador1 = fColaboradores.agregar(colaborador);
        assertNotNull(fColaboradores.buscarXId(colaborador1.getId()) , "El colaborador fue encontrado");
    }

    @Test
    public void modificarEstadoColaboradorTest(){
        ColaboradorDTO colaborador3 = fColaboradores.agregar(colaborador);
        colaborador2 = fColaboradores.modificar(colaborador3.getId(), List.of(FormaDeColaborarEnum.TRANSPORTADOR));
        assertNotEquals(colaborador2.getFormas() , formaDeColaborar,"Se cambio la forma de colaborar");
    }

    @Test
    public void puntosConViandasTest(){
        fColaboradores.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0, 5.0);
        var trasladoDTO = new TrasladoDTO("qrV", 2, 3);
        var viandaDTO = new ViandaDTO("codQ", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaborador.getId(), 20);
        when(logistica.trasladosDeColaborador(colaborador.getId(), 1, 2024)).thenReturn(List.of(trasladoDTO));
        when(viandas.viandasDeColaborador(colaborador.getId(), 1, 2024)).thenReturn(List.of(viandaDTO));
        Double puntos = fColaboradores.puntos(colaborador.getId());
        assertTrue(puntos > 0 ,"El colaborador dono y transporto viandas" );
    }

    @Test
    public void actualizarPesosPuntosTest(){
        fColaboradores.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0, 5.0);
        var trasladoDTO = new TrasladoDTO("qrV", 2, 3);
        var viandaDTO = new ViandaDTO("codQ", LocalDateTime.now(), EstadoViandaEnum.EN_TRASLADO, colaborador.getId(), 20);
        when(logistica.trasladosDeColaborador(colaborador.getId(), 1, 2024)).thenReturn(List.of(trasladoDTO));
        when(viandas.viandasDeColaborador(colaborador.getId(), 1, 2024)).thenReturn(List.of(viandaDTO));
        Double puntos = fColaboradores.puntos(colaborador.getId());
        fColaboradores.actualizarPesosPuntos(0.0, 2.0, 2.0, 0.0, 0.0);
        assertNotEquals(puntos , fColaboradores.puntos(colaborador.getId()) , "Se actualizaron los puntos");
    }

    //@Override
    public String paquete() {
        return PAQUETE_BASE + "tests.colaboradores";
    }

    // @Override
    public Class<FachadaColaboradores> clase() {
        return FachadaColaboradores.class;
    }
}