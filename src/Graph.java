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
        List<Localisation> zoneInondee = new ArrayList<>();

        Set<Long> dejaVisite = new HashSet<>();

        Deque<Localisation> file = new ArrayDeque<>();

        for (long id : idsOrigin) {
            Localisation depart = noeuds.get(id);
            if (depart != null && dejaVisite.add(id)) {
                file.add(depart);
                zoneInondee.add(depart);
            }
        }

        while (!file.isEmpty()) {
            Localisation courant = file.poll();

            for (Arc arc : ruesSortantes.get(courant.getId())) {
                Localisation voisin = arc.getArrivee();

                boolean peutPropager = voisin.getAltitude() <= courant.getAltitude() + epsilon;

                if (peutPropager && dejaVisite.add(voisin.getId())) {
                    file.add(voisin);
                    zoneInondee.add(voisin);
                }
            }
        }

        return zoneInondee.toArray(new Localisation[0]);
    }

    public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idOrigin, long idDestination, Localisation[] floodedZone) {

            Set<Long> inondee = new HashSet<>();
            for (Localisation loc : floodedZone) {
                inondee.add(loc.getId());
            }

            Localisation depart = noeuds.get(idOrigin);
            Localisation destination = noeuds.get(idDestination);
            if (depart == null || destination == null) return new ArrayDeque<>();

            Map<Long, Long> predecesseur = new HashMap<>();
            Set<Long> vus = new HashSet<>();
            Queue<Localisation> file = new LinkedList<>();

            if (inondee.contains(idOrigin)) return new ArrayDeque<>();

            vus.add(idOrigin);
            predecesseur.put(idOrigin, null);
            file.add(depart);

            boolean trouve = false;

            while (!file.isEmpty() && !trouve) {
                Localisation courant = file.poll();

                List<Arc> voisins = ruesSortantes.get(courant.getId());
                if (voisins == null) continue;

                for (Arc arc : voisins) {
                    Localisation voisin = arc.getArrivee();
                    long idVoisin = voisin.getId();

                    if (!vus.contains(idVoisin) && !inondee.contains(idVoisin)) {
                        vus.add(idVoisin);
                        predecesseur.put(idVoisin, courant.getId());
                        file.add(voisin);

                        if (idVoisin == idDestination) {
                            trouve = true;
                            break;
                        }
                    }
                }
            }

            if (!predecesseur.containsKey(idDestination)) return new ArrayDeque<>();

            Deque<Localisation> chemin = new ArrayDeque<>();
            Long courantId = idDestination;
            while (courantId != null) {
                chemin.addFirst(noeuds.get(courantId));
                courantId = predecesseur.get(courantId);
            }

            return chemin;
    }

    public Map<Localisation, Double> determinerChronologieDeLaCrue(long[] idsOrigin, double vWaterInit, double k) {


        Map<Localisation, Double> tFlood = new LinkedHashMap<>();


        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e[1]));


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


            Localisation currentLoc = this.noeuds.get(currentId);
            if (tFlood.get(currentLoc) < currentTime) continue;


            for (Arc arc : this.ruesSortantes.get(currentId)) {
                Localisation voisin = arc.getArrivee();


                double pente = (currentLoc.getAltitude() - voisin.getAltitude()) / arc.getDistance();
                double nouvelleVWater = currentVWater + (k * pente);


                if (nouvelleVWater <= 0) continue;


                double temps = arc.getDistance() / nouvelleVWater;
                double nouveauTemps = currentTime + temps;


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