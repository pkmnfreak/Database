public class NBody {
  public static void main(String[] args){
    double T = Double.parseDouble(args[0]);
    double dt = Double.parseDouble(args[1]);
    String filename = args[2];
    double radius = readRadius(filename);
    Planet[] Planets = readPlanets(filename);

    double time = 0;
    StdAudio.play("audio/2001.mid");
    while(time <= T){
      double[] xForces = new double[Planets.length];
      double[] yForces = new double[Planets.length];
      for (int i = 0; i < Planets.length; i++){
        xForces[i] = Planets[i].calcNetForceExertedByX(Planets);
        yForces[i] = Planets[i].calcNetForceExertedByY(Planets);
      }
      for (int i = 0; i < Planets.length; i++){
        Planets[i].update(time, xForces[i], yForces[i]);
      }

      StdDraw.setXscale(-radius,radius);
      StdDraw.setYscale(-radius,radius);
      StdDraw.picture(0, 0, "images/starfield.jpg");

      for (Planet p : Planets){
        p.draw();
      }

      StdDraw.show(10);
      time += dt;
    }
    StdOut.printf("%d\n", Planets.length);
    StdOut.printf("%.2e\n", radius);
    for (int i = 0; i < Planets.length; i++) {
	     StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   		   Planets[i].xxPos, Planets[i].yyPos, Planets[i].xxVel, Planets[i].yyVel, Planets[i].mass, Planets[i].imgFileName);
     }
  }

  public static double readRadius(String text){
      In string = new In(text);
      double first = string.readDouble();
      return string.readDouble();
  }

  public static Planet[] readPlanets(String text){
    In string = new In(text);
    int NumberOfPlanets = string.readInt();
    Planet[] ListOfPlanets = new Planet[NumberOfPlanets];
    double radius = string.readDouble();
    for(int i = 0; i < NumberOfPlanets; i++){
      double px = string.readDouble();
      double py = string.readDouble();
      double vx = string.readDouble();
      double vy = string.readDouble();
      double m = string.readDouble();
      String img = string.readString();
      ListOfPlanets[i] = new Planet(px, py, vx, vy, m, img);
    }
    return ListOfPlanets;
  }
}
