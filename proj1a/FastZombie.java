/**
 * Created by pkmnfreak on 2/14/17.
 */
public class FastZombie extends Zombie{

    public FastZombie(String name) {
        super(name);
    }

    public void run() {
        System.out.println(name + " will get you sooner");
    }

    public void bite (FastZombie f) {
        super.bite((Zombie) f);
        System.out.println("#FasterThan" +f.name);
    }
}
