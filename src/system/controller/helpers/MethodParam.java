package system.controller.helpers;

import lombok.Data;

/**
 *
 * @author ldpadilha
 */
@Data
public class MethodParam {
    private String name;
    private Class type;
    private String value;

    public MethodParam(String name, Class type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

}
