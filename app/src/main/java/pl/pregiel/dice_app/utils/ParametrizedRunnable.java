package pl.pregiel.dice_app.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ParametrizedRunnable implements Runnable {
    private List<Object> parameters;

    public ParametrizedRunnable() {
        parameters = new ArrayList<>();
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameters(Object... objects) {
        Collections.addAll(parameters, objects);
    }
}
