package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.model.DTOs.AlertaDTO;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorRtaDTO;
import ar.edu.utn.dds.k3003.model.DTOs.DonacionDTO;
import ar.edu.utn.dds.k3003.model.Donacion;
import ar.edu.utn.dds.k3003.model.PuntosBody;
import ar.edu.utn.dds.k3003.model.FormaDeColaborar;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;



public class ColaboradorController {
    private final Fachada fachada;
    private EntityManagerFactory entityManagerFactory;

    public ColaboradorController(Fachada fachada, EntityManagerFactory entityManagerFactory,
                                 Counter colaboradoresCounter, Counter cambiosEstadoCounter, Counter puntosColaboradores) {
        this.entityManagerFactory = entityManagerFactory;
        this.fachada = fachada;
        this.cambiosEstadoCounter = cambiosEstadoCounter;
        this.colaboradoresCounter = colaboradoresCounter;
        this.puntosColaboradores = puntosColaboradores;
    }

    private Counter cambiosEstadoCounter;
    private Counter colaboradoresCounter;
    private Counter puntosColaboradores;
    private List<Long> colaboradoresPuntos = new ArrayList<Long>();


    public void agregar(Context context) {
        var colaboradorDTO = context.bodyAsClass(ColaboradorDTO.class);
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        var colaboradorDTORta = fachada.agregar(colaboradorDTO);
        colaboradoresCounter.increment();
        registry.config().commonTags("app", "metrics-colaborador");
        context.json(new ColaboradorRtaDTO(colaboradorDTORta));
        context.status(HttpStatus.CREATED);
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var colaboradorDTO = this.fachada.buscarXId(id);
            context.status(HttpStatus.OK);
            context.json(new ColaboradorRtaDTO(colaboradorDTO));
        } catch (NoSuchElementException ex) {
            context.status(404).result("Colaborador " + id + " no encontrado " + ex.getMessage());
        }
    }
    public void obtenerColaboradores(Context context) {
        try {
            var colaboradorDTO = this.fachada.colaboradorRepository.list();
            context.status(HttpStatus.OK);
            context.json(colaboradorDTO);
        } catch (NoSuchElementException ex) {
            context.result("Colaboradores no encontrados" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void obtenerDonaciones(Context context) {
        try {
            var donacionesDTO = this.fachada.colaboradorRepository.listDonacion();
            context.status(HttpStatus.OK);
            context.json(donacionesDTO);
        } catch (NoSuchElementException ex) {
            context.result("Donaciones no encontradas" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void cambiarForma(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var forma = context.bodyAsClass(FormaDeColaborar.class);
        try {
            var colaboradorDTO = fachada.modificarFormas(id, forma.getFormas());
            cambiosEstadoCounter.increment();
            final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            registry.config().commonTags("app", "metrics-colaborador");
            //new MicrometerPlugin(config -> config.registry = registry);
            context.status(HttpStatus.OK);
            context.result("Se modificó correctamente la forma de colaborar del colaborador " + id);
            context.json(new ColaboradorRtaDTO(colaboradorDTO));
        } catch (NoSuchElementException ex) {
            context.result("No se pudo modificar el colaborador"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void donarPesos(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        var donacion = context.bodyAsClass(DonacionDTO.class);
        try {
            var colaboradorDTO = fachada.donar(id,donacion);
            context.status(HttpStatus.OK);
            context.result("El colaborador donó correctamente \n");
            context.json(new ColaboradorRtaDTO(colaboradorDTO));
        } catch (NoSuchElementException ex) {
            context.result("No se pudo donar el dinero \n"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /*public void formaDeColaborar(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            ColaboradorDTO colaboradorDTO = fachada.buscarXId(id);
            List<FormaDeColaborarEnum> forma = colaboradorDTO.getFormas();
            context.status(HttpStatus.OK);
            context.json(forma);
        } catch (NoSuchElementException ex) {
            context.result("No se realizar la verificación"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }*/

    /*public void suscribirseMin(Context context){
        var colaboradorId = context.pathParamAsClass("id", Long.class).get();
        var heladeraId = context.queryParamAsClass("heladeraId", Long.class).get();
        var min = context.queryParamAsClass("min", Long.class).get();
        try {
            ColaboradorDTO colaboradorDTO =  fachada.suscribirseMin(colaboradorId,min ,heladeraId);
            context.status(HttpStatus.OK);
            context.result("El colaborador " + colaboradorId + " se ha suscripto correctamente a la heladera " + heladeraId);
            context.json(colaboradorDTO);

        } catch (NoSuchElementException ex) {
            context.result("No se suscribir a la heladera"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }*/

    public void reportar(Context context){
        var alerta = context.bodyAsClass(AlertaDTO.class);
        try {
            fachada.notificarIncidente(alerta);
            context.status(HttpStatus.OK);
            context.result("Se reportó correctamente");
        } catch (NoSuchElementException ex) {
            context.result("No se pudo reportar"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void asignarTraslado(Context context){
        var traslado = context.bodyAsClass(TrasladoDTO.class);
        try {
            fachada.notificarTraslado(traslado);
            context.status(HttpStatus.OK);
            context.result("Se asignó correctamente");
        } catch (NoSuchElementException ex) {
            context.result("No se pudo asignar el traslado"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void repararHeladera(Context context){
        var colaboradorId = context.pathParamAsClass("id", Long.class).get();
        var heladeraId = context.pathParamAsClass("heladeraId", Long.class).get();
        try{
            fachada.repararHeladera(colaboradorId, heladeraId);
            context.status(HttpStatus.OK);
            context.result("El colaborador " + colaboradorId + " pudo reparar la heladera");
        } catch (NoSuchElementException ex) {
            context.result("No se pudo reparar la heladera"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void puntosAnioMes(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio", Integer.class).get();
        var mes = context.queryParamAsClass("mes", Integer.class).get();
        try {
            var puntosColaborador = fachada.puntosAnioMes(id, mes, anio);
            if(!colaboradoresPuntos.contains(id)) {
                puntosColaboradores.increment(puntosColaborador);
                final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                registry.config().commonTags("app", "metrics-colaborador");
                colaboradoresPuntos.add(id);
            }
            context.result("Puntos del colaborador " + id + " :" + puntosColaborador );
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener los puntos del colaborador " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }
    public void puntosViandasDonadasAnioMes(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio", Integer.class).get();
        var mes = context.queryParamAsClass("mes", Integer.class).get();
        try {
            var viandasDonadas = fachada.viandasDonadas(id,mes,anio) * fachada.viandasDonadasPeso;
            context.result("Puntos de viandas donadas: " + viandasDonadas);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas donadas \n" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void puntosViandasDistribuidasAnioMes(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio", Integer.class).get();
        var mes = context.queryParamAsClass("mes", Integer.class).get();
        try {
            var viandasDistribuidas = fachada.viandasDistribuidas(id,mes,anio) * fachada.viandasDistribuidasPeso;
            context.result("Puntos de viandas distribuidas: " + viandasDistribuidas);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas distribuidas \n" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void actualizarPuntos(Context context) {//ok
        PuntosBody puntos = context.bodyAsClass(PuntosBody.class);
        Double pesosDonados = puntos.getPesosDonados();
        Double viandasDistribuidas = puntos.getViandasDistribuidas();
        Double viandasDonadas = puntos.getViandasDonadas();
        Double heladerasReparadas = puntos.getHeladerasReparadas();
        try {
            fachada.actualizarPesosPuntos(pesosDonados,
                    viandasDistribuidas,
                    viandasDonadas,
                    heladerasReparadas);
            context.result("Puntos actualizados: \n"
                    + " peso Pesos = " + pesosDonados + "\n"
                    + " peso Viandas Distribuidas = " + viandasDistribuidas + "\n"
                    + " peso Viandas Donadas = " + viandasDonadas + "\n"
                    + " peso Heladeras Reparadas = " + heladerasReparadas)
            ;
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron actualizar los puntos " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void prueba(Context context) {

        ColaboradorDTO colaborador1 = new ColaboradorDTO("Aylen", List.of(FormaDeColaborarEnum.DONADORDINERO),null,0L);
        ColaboradorDTO colaborador2 = new ColaboradorDTO("Javier", List.of(FormaDeColaborarEnum.TRANSPORTADOR),null,0L);
        ColaboradorDTO colaborador3 = new ColaboradorDTO("Eduardo", List.of(FormaDeColaborarEnum.DONADORVIANDA),null,0L);
        ColaboradorDTO colaborador4 = new ColaboradorDTO("Sabrina", List.of(FormaDeColaborarEnum.TECNICO),null,0L);
        ColaboradorDTO colaborador5 = new ColaboradorDTO("Daniel", List.of(FormaDeColaborarEnum.TRANSPORTADOR),null,0L);
        fachada.agregar(colaborador1);
        fachada.agregar(colaborador2);
        fachada.agregar(colaborador3);
        fachada.agregar(colaborador4);
        fachada.agregar(colaborador5);
        fachada.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0);
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        colaboradoresCounter.increment(5);
        registry.config().commonTags("app", "metrics-colaborador");
        context.status(HttpStatus.OK);
        context.result("Prueba lista! ");
    }

    public void clean(Context context) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try{
            em.createQuery("DELETE FROM Colaborador").executeUpdate();
            em.getTransaction().commit();
            context.result("Se borraron los datos correctamente.");
        }catch(RuntimeException e){
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            context.result("Error al borrar datos.");
            context.status(500);
            throw e;
        } finally{
            em.close();
        }
    }


}
