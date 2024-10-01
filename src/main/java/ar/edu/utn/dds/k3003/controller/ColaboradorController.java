package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.Colaborador;
import ar.edu.utn.dds.k3003.model.PuntosBody;
import ar.edu.utn.dds.k3003.model.FormaDeColaborar;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ColaboradorController {
    private final Fachada fachada;
    private EntityManagerFactory entityManagerFactory;
    public int contadorCambios = 0;

    public ColaboradorController(Fachada fachada, EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.fachada = fachada;
    }

    public void agregar(Context context) {
        var colaboradorDTO = context.bodyAsClass(ColaboradorDTO.class);
        var colaboradorDTORta = this.fachada.agregar(colaboradorDTO);
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

    public void modificar(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var forma = context.bodyAsClass(FormaDeColaborar.class);
            try {
                var colaboradorDTO = this.fachada.modificar(id, forma.getFormas());
                contadorCambios++;
                context.status(HttpStatus.OK);
                context.result("Se modificó correctamente el colaborador \n"  );
                context.json(colaboradorDTO);
            } catch (NoSuchElementException ex) {
                context.result("No se pudo modificar el colaborador" ); //ex.getLocalizedMessage());
                context.status(HttpStatus.NOT_ACCEPTABLE);
            }
    }

    public void puntosAnioMes(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio",Integer.class).get();
        var mes = context.queryParamAsClass("mes",Integer.class).get();
        try {
            var puntosColaborador = this.fachada.puntosAnioMes(id,mes,anio);
            context.result("Puntos del colaborador" +id +" :" + puntosColaborador); //PROBAR
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }
    public void puntos(Context context) {
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var puntosColaborador = this.fachada.puntos(id);
            context.result("Puntos del colaborador" +id +" :" + puntosColaborador); //PROBAR
            context.status(HttpStatus.OK);

        } catch (NoSuchElementException ex) {
            context.result(ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void actualizarPuntos(Context context) {//ok
        PuntosBody puntos = context.bodyAsClass(PuntosBody.class);
        Double pesosDonados = puntos.getPesosDonados();
        Double viandasDistribuidas = puntos.getViandasDistribuidas();
        Double viandasDonadas = puntos.getViandasDonadas();
        Double tarjetasRepartidas = puntos.getTarjetasRepartidas();
        Double heladerasActivas = puntos.getHeladerasActivas();
        try {
            this.fachada.actualizarPesosPuntos(pesosDonados,
                    viandasDistribuidas,
                    viandasDonadas,
                    tarjetasRepartidas,
                    heladerasActivas);
            context.result("Puntos actualizados: \n"
                    + " peso Pesos = " + pesosDonados + "\n"
                    + " peso Viandas Distribuidas = " + viandasDistribuidas + "\n"
                    + " peso Viandas Donadas = " + viandasDonadas + "\n"
                    + " peso Tarjetas Repartidas = " +tarjetasRepartidas + "\n"
                    + " peso Heladeras Activas = " +heladerasActivas)
            ;
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron actualizar los puntos " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void viandasDonadas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var viandasDonadas = this.fachada.viandasDonadas(id);
            context.result("Cantidad de viandas donadas: " +  viandasDonadas);
            context.status(HttpStatus.OK);

        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas donadas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void viandasDistribuidas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        try {
            var viandasDistribuidas = this.fachada.viandasDistribuidas(id);
            context.result("Cantidad de viandas distribuidas: " + viandasDistribuidas);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas distribuidas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }
    public void puntosViandasDonadas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio",Integer.class).get();
        var mes = context.queryParamAsClass("mes",Integer.class).get();
        try {
            var viandasDonadas = this.fachada.viandasDonadas(id,anio,mes) * this.fachada.viandasDonadasPeso;
            context.result("Cantidad de viandas donadas: " +  viandasDonadas);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas donadas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void puntosViandasDistribuidas(Context context){
        var id = context.pathParamAsClass("id", Long.class).get();
        var anio = context.queryParamAsClass("anio",Integer.class).get();
        var mes = context.queryParamAsClass("mes",Integer.class).get();
        try {
            var viandasDistribuidas = this.fachada.viandasDistribuidas(id,anio,mes) * this.fachada.viandasDistribuidasPeso;
            context.result("Cantidad de viandas distribuidas: " + viandasDistribuidas);
            context.status(HttpStatus.OK);
        } catch (NoSuchElementException ex) {
            context.result("No se pudieron obtener las viandas distribuidas " + ex.getLocalizedMessage());
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    public void prueba(Context context) {
        ColaboradorDTO colaborador1 = new ColaboradorDTO("Pepe", List.of(FormaDeColaborarEnum.DONADOR));
        ColaboradorDTO colaborador2 = new ColaboradorDTO("Jose", List.of(FormaDeColaborarEnum.TRANSPORTADOR));
        ColaboradorDTO colaborador3 = new ColaboradorDTO("Laura", List.of(FormaDeColaborarEnum.DONADOR));
        var colaboradorDTORta1 = this.fachada.agregar(colaborador1);
        var colaboradorDTORta2 = this.fachada.agregar(colaborador2);
        var colaboradorDTORta3 = this.fachada.agregar(colaborador3);
        this.fachada.actualizarPesosPuntos(0.5, 1.0, 1.5, 2.0, 5.0);
        context.status(HttpStatus.OK);
        context.result("Prueba lista! ");

    }

    /*public void contadorCambioEstado(Context context){
        /*myGauge.set(number);
        ctx.result("updated number: " + number.toString());
        log.info("valor gauge cambiado");
    }
    */

    public void clean(Context context) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        try {
            em.createQuery("DELETE FROM Colaborador").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE id RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}