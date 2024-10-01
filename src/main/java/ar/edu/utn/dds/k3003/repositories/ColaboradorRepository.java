package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Colaborador;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ColaboradorRepository {
    private static AtomicLong seqId = new AtomicLong();
    private Collection<Colaborador> colaboradores;

    public ColaboradorRepository() {
        this.colaboradores = new ArrayList<>();
    }

    public Colaborador save(Colaborador colaborador) {
        if (Objects.isNull(colaborador.getId())) {
            colaborador.setId(seqId.getAndIncrement());
            this.colaboradores.add(colaborador);
        }
        else {
            this.colaboradores.add(colaborador);
        }
        return colaborador;
    }

    public Colaborador findById(Long id) {
        Optional<Colaborador> first = this.colaboradores.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay un colaborador de id: %s", id)
        ));
    }

    public void remover(Long id) {
        Colaborador c = findById(id);
        this.colaboradores.remove(c);
    }
}