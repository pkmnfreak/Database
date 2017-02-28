import java.util.List;
import java.util.ArrayList;

/**
 * Created by pkmnfreak on 2/21/17.
 */
public class Table<K, V> implements Map61B<K, V>{
        private K[] keys;
        private V[] values;
        int size;

        public static void main(String[] args) {
            Table<String, int[]> t = new Table<>();
            t.put("x int", new int[]{2, 8, 10});
            t.put("y int", new int[]{2, 8, 5});

        }

        public Table() {
            keys = (K[]) new Object[2];
            values = (V[]) new Object[2];
            size = 0;
        }

        /** Returns the index of the given key if it exists,
         *  -1 otherwise. */
        private int keyIndex(K key) {
            for (int i = 0; i < size; i += 1) {
                if (keys[i].equals(key)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean containsKey(K key) {
            int index = keyIndex(key);
            return index > -1;
        }

        public void put(K key, V value) {
            int index = keyIndex(key);
            if (index == -1) {
                keys[size] = key;
                values[size] = value;
                size += 1;
                return;
            }
            values[index] = value;
        }

        public V get(K key) {
            int index = keyIndex(key);
            return values[index];
        }

        public int size() {
            return size;
        }

        public List<K> keys() {
            List<K> keylist = new ArrayList<K>();
            for (int i = 0; i < keys.length; i += 1) {
                keylist.add(keys[i]);
            }
            return keylist;
        }


    }
