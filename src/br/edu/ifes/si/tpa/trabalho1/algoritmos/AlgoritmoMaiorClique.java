/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifes.si.tpa.trabalho1.algoritmos;

import br.edu.ifes.si.tpa.trabalho1.estruturas.Grafo;
import java.util.ArrayList;
import java.util.List;

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
