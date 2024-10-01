package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.TrasladoDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface LogisticaRetrofitClient {
    @GET("traslados/search/findByColaboradorId")//query
    Call<List<TrasladoDTO>> get(@Query("colaboradorId") Long colaboradorId);
}

