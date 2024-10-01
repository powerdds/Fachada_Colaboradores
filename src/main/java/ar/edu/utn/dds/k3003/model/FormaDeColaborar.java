package ar.edu.utn.dds.k3003.model;
import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import java.util.List;

public class FormaDeColaborar {
    private List<FormaDeColaborarEnum> formas;
    public List<FormaDeColaborarEnum> getFormas(){return formas;}

    public void setFormas(List<FormaDeColaborarEnum> formas) {
        this.formas = formas;
    }
}
