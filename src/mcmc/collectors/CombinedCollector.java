package mcmc.collectors;

import java.util.List;

public class CombinedCollector implements Collector {
    List<Collector> collectors;

    public CombinedCollector(List<Collector> collectors) {
        this.collectors = collectors;
    }

    public void add(Collector collector) {
    	this.collectors.add(collector);
    }
    
    @Override
    public void collect() {
        for (Collector collector : collectors)
            collector.collect();
    }

    @Override
    public void close() {
        for (Collector collector : collectors)
            collector.close();
    }

}