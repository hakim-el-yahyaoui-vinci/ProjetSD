import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Deque;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Graph {

    private Map<Long, Localisation> noeuds;

    private Map<Long, List<Arc>> ruesSortantes;

    public Graph(String localisations, String roads)  {

        this.noeuds = new HashMap<>();
        this.ruesSortantes = new HashMap<>();

        try {
            BufferedReader lecteurNoeuds = new BufferedReader(new FileReader(localisations));
            String ligneNoeud;

            while ((ligneNoeud = lecteurNoeuds.readLine()) != null) {

                String[] morceaux = ligneNoeud.split(",");

                if (morceaux.length >= 5) {
                    long id = Long.parseLong(morceaux[0]);
                    double lat = Double.parseDouble(morceaux[1]);
                    double lon = Double.parseDouble(morceaux[2]);
                    String nom = morceaux[3];
                    double alt = Double.parseDouble(morceaux[4]);

                    Localisation nouveauPoint = new Localisation(id, lat, lon, nom, alt);

                    this.noeuds.put(id, nouveauPoint);

                    this.ruesSortantes.put(id, new ArrayList<>());
                }
            }
            lecteurNoeuds.close();

        } catch (Exception e) {
            System.out.println("Oups, petit problème en lisant le fichier des points : " + e.getMessage());
        }

        try {
            BufferedReader lecteurArcs = new BufferedReader(new FileReader(roads));
            String ligneArc;

            while ((ligneArc = lecteurArcs.readLine()) != null) {
                String[] morceaux = ligneArc.split(",");

                if (morceaux.length >= 4) {
                    long idOrigine = Long.parseLong(morceaux[0]);
                    long idArrivee = Long.parseLong(morceaux[1]);
                    double distance = Double.parseDouble(morceaux[2]);
                    String nomRue = morceaux[3];

                    Localisation origine = this.noeuds.get(idOrigine);
                    Localisation arrivee = this.noeuds.get(idArrivee);

                    if (origine != null && arrivee != null) {

                        Arc nouvelleRue = new Arc(origine, arrivee, distance, nomRue);

                        this.ruesSortantes.get(idOrigine).add(nouvelleRue);
                    }
                }
            }
            lecteurArcs.close();

        } catch (Exception e) {
            System.out.println("Oups, petit problème en lisant le fichier des rues : " + e.getMessage());
        }
    }

    public Localisation[] determinerZoneInondee(long[] idsDepart, double epsilon) {
        // TODO
        return null ;
    }

    public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idDepart, long idArrivee, Localisation[] zoneInondee) {
        // TODO
        return null ;
    }

    public Map<Localisation, Double> determinerChronologieDeLaCrue(long[] idsDepart, double vWaterInit, double k) {
        // TODO
        return null ;
    }

    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idDepart, long idEvacuation, double vVehicule, Map<Localisation, Double> tFlood) {
        // TODO
        return null ;
    }
}