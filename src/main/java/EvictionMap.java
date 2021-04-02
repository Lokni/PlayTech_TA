import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


public class EvictionMap<K, V> {
    private final Long expireTimeInMs;
    private final Map<K, EvictableValueHolder<K, V>> storage;
    private final ReentrantLock lock;

    public EvictionMap(Long expireTimeInMs) {
        this.expireTimeInMs = expireTimeInMs;
        this.storage = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    public void put(K key, V value) {
        storage.put(key, new EvictableValueHolder<>(key,
                value,
                expireTimeInMs,
                (expiredKey, evictableValueHolder) -> {
                    if (storage.containsValue(evictableValueHolder)) {
                        try {
                            lock.lock();
                            storage.remove(expiredKey);
                        } finally {
                            lock.unlock();
                        }
                    }
                }));
    }

    public V get(K key) {
        return Optional.ofNullable(storage.get(key))
                .map(EvictableValueHolder::getValue)
                .orElse(null);
    }

    public int size() {
        return storage.size();
    }

    private interface ExpirationHandler<K, V> {
        void onExpired(K key, EvictableValueHolder<K, V> evictableValueHolder);
    }

    private static final class EvictableValueHolder<K, V> {
        private final V value;

        protected EvictableValueHolder(K key,
                                       V value,
                                       long expirationDelay,
                                       EvictionMap.ExpirationHandler<K, V> expirationHandler) {
            this.value = value;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    expirationHandler.onExpired(key, EvictableValueHolder.this);
                }
            }, expirationDelay);

        }

        public V getValue() {
            return value;
        }
    }
}
