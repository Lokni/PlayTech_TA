public interface EvictableMap <K, V>{
     void put(K key, V value);
     V get(K key);
     int size();
}
