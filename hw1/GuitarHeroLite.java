/** A client that uses the synthesizer package to replicate a plucked guitar string sound */
public class GuitarHeroLite {
    private static final double CONCERT_A = 440.0;
    private static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);
    private static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        synthesizer.GuitarString stringA = new synthesizer.GuitarString(CONCERT_A);
        synthesizer.GuitarString stringC = new synthesizer.GuitarString(CONCERT_C);
        synthesizer.GuitarString[] piano = new synthesizer.GuitarString[37];
        for (int i = 0; i < piano.length; i += 1) {
            piano[i] = new synthesizer.GuitarString(440 * Math.pow(2, ((i - 24) / 12)));
        }
        while (true) {
            /*
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (keyboard.contains(Character.toString(key))) {
                    piano[keyboard.indexOf(key)].pluck();
                }
            */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'a') {
                    stringA.pluck();
                } else if (key == 'c') {
                    stringC.pluck();
                }
            }

        /* compute the superposition of samples */
                double sample = stringA.sample() + stringC.sample();

        /* play the sample on standard audio */
                StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
                stringA.tic();
                stringC.tic();
        }
    }
}

