package modelo;

public class CriadorDeLeilao {

	private Leilao leilao;

	public CriadorDeLeilao para(String descricao) {
		leilao = new Leilao(descricao);
		return this;
	}

	public CriadorDeLeilao lance(Usuario usuario, double valor) {
        this.leilao.propoe(new Lance(usuario, valor));
		
		return this;
	}

	public Leilao constroi() {
		return leilao;
	}

}
