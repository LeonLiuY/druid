package liuyang.druid.signal;

import java.util.Map;

import liuyang.druid.DruidParser.SignalContext;

public interface Signals<T extends Signal> {

    void register(SignalContext ctx, T signal);

    void registerAll(Map<SignalContext, T> signals);

    void scan();

    int size();
}
