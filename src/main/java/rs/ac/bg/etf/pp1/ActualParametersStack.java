package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ActualParametersStack {

    private class ActualMethodParameters {
        ArrayList<Obj> parameters;

        public ActualMethodParameters() {
            parameters = new ArrayList<>();
        }
    }

    private Stack<ActualMethodParameters> parametersStack;
    private ActualMethodParameters currentParameters;

    public ActualParametersStack() {
        parametersStack = new Stack<>();
    }

    public boolean insertActualParameter(Obj parameter) {
        if (currentParameters == null) {
            return false;
        }
        currentParameters.parameters.add(parameter);
        return true;
    }

    public void createParameters() {
        if (currentParameters != null) {
            parametersStack.add(currentParameters);
        }
        currentParameters = new ActualMethodParameters();
    }

    public List<Obj> getParameters() {
        if (currentParameters == null) {
            return null;
        }

        List<Obj> parameters = currentParameters.parameters;

        if (parametersStack.isEmpty()) {
            currentParameters = null;
        } else {
            currentParameters = parametersStack.pop();
        }

        return parameters;
    }
}