package liuyang.druid.signal;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import liuyang.druid.DruidParser.SignalContext;

public class FileSignals implements Signals<FileSignal> {

    private final WatchService watcher;
    private final SignalReceiver signalReceiver;
    private final Map<WatchKey, Path> keys = new HashMap<>();

    private final Map<Path, Set<Path>> pathFiles = new HashMap<>();
    private Map<SignalContext, FileSignal> signals = new HashMap<>();

    public FileSignals(SignalReceiver signalReceiver) {
        this.signalReceiver = signalReceiver;
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void registerAll(Map<SignalContext, FileSignal> signals) {
        signals.forEach((ctx, signal) -> register(ctx, signal));
    }

    @Override
    public void register(SignalContext ctx, FileSignal signal) {
        Path filePath = signal.getFilePath();
        signals.put(ctx, signal);

        Path dir = filePath.getParent();
        if (pathFiles.containsKey(dir)) {
            pathFiles.get(dir).add(filePath);
        } else {
            try {
                WatchKey key = dir.register(watcher, ENTRY_MODIFY);
                keys.put(key, dir);
                Set<Path> files = new HashSet<>();
                files.add(filePath);
                pathFiles.put(dir, files);

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    @Override
    public void scan() {
        WatchKey key;
        try {
            key = watcher.take();
        } catch (InterruptedException x) {
            return;
        }

        Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            Kind<?> kind = event.kind();

            // TBD - provide example of how OVERFLOW event is handled
            if (kind == OVERFLOW) {
                continue;
            }

            final Path changed = ((Path) event.context()).toAbsolutePath();
            if (pathFiles.get(dir).contains(changed)) {
                signalReceiver.receive(new FileSignal(changed));
            }
        }

        // reset key and remove from set if directory no longer accessible
        boolean valid = key.reset();
        if (!valid) {
            keys.remove(key);

            // all directories are inaccessible
            if (keys.isEmpty()) {
                return;
            }
        }
    }

    @Override
    public int size() {
        return signals.values().size();
    }

}
