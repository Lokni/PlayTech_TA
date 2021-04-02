import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class LazyEvictionMapUTest {
    private final static long EVICTION_DELAY_MS = 10_000L;
    private EvictionMapLazy<String, Number> testObj;

    @BeforeEach
    public void setup() {
        testObj = new EvictionMapLazy<>(EVICTION_DELAY_MS);
    }

    @Test
    public void shouldAddValue() {
        testObj.put("key", 1);

        Number actual = testObj.get("key");

        Assertions.assertEquals(1, actual);
    }

    @Test
    public void shouldEvictValue() throws InterruptedException {
        testObj.put("key", 1);

        Thread.sleep(EVICTION_DELAY_MS + 1);

        Number actual = testObj.get("key");

        Assertions.assertNull(actual);
    }

    @Test
    public void shouldInsert_1000_values() {
        // Avoid eviction at least 10 minutes.
        testObj = new EvictionMapLazy<>(Duration.ofMinutes(10).getSeconds() * 1000);
        int valuesCount = 1000;

        for (int i = 0; i < valuesCount; i++) {
            testObj.put("key_" + i, i);
        }

        int mapSizeAfterInsert = testObj.size();

        Assertions.assertEquals(valuesCount, mapSizeAfterInsert);
    }
}
