import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class EvictionMap<K, V> {
    private static final Long DELAY = 10_000L;
    private final Map<Object, MyObjectHolder> storage = new ConcurrentHashMap<>();


    public int size() {
        return storage.size();
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public void put(K key, V value) {
        checkKeyNotNull(key);
        MyObjectHolder data = new MyObjectHolder(value);
        storage.put(key, data);
    }

    public V get(K key) {
        MyObjectHolder value = storage.get(key);
        if (System.currentTimeMillis() - value.expirationTime <= DELAY) {
            storage.remove(key);
            return null;
        } else {
            return value.value;
        }
    }


    private void checkKeyNotNull(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
    }

    private class MyObjectHolder {
        private final V value;
        private final Long expirationTime;

        protected MyObjectHolder(V value) {
            this.value = value;
            expirationTime = System.currentTimeMillis();
        }
    }

}
