import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class EvictionMapNormal<K, V> implements EvictableMap<K, V> {

    private final long evictionDelayMs;
    private final Map<K, ObjectHolder> storage;
    private final ReentrantLock lock;


    public EvictionMapNormal(long evictionDelayMs) {
        this.evictionDelayMs = evictionDelayMs;
        this.storage = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public void put(K key, V value) {

        storage.put(key, new ObjectHolder(key, value, evictionDelayMs, expiredKey -> {
            try {
                lock.lock();
                storage.remove(expiredKey);
            } finally {
                lock.unlock();
            }
        }));
    }

    @Override
    public V get(K key) {
        return Optional.ofNullable(storage.get(key))
                .map(ObjectHolder::getValue)
                .orElse(null);
    }

    private interface ExpirationHandler<K> {
        void onExpired(K key);
    }

    private class ObjectHolder {

        private final V value;

        ObjectHolder(K key, V value, long expirationDelay, ExpirationHandler<K> expirationHandler) {

            this.value = value;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    expirationHandler.onExpired(key);
                }
            }, expirationDelay);

        }

        V getValue() {
            return value;
        }
    }
}
