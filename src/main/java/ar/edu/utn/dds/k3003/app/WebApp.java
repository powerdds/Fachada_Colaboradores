package ar.edu.utn.dds.k3003.app;
import ar.edu.utn.dds.k3003.clients.HeladeraProxy;
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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApp {
    public static EntityManagerFactory entityManagerFactory;
    private static final String TOKEN = "ColabToken";
    public static void main(String[] args) {

        startEntityManagerFactory();

        var env = System.getenv();
        var objectMapper = createObjectMapper();
        var fachada = new Fachada(entityManagerFactory);
        ///////////metrics////////////
        log.info("starting up the server");

        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        registry.config().commonTags("app", "metrics-colaborador");
        // agregamos a nuestro reigstro de métricas todo lo relacionado a infra/tech
        // de la instancia y JVM
        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);
        // agregamos métricas custom de nuestro dominio
        Gauge.builder("metrica_prueba", () -> (int)(Math.random() * 1000))
                .description("Random number from My-Application.")
                .strongReference(true)
                .register(registry);

        Counter colaboradoresCounter = Counter.builder("colaboradores_agregados")
                .description("Cantidad de colaboradores agregados")
                .register(registry);

        Counter cambiosEstadoCounter = Counter.builder("cambios_estado_colaborador")
                .description("Cantidad de cambios de los colaboradores")
                .register(registry);

        Counter puntosColaboradores = Counter.builder("puntos_totales")
                .description("Puntos totales calculados de los colaboradores")
                .register(registry);

        Gauge puntosPromedio = Gauge.builder("puntos_promedio", ()-> puntosColaboradores.count() / colaboradoresCounter.count())
                .description("Puntos promedio calculados de los colaboradores")
                .strongReference(true)
                .register(registry);
        // seteamos el registro dentro de la config de Micrometer

        final var micrometerPlugin =
                new MicrometerPlugin(config -> config.registry = registry);
        ////////////////////////////
        fachada.setViandasProxy(new ViandasProxy(objectMapper));
        fachada.setLogisticaProxy(new LogisticaProxy(objectMapper));
        fachada.setHeladerasProxy(new HeladeraProxy(objectMapper));

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        //var app = Javalin.create().start(port);
        var colaboradorController = new ColaboradorController(fachada,entityManagerFactory , colaboradoresCounter , cambiosEstadoCounter, puntosColaboradores);

        Javalin app = Javalin.create(config -> { config.registerPlugin(micrometerPlugin); }).start(port);
        //,cambiosEstadoCounter,colaboradoresCounter);

            app.get("/", context -> {context.result("Bienvenido al módulo de Colaboradores!"); } );
            app.post("/colaboradores", colaboradorController::agregar);
            app.post("/colaboradores/reportarIncidente", colaboradorController::reportar);
            app.get("/colaboradores", colaboradorController::obtenerColaboradores);
            app.get("/colaboradores/donaciones", colaboradorController::obtenerDonaciones);
            app.post("/colaboradores/prueba", colaboradorController::prueba);
            app.get("/colaboradores/{id}", colaboradorController::obtener);
            app.patch("/colaboradores/{id}",colaboradorController::cambiarForma);
            app.post("/colaboradores/{id}/donar",colaboradorController::donarPesos);
            app.get("/colaboradores/{id}/puntosAnioMes",colaboradorController::puntosAnioMes);
            app.get("/colaboradores/{id}/puntosViandasDistribuidasAnioMes",colaboradorController::puntosViandasDistribuidasAnioMes);
            app.get("/colaboradores/{id}/puntosViandasDonadasAnioMes",colaboradorController::puntosViandasDonadasAnioMes);
            app.put("/formula", colaboradorController::actualizarPuntos);
            app.patch("/colaboradores/{id}/repararHeladera/{heladeraId}",colaboradorController::repararHeladera); //ok
            //app.post("/colaboradores/{id}/suscribirMinHeladera",colaboradorController::suscribirseMin);//ok
            app.delete("/cleanup",colaboradorController::clean);
            app.get("/metrics",
                ctx -> {
                    // chequear el header de authorization y chequear el token bearer
                    // configurado
                    var auth = ctx.header("Authorization");
                    if (auth != null && auth.intern() == "Bearer " + TOKEN) {
                        ctx.contentType("text/plain; version=0.0.4").result(registry.scrape());
                    } else {
                        // si el token no es el apropiado, devolver error,
                        // desautorizado
                        // este paso es necesario para que Grafana online
                        // permita el acceso
                        ctx.status(401).json("unauthorized access");
                    }
                });
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


