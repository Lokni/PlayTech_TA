import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EvictionMapLazy<K, V> implements EvictableMap<K, V> {
    private final long evictionDelayMs;
    private final Map<K, ObjectHolder> storage;


    public EvictionMapLazy(long evictionDelayMs) {
        this.evictionDelayMs = evictionDelayMs;
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public void put(K key, V value) {
        storage.put(key, new ObjectHolder(value, LocalDateTime.now().plus(evictionDelayMs, ChronoUnit.MILLIS)));
    }

    @Override
    public V get(K key) {
        removeIfExpired(key);

        return Optional.ofNullable(storage.get(key))
                .map(ObjectHolder::getValue)
                .orElse(null);
    }

    private void removeIfExpired(K key) {

        Optional.ofNullable(storage.get(key))
                .filter(objectHolder -> objectHolder.getExpirationTime().isBefore(LocalDateTime.now()))
                .ifPresent(objectHolder -> storage.remove(key));
    }

    private class ObjectHolder {

        private final V value;
        private final LocalDateTime expirationTime;

        ObjectHolder(V value, LocalDateTime expirationTime) {

            this.value = value;
            this.expirationTime = expirationTime;
        }

        V getValue() {
            return value;
        }

        LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }

}
