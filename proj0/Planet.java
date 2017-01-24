public class Planet {
  public double xxPos;
  public double yyPos;
  public double xxVel;
  public double yyVel;
  public double mass;
  public String imgFileName;
  public Planet(double xP, double yP, double xV, double yV, double m, String img){
    xxPos = xP;
    yyPos = yP;
    xxVel = xV;
    yyVel = yV;
    mass = m;
    imgFileName = img;
  };

  public Planet(Planet p){
    xxPos = p.xxPos;
    yyPos = p.yyPos;
    xxVel = p.xxVel;
    yyVel = p.yyVel;
    mass = p.mass;
    imgFileName = p.imgFileName;
  };

  public double calcDistance(Planet p){
    return Math.sqrt((p.xxPos-xxPos)*(p.xxPos-xxPos)+(p.yyPos-yyPos)*(p.yyPos-yyPos));
  };

  public double calcForceExertedBy(Planet p){
    return (6.67E-11*p.mass*mass)/Math.pow(calcDistance(p),2);
  };

  public double calcForceExertedByX(Planet p){
    return (calcForceExertedBy(p)*(p.xxPos-xxPos))/calcDistance(p);
  };

  public double calcForceExertedByY(Planet p){
    return (calcForceExertedBy(p)*(p.yyPos-yyPos))/calcDistance(p);
  };

  public double calcNetForceExertedByX(Planet[] a){
    double net = 0;
    for(Planet p : a){
      if (!this.equals(p)){
        net += calcForceExertedByX(p);
      };
    };
    return net;
  };

  public double calcNetForceExertedByY(Planet[] a){
    double net = 0;
    for(Planet p : a){
      if (!this.equals(p)){
        net += calcForceExertedByY(p);
      }
    };
    return net;
  };

  public void update(double t, double Fx, double Fy){
    double ax = Fx/mass;
    double ay = Fy/mass;
    xxVel = xxVel + t*ax;
    yyVel = yyVel + t*ay;
    xxPos = xxPos + t*xxVel;
    yyPos = yyPos + t*yyVel;
  }

  public void draw(){
    StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
  }
}
