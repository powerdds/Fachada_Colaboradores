package ar.edu.utn.dds.k3003.model;

//import ar.edu.utn.dds.k3003.facades.dtos.FormaDeColaborarEnum;
import ar.edu.utn.dds.k3003.model.FormaDeColaborarEnum;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ConversorFormasDeColaborar implements AttributeConverter<List<FormaDeColaborarEnum>, String> {
    @Override
    public String convertToDatabaseColumn(List<FormaDeColaborarEnum> formasDeColaborar) {
        //Recibe una lista de formas de colaborar y devuelve un string con el indice de cada una separado por coma
        return formasDeColaborar.stream().map(FormaDeColaborarEnum::ordinal).map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public List<FormaDeColaborarEnum> convertToEntityAttribute(String formasDeColaborar) {
        //Llega una lista de indices separados por coma y devuelve una lista de FormaDeColaborarEnum
        return Arrays.stream(formasDeColaborar.split(",")).map(Integer::parseInt).map(forma -> {return FormaDeColaborarEnum.values()[forma]; }).collect(Collectors.toList());
    }
}
