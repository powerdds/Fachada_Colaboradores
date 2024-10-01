package ar.edu.utn.dds.k3003.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PuntosBody{

    private Double pesosDonados;
    private Double viandasDistribuidas;
    private Double viandasDonadas;
    private Double tarjetasRepartidas;
    private Double heladerasActivas;

    protected PuntosBody() {
        super();
    }

    public PuntosBody(Double pesosDonados , Double viandasDistribuidas, Double viandasDonadas,
                      Double tarjetasRepartidas, Double heladerasActivas) {
        this.pesosDonados = pesosDonados;
        this.viandasDistribuidas = viandasDistribuidas;
        this.viandasDonadas =viandasDonadas;
        this.tarjetasRepartidas = tarjetasRepartidas;
        this.heladerasActivas = heladerasActivas;
    }
}

