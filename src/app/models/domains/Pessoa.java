package app.models.domains;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author leodouglas
 */
@Data
public class Pessoa implements Serializable{

    public Pessoa(String name, int idade) {
        this.name = name;
        this.idade = idade;
    }

    private String name;
    private int idade;
    
}
