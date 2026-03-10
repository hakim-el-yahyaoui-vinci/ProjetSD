public class Arc {

    private Localisation origine;

    private Localisation arrivee;

    private double distance;

    private String nom;

    public Arc(Localisation origine, Localisation arrivee, double distance, String nom) {
        this.origine = origine;
        this.arrivee = arrivee;
        this.distance = distance;
        this.nom = nom;
    }

    public Localisation getOrigine() {
        return origine;
    }

    public Localisation getArrivee() {
        return arrivee;
    }

    public double getDistance() {
        return distance;
    }

    public String getNom() {
        return nom;
    }
}
