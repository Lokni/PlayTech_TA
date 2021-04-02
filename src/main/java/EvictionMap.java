import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class EvictionMap<K, V> {
    private final Long expireTimeInMs;
    private final Map<K, EvictableValueHolder<K,V>> storage;
    private final ReentrantLock lock;

    public EvictionMap(Long expireTimeInMs) {
        this.expireTimeInMs = expireTimeInMs;
        this.storage = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    private static final class EvictableValueHolder<K,V>{

    }
}
