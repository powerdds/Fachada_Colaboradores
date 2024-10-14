package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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

/*    @Override
    @SneakyThrows
public List<TrasladoDTO> trasladosDeColaborador(Long var1, Integer var2, Integer var3){
    Response<List<TrasladoDTO>> execute = service.get(var1).execute();

    if (execute.isSuccessful()) {
        return execute.body();
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
        throw new NoSuchElementException("No se encontraron traslados del colaborador " + var1);
    }
    throw new RuntimeException("Error conectandose con el componente logística");
}*/

    public HeladeraDTO agregar(HeladeraDTO var1){return null;}

    public void depositar(Integer var1, String var2) throws NoSuchElementException{}

    public Integer cantidadViandas(Integer var1) throws NoSuchElementException{return null;}

    public void retirar(RetiroDTO var1) throws NoSuchElementException{}

    public void temperatura(TemperaturaDTO var1){}

    public List<TemperaturaDTO> obtenerTemperaturas(Integer var1){return null;}

    public void setViandasProxy(FachadaViandas fachadaViandas) {}
}
