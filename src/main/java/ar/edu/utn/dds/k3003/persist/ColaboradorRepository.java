package ar.edu.utn.dds.k3003.persist;

import ar.edu.utn.dds.k3003.facades.dtos.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.Colaborador;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ColaboradorRepository {
    private EntityManagerFactory entityManagerFactory;

   private static AtomicLong seqId = new AtomicLong();

    public ColaboradorRepository(EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
    }
    public ColaboradorRepository() {
        super();
    }

    public Colaborador save(Colaborador colaborador) {
        EntityManager em = entityManagerFactory.createEntityManager();
        /*em.getTransaction().begin();
        em.persist(colaborador);
        em.getTransaction().commit();
        em.close();*/

        if (Objects.isNull(colaborador.getId())) {
            colaborador.setId(seqId.getAndIncrement());
            em.getTransaction().begin();
            em.persist(colaborador);
            em.getTransaction().commit();
            em.close();
        }
        else {
            em.getTransaction().begin();
            em.persist(colaborador);
            em.getTransaction().commit();
            em.close();
        }
         return this.findById(colaborador.getId());
    }

    public Colaborador findById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Colaborador colab1 = em.find(Colaborador.class, id);
        em.getTransaction().commit();
        em.close();
        return colab1;
    }

    public void remove(Long id){
        EntityManager em = entityManagerFactory.createEntityManager();
        Colaborador col1 = this.findById(id);
        em.getTransaction().begin();
        em.remove(em.contains(col1) ? col1 : em.merge(col1));
        em.getTransaction().commit();
        em.close();
    }
    public List<Colaborador> list(){
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Colaborador> colaboradores = em.createQuery("from Colaborador", Colaborador.class).getResultList();

        return colaboradores;
    }
}

