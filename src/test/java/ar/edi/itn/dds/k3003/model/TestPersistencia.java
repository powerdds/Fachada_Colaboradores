package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.model.Colaborador;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;
import ar.edu.utn.dds.k3003.persist.ColaboradorRepository;
import static ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum.DONADORDINERO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPersistencia {

        static EntityManagerFactory entityManagerFactory ;
        EntityManager entityManager ;

        @BeforeAll
        public static void setUpClass() throws Exception {
            entityManagerFactory = Persistence.createEntityManagerFactory("colaboradoresdb");
        }
        @BeforeEach
        public void setup() throws Exception {
            entityManager = entityManagerFactory.createEntityManager();
        }
        @Test
        public void testConectar() {
// vacío, para ver que levante el ORM
        }

   @Test
    public void testGuardarYRecuperarDoc() throws Exception {
        Colaborador col1 = new Colaborador("pepe", List.of(DONADORDINERO),null ,0L);
        entityManager.getTransaction().begin();
        entityManager.persist(col1);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = entityManagerFactory.createEntityManager();
        Colaborador col2 = entityManager.find(Colaborador.class,1L);

        assertEquals(col1.getNombre(), col2.getNombre()); // también puede redefinir el equals
    }
    @Test
    public void testGuardarYRecuperarColaborador() throws Exception {
// Pre condiciones: se supone que el revisor esta dado de alta ANTES de cargar el lote
        Colaborador colaborador= new Colaborador("Jose" , List.of(DONADORDINERO),null ,0L);
// Notar que volvemos a inicializar la persistencia
        entityManager = entityManagerFactory.createEntityManager();
        ColaboradorRepository colaboradorRepo= new ColaboradorRepository(entityManagerFactory);
        //List<Revisor> revisores = revisorRepo.all();
        entityManager.getTransaction().begin();
        colaboradorRepo.save(colaborador);
        entityManager.getTransaction().commit();
        entityManager.close();
// Otra interaccion
        entityManager = entityManagerFactory.createEntityManager();
        colaboradorRepo = new ColaboradorRepository(entityManagerFactory);
        entityManager.getTransaction().begin();
        Colaborador colaboradorX = colaboradorRepo.findById(colaborador.getId());
// Marco manualmente el valor de copia en un valor alto
// 1 --> no se copiaron | 0 se copiaron y los docs son identicos
        entityManager.getTransaction().commit();
        entityManager.close();
        assertEquals(colaborador.getNombre(), colaboradorX.getNombre());
    }
}


