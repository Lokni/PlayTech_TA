import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class EvictionMap<K, V> {
    private final Long expireTimeInMs;
    private final Map<K, ObjectHolder> storage;

    public EvictionMap(Long expireTimeInMs) {
        this.storage = new ConcurrentHashMap<>();
        this.expireTimeInMs = expireTimeInMs;
    }

    public void put(K key, V value) {
        storage.put(key, new ObjectHolder(value, LocalDateTime.now().plus(expireTimeInMs, ChronoUnit.MILLIS)));
    }

    public V get(K key) {
        removeIfExpired(key);
        return Optional.ofNullable(storage.get(key))
                .map(ObjectHolder::getValue)
                .orElse(null);
    }

    private void removeIfExpired(K key) {
        Optional.ofNullable(storage.get(key)).ifPresent(objectHolder -> {
            if (objectHolder.expirationTime.isBefore(LocalDateTime.now())) {
                storage.remove(key);
            }
        });
    }


    private class ObjectHolder {
        private final V value;
        private final LocalDateTime expirationTime;

        protected ObjectHolder(V value, LocalDateTime currentTime) {
            this.value = value;
            this.expirationTime = currentTime;
        }

        public V getValue() {
            return value;
        }
    }

}
