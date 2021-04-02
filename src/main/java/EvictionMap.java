import java.util.Map;
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
