package com.ctrip.zeus.logstats.parser.state.extended;

import com.ctrip.zeus.logstats.parser.state.LogStatsState;
import com.ctrip.zeus.logstats.parser.state.LogStatsStateMachine;
import com.ctrip.zeus.logstats.parser.state.StateMachineContext;
import com.ctrip.zeus.logstats.parser.state.Transition;

import java.util.LinkedList;

/**
 * Created by zhoumy on 2016/6/7.
 */
public class XForwardForStateMachine implements LogStatsStateMachine {

    private final LogStatsState xforwardState;
    private final Transition transition;
    private final String[] emptyMarker;
    private final String separator;

    public XForwardForStateMachine(LogStatsState xforwardState, String separator) {
        this(xforwardState, separator == null ? " " : separator, new String[]{"", "-"});
    }

    public XForwardForStateMachine(LogStatsState xforwardState, String separator, String[] emptyMarker) {
        this.xforwardState = xforwardState;
        this.transition = new XForwardTransition();
        this.separator = separator;
        this.emptyMarker = emptyMarker == null ? new String[0] : emptyMarker;
    }

    @Override
    public LogStatsState getStartState() {
        return xforwardState;
    }

    @Override
    public void transduce(StateMachineContext ctxt) {
        StateMachineContext subCtxt = new VariableContext(ctxt);
        subCtxt.setState(StateMachineContext.ContextState.PROCESSING);
        try {
            LogStatsState current = xforwardState;
            current.getAction().execute(subCtxt, separator);
            while ((current = transition.transit(xforwardState, subCtxt)) != null) {
                current.getAction().execute(subCtxt, separator);
            }
            StringBuilder sb = new StringBuilder();
            LinkedList<String> result = subCtxt.getResult();
            while (!result.isEmpty()) {
                if (sb.length() == 0) {
                    sb.append(result.poll());
                } else {
                    sb.append(", ").append(result.poll());
                }
            }
            ctxt.addResult(xforwardState.getName(), sb.toString());
            subCtxt.setState(StateMachineContext.ContextState.SUCCESS);
        } catch (Exception ex) {
            ctxt.setState(StateMachineContext.ContextState.FAILURE);
        }
    }

    private class XForwardTransition implements Transition {

        @Override
        public LogStatsState transit(LogStatsState state, StateMachineContext ctxt) {
            if (xforwardState.getName().equals(state.getName())) {
                String v = ctxt.peekLastParsedValue();
                for (String m : emptyMarker) {
                    if (m.equals(v)) {
                        return null;
                    }
                }
                char[] paralSplitter = ctxt.delay(2);
                if (paralSplitter[0] == ',' && paralSplitter[1] == ' ') {
                    ctxt.proceed(2);
                    return xforwardState;
                } else {
                    return null;
                }
            }
            return null;
        }
    }
}
