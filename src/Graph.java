import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Graph {

    private Map<Long, Localisation> noeuds;

    private Map<Long, List<Arc>> ruesSortantes;

    public Graph(String localisations, String roads)  {

        this.noeuds = new HashMap<>();
        this.ruesSortantes = new HashMap<>();

        try {
            BufferedReader lecteurNoeuds = new BufferedReader(new FileReader(localisations));
            lecteurNoeuds.readLine();
            String ligneNoeud;

            while ((ligneNoeud = lecteurNoeuds.readLine()) != null) {

                String[] morceaux = ligneNoeud.split(",");

                if (morceaux.length >= 5) {
                    long id    = Long.parseLong(morceaux[0]);
                    String nom = morceaux[1];
                    double lat = Double.parseDouble(morceaux[2]);
                    double lon = Double.parseDouble(morceaux[3]);
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
            lecteurArcs.readLine();
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

    public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
        // TODO: On va écrire la logique ici bientôt !
        return null ;
    }

    public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idOrigin, long idDestination, Localisation[] floodedZone) {
        // TODO: On va écrire la logique ici bientôt !
        return null ;
    }

    public Map<Localisation, Double> determinerChronologieDeLaCrue(long[] idsOrigin, double vWaterInit, double k) {

        // Map résultat : localisation -> temps d'inondation
        // LinkedHashMap pour garder l'ordre d'insertion (ordre croissant grâce à Dijkstra)
        Map<Localisation, Double> tFlood = new LinkedHashMap<>();

        // Pour Dijkstra : PriorityQueue sur le temps (le plus petit temps en premier)
        // Chaque entrée : [id du noeud, temps actuel, vitesse actuelle de l'eau]
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e[1]));

        // Initialisation avec les points de départ
        for (long id : idsOrigin) {
            Localisation loc = this.noeuds.get(id);
            if (loc != null) {
                pq.add(new double[]{id, 0.0, vWaterInit});
                tFlood.put(loc, 0.0);
            }
        }

        while (!pq.isEmpty()) {
            double[] current = pq.poll();
            long currentId = (long) current[0];
            double currentTime = current[1];
            double currentVWater = current[2];

            // Si on a déjà trouvé un meilleur chemin, on skip
            Localisation currentLoc = this.noeuds.get(currentId);
            if (tFlood.get(currentLoc) < currentTime) continue;

            // On explore les voisins
            for (Arc arc : this.ruesSortantes.get(currentId)) {
                Localisation voisin = arc.getArrivee();

                // Calcul de la pente et nouvelle vitesse
                double pente = (currentLoc.getAltitude() - voisin.getAltitude()) / arc.getDistance();
                double nouvelleVWater = currentVWater + (k * pente);

                // Condition : l'eau s'arrête si vitesse <= 0
                if (nouvelleVWater <= 0) continue;

                // Calcul du temps pour atteindre ce voisin
                double temps = arc.getDistance() / nouvelleVWater;
                double nouveauTemps = currentTime + temps;

                // On met à jour si on a trouvé un chemin plus rapide
                if (!tFlood.containsKey(voisin) || nouveauTemps < tFlood.get(voisin)) {
                    tFlood.put(voisin, nouveauTemps);
                    pq.add(new double[]{voisin.getId(), nouveauTemps, nouvelleVWater});
                }
            }
        }

        return tFlood;
    }

    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation, double vVehicule, Map<Localisation,Double> tFlood) {
        // TODO: On va écrire la logique ici bientôt !
        return null ;
    }
}