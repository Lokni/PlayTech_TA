import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;

public class NormalEvictionMapUTest {

    private final static long EVICTION_DELAY_MS = 10_000L;
    private EvictionMapNormal<String, Number> testObj;

    @BeforeEach
    public void setup() {
        testObj = new EvictionMapNormal<>(EVICTION_DELAY_MS);
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
        testObj = new EvictionMapNormal<>(Duration.ofMinutes(10).getSeconds() * 1000);
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


