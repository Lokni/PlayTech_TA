import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;

public class EvictionMapUTest {
    private final static long EVICTION_DELAY_MS = 10_000L;
    private EvictionMap<String, Number> testObj;

    @BeforeEach
    public void setup() {
        testObj = new EvictionMap<>(EVICTION_DELAY_MS);
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
    public void shouldShiftEvictionTimeWithSameValues() throws InterruptedException {
        testObj.put("key", 1);

        Thread.sleep(EVICTION_DELAY_MS - 1000);

        Number actual1 = testObj.get("key");

        testObj.put("key", 1);

        Thread.sleep(EVICTION_DELAY_MS - 1000);

        Number actual2 = testObj.get("key");

        Assertions.assertEquals(1, actual1);
        Assertions.assertEquals(1, actual2);
    }

    @Test
    public void shouldShiftEvictionTimeWithDifferentValues() throws InterruptedException {
        testObj.put("key", 1);

        Thread.sleep(EVICTION_DELAY_MS - 1000);

        Number actual1 = testObj.get("key");

        testObj.put("key", 2);

        Thread.sleep(EVICTION_DELAY_MS - 1000);

        Number actual2 = testObj.get("key");

        Assertions.assertEquals(1, actual1);
        Assertions.assertEquals(2, actual2);
    }

    @Test
    public void shouldInsert_1000_values() {
        // Avoid eviction at least 10 minutes.
        testObj = new EvictionMap<>(Duration.ofMinutes(10).getSeconds() * 1000);
        int valuesCount = 1000;

        for (int i = 0; i < valuesCount; i++) {
            testObj.put("key_" + i, i);
        }

        int mapSizeAfterInsert = testObj.size();

        Assertions.assertEquals(valuesCount, mapSizeAfterInsert);
    }

    @Test
    public void shouldInsertAndEvict_1000_values() throws InterruptedException {
        int valueCount = 1000;
        int randomNumber = new Random().nextInt(valueCount);
        String randomKey = "key_" + randomNumber;
        Number actualRandomValue = null;

        for (int i = 0; i < valueCount; i++) {
            testObj.put("key_" + i, i);
            if (i == randomNumber) {
                // Take one random value immediately to avoid lost after eviction because a long insertion process.
                actualRandomValue = testObj.get(randomKey);
            }
        }
        // Make sure that last insertion is evicted.
        Thread.sleep(EVICTION_DELAY_MS + 1);

        int mapSizeAfterEviction = testObj.size();

        Assertions.assertEquals(randomNumber, actualRandomValue);
        Assertions.assertEquals(0, mapSizeAfterEviction);
    }


}
