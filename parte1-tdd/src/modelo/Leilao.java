package modelo;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leilao {

	private String descricao;
	private List<Lance> lances;

	public Leilao(String descricao) {
		this.descricao = descricao;
		this.lances = new ArrayList<Lance>();
	}

	public void propoe(Lance lance) {
	    Usuario usuario = lance.getUsuario();
	    
	   

	    
		if(lances.isEmpty() || naoEhOultimoLanceDadoPelo(usuario)  
			&& qtdDeLancesDo(usuario) ){
	        lances.add(lance);
	    }

	}

	private boolean qtdDeLancesDo(Usuario usuario) {
		 int total = 0;
		    for(Lance l : this.lances) {
		       if(l.getUsuario().equals(usuario)) 
		          total++;
		       }
		
		return ( total < 5 );
	}

	private boolean naoEhOultimoLanceDadoPelo(Usuario usuario) {
		return ! (lances.get(lances.size() - 1).
				             getUsuario().equals(usuario) );
	}

	public String getDescricao() {
		return descricao;
	}

	public List<Lance> getLances() {
		return Collections.unmodifiableList(lances);
	}

	public void dobraLance(Usuario usuario) {
		Lance ultimoLance = ultimoLanceDo(usuario);
		
		if (ultimoLance != null) {
			propoe(new Lance(usuario, ultimoLance.getValor() * 2));
		}
	}

	private Lance ultimoLanceDo(Usuario usuario) {
		Lance ultimo = null;
		for (Lance lance : lances) {
			if (lance.getUsuario().equals(usuario))
				ultimo = lance;
		}

		return ultimo;
	}

	// public void dobraLance(Usuario usuario) {
	// Lance ultimoLance = null;
	// for (Lance lance : this.lances) {
	// if ( lance.getUsuario().equals(usuario) ) {
	// ultimoLance = lance;
	// }
	// }
	// if (ultimoLance != null) {
	// this.propoe( new Lance(usuario, (ultimoLance.getValor() * 2) ) );
	// }
	// }

}
