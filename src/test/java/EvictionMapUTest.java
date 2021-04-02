import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvictionMapUTest {
    private EvictionMap<String, Number> testObj;
    private final static long EVICTION_DELAY_MS = 10_000L;

    @BeforeEach
    public void setup(){
        testObj = new EvictionMap<>(EVICTION_DELAY_MS);
    }

    @Test
    public void shouldAddValue(){
        testObj.put("key", 1);

        Number actual = testObj.get("key");

        Assertions.assertEquals(1, actual);
    }



}
