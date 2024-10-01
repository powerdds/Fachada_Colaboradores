package ar.edu.utn.dds.k3003.app;
import ar.edu.utn.dds.k3003.clients.LogisticaProxy;
import ar.edu.utn.dds.k3003.clients.ViandasProxy;
import ar.edu.utn.dds.k3003.controller.ColaboradorController;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.micrometer.MicrometerPlugin;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
   public static EntityManagerFactory entityManagerFactory;
    public static void main(String[] args) {

       startEntityManagerFactory();

        var env = System.getenv();
        var objectMapper = createObjectMapper();
        var fachada = new Fachada(entityManagerFactory);

        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        var app = Javalin.create().start(port);

        var colaboradorController = new ColaboradorController(fachada,entityManagerFactory);

        app.post("/colaboradores", colaboradorController::agregar);
        app.get("/colaboradores/{id}", colaboradorController::obtener);
        app.patch("/colaboradores/{id}",colaboradorController::modificar);
        app.get("/colaboradores/{id}/puntosAnioMes",colaboradorController::puntosAnioMes);
        app.get("/colaboradores/{id}/puntos",colaboradorController::puntos);
        app.get("/colaboradores/{id}/viandasDistribuidas",colaboradorController::viandasDistribuidas);
        app.get("/colaboradores/{id}/viandasDonadas",colaboradorController::viandasDonadas);
        app.get("/colaboradores/{id}/puntosViandasDistribuidasAnioMes",colaboradorController::puntosViandasDistribuidas);
        app.get("/colaboradores/{id}/puntosViandasDonadasAnioMes",colaboradorController::puntosViandasDonadas);
        app.put("/formula", colaboradorController::actualizarPuntos);
        app.post("/colaboradores/prueba", colaboradorController::prueba);
        app.delete("/cleanup",colaboradorController::clean);
    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
        return objectMapper;
    }

   public static void startEntityManagerFactory() {
// https://stackoverflow.com/questions/8836834/read-environment-variables-in-persistence-xml-file
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] {
                "javax.persistence.jdbc.url",
                "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size", "hibernate.show_sql" };
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("colaboradoresdb", configOverrides);
    }
}


