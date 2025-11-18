package br.edu.ifes.si.tpa.trabalho1.algoritmos;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifes.si.tpa.trabalho1.estruturas.Grafo;

public class AlgoritmoMaiorClique {
   
  private Grafo grafo;
  private List<Integer> cliqueAtual;
  private List<Integer> maiorClique;

  public AlgoritmoMaiorClique(Grafo grafo) {
    this.grafo = grafo;
    this.cliqueAtual = new ArrayList<>();
    this.maiorClique = new ArrayList<>();
  }

  public List<Integer> encontrarMaiorClique() {
    encontrarMaiorClique(0);
    return maiorClique;
  }

  private void encontrarMaiorClique(int vertice) {
    if (vertice == grafo.V()) {
      if (cliqueAtual.size() > maiorClique.size()) {
        maiorClique = new ArrayList<>(cliqueAtual);
      }
      return;
    }

    boolean podeAdicionar = true;
    for (int v : cliqueAtual) {
      if (!grafo.existeArestaEntre(vertice, v)) {
        podeAdicionar = false;
        break;
      }
    }

    if (podeAdicionar) {
      cliqueAtual.add(vertice);
      encontrarMaiorClique(vertice + 1);
      cliqueAtual.remove(cliqueAtual.size() - 1);
    }

    encontrarMaiorClique(vertice + 1);
  } 
}
