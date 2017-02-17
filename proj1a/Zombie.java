/**
 * Created by pkmnfreak on 2/14/17.
 */
public class Zombie{

    public static void main(String[] args) {
        FastZombie witch = new FastZombie("Witch");
        Zombie z = (FastZombie) witch;
        (z).run();
    }

    protected String name;

    public void run() {
        System.out.println(name + " will get you soon");
    }

    public Zombie(String name) {
        this.name = name;
        System.out.println(name + " wants BRAINS!");
    }

    public void bite() {
        System.out.println("Nom nom nom!");
    }

    public void bite(Zombie z) {
        System.out.println(z.name + ", is that you?");
    }

    public void bite(FastZombie z) {
        System.out.println("Not... Possible...");
    }

}