/**
 * According to the test task, I have to write a data storage structure
 * with limited storage time.
 * Structure must be based on generic Map<K, V>.
 *
 * @Author Dmitri Kalvan
 */

public class Main {
    public static void main(String[] args) {
        EvictionMap<String, Integer> test = new EvictionMap<>(10_000L);



        new Thread(() -> {
            try {
                test.put("A", 1);
                test.put("B", 2);
                test.put("C", 3);
                System.out.println(test.get("A"));
                System.out.println(test.get("C"));
                System.out.println(test.get("B"));
                Thread.sleep(5000);
                System.out.println(test.get("C"));
                Thread.sleep(5000);
                System.out.println(test.get("A"));



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
