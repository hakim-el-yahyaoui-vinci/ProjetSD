public class Localisation {

    private long id;
    private double latitude;
    private double longitude;
    private String nom;
    private double altitude;

    public Localisation(long id, double latitude, double longitude, String nom, double altitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nom = nom;
        this.altitude = altitude;
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNom() {
        return nom;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localisation)) return false;
        return this.id == ((Localisation) o).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }
}
