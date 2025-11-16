package br.edu.ifes.si.tpa.trabalho1;

import br.edu.ifes.si.tpa.trabalho1.estruturas.Aresta;
import br.edu.ifes.si.tpa.trabalho1.estruturas.Grafo;
import javafx.scene.paint.Color;

import java.util.*;

public class MapColors {

    private final Map<Integer, Color> nodeColors = new HashMap<>();
    private final List<Color> palette = Arrays.asList(
            Color.web("#4991df"),
            Color.web("#70b8ff"),
            Color.web("#9ed3ff"),
            Color.web("#cfe8ff")
    );

    public MapColors(Grafo g) {

        for (int v = 0; v < g.V(); v++) {
            nodeColors.put(v, getRandomPaletteColor());
        }
    }

    private Color getRandomPaletteColor() {
        return palette.get(new Random().nextInt(palette.size()));
    }

    public Color getColor(int node) {
        return nodeColors.get(node);
    }
}
