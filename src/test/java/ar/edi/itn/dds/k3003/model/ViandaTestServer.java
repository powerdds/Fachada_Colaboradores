package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoTrasladoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Long.parseLong;

public class ViandaTestServer {

    public static void main(String[] args) throws Exception {
        var env = System.getenv();

        var port = Integer.parseInt(env.getOrDefault("PORT", "8081"));

        var app = Javalin.create().start(port);

        app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", ViandaTestServer::obtenerViandasColaborador);

        app.get("/traslados/search/findByColaboradorId", ViandaTestServer::obtenerTrasladosColaborador);
    }

    private static void obtenerViandasColaborador(Context context) {

        var colaboradorId = parseLong(context.queryParam("colaboradorId"));
        var mes =  Integer.valueOf(context.queryParam("mes"));
        var anio = Integer.valueOf(context.queryParam("anio"));

        if(colaboradorId == 2L){
            var viandaDTO1 = new ViandaDTO("unQRQueExiste1", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);
            var viandaDTO2 = new ViandaDTO("unQRQueExiste2", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);//= fachadaViandas.viandasDeColaborador(colaboradorId,mes,anio);
            List<ViandaDTO> viandas = List.of(viandaDTO2,viandaDTO1);
            context.json(viandas);
        } else{
            context.result("Viandas no encontradas de " + colaboradorId);
            context.status(HttpStatus.NOT_FOUND);
        }
    }
    private static void obtenerTrasladosColaborador(Context context) {
        var colaboradorId = parseLong(context.queryParam("colaboradorId"));
        if(colaboradorId == 2L){
            var viandaDTO1 = new ViandaDTO("unQRQueExiste1", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);
            var viandaDTO2 = new ViandaDTO("unQRQueExiste2", LocalDateTime.now(), EstadoViandaEnum.PREPARADA, 2L, 1);
            var trasladoDTO1 = new TrasladoDTO("unQRQueExiste1" , EstadoTrasladoEnum.ENTREGADO,LocalDateTime.now(), 1 , 2);
            var trasladoDTO2 = new TrasladoDTO("unQRQueExiste2" , EstadoTrasladoEnum.ENTREGADO,LocalDateTime.now(), 1 , 2);
            List<TrasladoDTO> viandas = List.of(trasladoDTO1,trasladoDTO2);
        } else{
            context.result("Viandas no encontradas de " + colaboradorId);
            context.status(HttpStatus.NOT_FOUND);
        }
    }
}



