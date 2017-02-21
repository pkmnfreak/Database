package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        arb.enqueue("hi");
        assertEquals("hi", arb.peek());
        arb.enqueue("my");
        assertEquals("hi", arb.peek());
        assertFalse(arb.isEmpty());
        assertFalse(arb.isFull());
        assertEquals("hi", arb.dequeue());
        assertEquals("my", arb.peek());
        arb.enqueue("name");
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
