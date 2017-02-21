package synthesizer;

/**
 * Created by pkmnfreak on 2/20/17.
 */
import java.util.Iterator;

public interface BoundedQueue<T> extends Iterable<T> {
    Iterator<T> iterator();
    int capacity();
    int fillCount();
    void enqueue(T x);
    T dequeue();
    T peek();
    default boolean isEmpty() {
        return fillCount() == 0;
    }
    default boolean isFull() {
        return fillCount() == capacity();
    }
}
