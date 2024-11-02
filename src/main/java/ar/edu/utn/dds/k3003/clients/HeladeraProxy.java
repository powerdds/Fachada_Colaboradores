package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;


public class HeladeraProxy implements FachadaHeladeras {

    private final String endpoint;
    private final HeladeraRetrofitClient service;
    public HeladeraProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_HELADERA", "http://localhost:8081/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(HeladeraRetrofitClient.class);
    }

    public HeladeraDTO agregar(HeladeraDTO var1){return null;}

    public void depositar(Integer var1, String var2) throws NoSuchElementException{}

    public Integer cantidadViandas(Integer var1) throws NoSuchElementException{return null;}

    public void retirar(RetiroDTO var1) throws NoSuchElementException{}

    public void temperatura(TemperaturaDTO var1){}

   /* @SneakyThrows
    public PrintStream suscribir(Long heladeraId, ColaboradorDTO colaborador)throws NoSuchElementException{
       Response<?> execute = service.suscribir(heladeraId , colaborador).execute();

        if (execute.isSuccessful()) {
            return System.out.printf("Se ha suscrito exitosamente a la heladera");
        }
        if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("No se pudo suscribir a la heladera" + heladeraId);
        }
        throw new RuntimeException("Error conectandose con el componente Heladera");
    }*/

    @SneakyThrows
    public void reparar(Long heladeraId)throws NoSuchElementException{ //revisar
        Response<Void> execute = service.reparar(heladeraId).execute();
        if (execute.isSuccessful()) {
            System.out.printf("Se reparó la heladera " + heladeraId);
        }
        else if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
            throw new NoSuchElementException("No se pudo reparar la heladera " + heladeraId);
        }
        else {throw new RuntimeException("Error conectandose con el componente Heladera");}

    }
    public List<TemperaturaDTO> obtenerTemperaturas(Integer var1){return null;}

    public void setViandasProxy(FachadaViandas fachadaViandas) {}
}
