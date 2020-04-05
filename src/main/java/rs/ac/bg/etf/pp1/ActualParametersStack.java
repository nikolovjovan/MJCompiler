package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.symboltable.concepts.MJSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ActualParametersStack {

    private class ActualMethodParameters {
        ArrayList<MJSymbol> parameters;

        public ActualMethodParameters() {
            parameters = new ArrayList<>();
        }
    }

    private Stack<ActualMethodParameters> parametersStack;
    private ActualMethodParameters currentParameters;

    public ActualParametersStack() {
        parametersStack = new Stack<>();
    }

    public boolean insertActualParameter(MJSymbol parameter) {
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

    public List<MJSymbol> getParameters() {
        if (currentParameters == null) {
            return null;
        }

        List<MJSymbol> parameters = currentParameters.parameters;

        if (parametersStack.isEmpty()) {
            currentParameters = null;
        } else {
            currentParameters = parametersStack.pop();
        }

        return parameters;
    }
}