package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.dtos.HeladeraDTO;
import ar.edu.utn.dds.k3003.model.DTOs.ColaboradorDTO;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HeladeraRetrofitClient {
   /* @POST("/heladeras/{id}/suscribir")//query
    Call<?> suscribir(@Path("id") Long heladeraId , @Body ColaboradorDTO colaborador);*/
    @PATCH("/heladeras/{id}/reparar")
    Call<?> reparar(@Path("id") Long heladeraId);
}

