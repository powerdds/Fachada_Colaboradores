package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.PuntosBody;
import ar.edu.utn.dds.k3003.model.FormaDeColaborar;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

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


    /*public Double promedio(){
        return this.puntosColaboradores.count() / colaboradoresCounter.count();
    }*/

    public void agregar(Context context) {
        var colaboradorDTO = context.bodyAsClass(ColaboradorDTO.class);
        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        var colaboradorDTORta = this.fachada.agregar(colaboradorDTO);
        colaboradoresCounter.increment();
        registry.config().commonTags("app", "metrics-colaborador");
        context.json(colaboradorDTORta);
        context.status(HttpStatus.CREATED);
    }

    public void obtener(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var colaboradorDTO = this.fachada.buscarXId(id);
            context.status(HttpStatus.OK);
            context.json(colaboradorDTO);
        } catch (NoSuchElementException ex) {
            context.result("Colaborador " + id + " no encontrado" + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
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

    public void modificar(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var forma = context.bodyAsClass(FormaDeColaborar.class);
        try {
            var colaboradorDTO = this.fachada.modificar(id, forma.getFormas());
            cambiosEstadoCounter.increment();
            final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
            registry.config().commonTags("app", "metrics-colaborador");
            //new MicrometerPlugin(config -> config.registry = registry);
            context.status(HttpStatus.OK);
            context.result("Se modificó correctamente el colaborador \n");
            context.json(colaboradorDTO);
        } catch (NoSuchElementException ex) {
            context.result("No se pudo modificar el colaborador"); //ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void puntosAnioMes(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio", Integer.class).get();
        var mes = context.queryParamAsClass("mes", Integer.class).get();
        Colaborador colaborador = this.fachada.colaboradorRepository.findById(id);
        try {
            var puntosColaborador = this.fachada.puntosAnioMes(id, mes, anio);
            if(!colaborador.getPuntosCalculados()) {
                puntosColaboradores.increment(puntosColaborador);
                final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
                registry.config().commonTags("app", "metrics-colaborador");
                colaborador.setPuntosCalculados(true);
            }
            context.result("Puntos del colaborador " + id + " :" + puntosColaborador);
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
            var viandasDonadas = this.fachada.viandasDonadas(id,mes,anio) * this.fachada.viandasDonadasPeso;
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
            var viandasDistribuidas = this.fachada.viandasDistribuidas(id,mes,anio) * this.fachada.viandasDistribuidasPeso;
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
            this.fachada.actualizarPesosPuntos(pesosDonados,
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
        ColaboradorDTO colaborador1 = new ColaboradorDTO("Pepe", List.of(FormaDeColaborarEnum.DONADORDINERO));
        ColaboradorDTO colaborador2 = new ColaboradorDTO("Jose", List.of(FormaDeColaborarEnum.TRANSPORTADOR));
        ColaboradorDTO colaborador3 = new ColaboradorDTO("Laura", List.of(FormaDeColaborarEnum.DONADORVIANDA));
        ColaboradorDTO colaborador4 = new ColaboradorDTO("Lolo", List.of(FormaDeColaborarEnum.TECNICO));
        ColaboradorDTO colaborador5 = new ColaboradorDTO("Maria", List.of(FormaDeColaborarEnum.TRANSPORTADOR));
        this.fachada.agregar(colaborador1);
        this.fachada.agregar(colaborador2);
        this.fachada.agregar(colaborador3);
        this.fachada.agregar(colaborador4);
        this.fachada.agregar(colaborador5);
        this.fachada.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0);
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
