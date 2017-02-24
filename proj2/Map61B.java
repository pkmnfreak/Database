/**
 * Created by pkmnfreak on 2/21/17.
 */

import java.util.List;

    public interface Map61B<K, V> {
        /* Returns true if this map contains a mapping for the specified key. */
        boolean containsKey(K key);

        V get(K key);

        int size();

        void put(K key, V value);

        List<K> keys();
    }
