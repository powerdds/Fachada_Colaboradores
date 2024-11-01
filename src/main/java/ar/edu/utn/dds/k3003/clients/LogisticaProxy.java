package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaLogistica;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import ar.edu.utn.dds.k3003.facades.exceptions.TrasladoNoAsignableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.HttpStatus;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class LogisticaProxy implements FachadaLogistica {

    private final String endpoint;
    private final LogisticaRetrofitClient service;
    public LogisticaProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_LOGISTICA", "http://localhost:8081/");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(this.endpoint)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(LogisticaRetrofitClient.class);
    }


public RutaDTO agregar(RutaDTO var1){return null;}

public TrasladoDTO buscarXId(Long var1) throws NoSuchElementException
    {return null;}

public TrasladoDTO asignarTraslado(TrasladoDTO var1) throws TrasladoNoAsignableException{return null;}

    @Override
    @SneakyThrows
public List<TrasladoDTO> trasladosDeColaborador(Long var1, Integer var2, Integer var3){
    Response<List<TrasladoDTO>> execute = service.get(var1).execute();

    if (execute.isSuccessful()) {
        return execute.body();
    }
    if (execute.code() == HttpStatus.NOT_FOUND.getCode()) {
        //List<TrasladoDTO> list = new ArrayList<>();
        return new ArrayList<>();//throw new NoSuchElementException("No se encontraron traslados del colaborador " + var1);
    }
    throw new RuntimeException("Error conectandose con el componente logística");
}

public void setHeladerasProxy(FachadaHeladeras var1){}

public void setViandasProxy(FachadaViandas var1){}

public void trasladoRetirado(Long var1){}

public void trasladoDepositado(Long var1){}
}
