package leilao.infra.dao;

import java.util.List;

import leilao.dominio.Leilao;

public interface RepositorioLeilao {

	/* (non-Javadoc)
	 * @see leilao.infra.dao.RepositorioDeLeilao#salva(leilao.dominio.Leilao)
	 */
	void salva(Leilao leilao);

	List<Leilao> encerrados();

	List<Leilao> correntes();

	void atualiza(Leilao leilao);

}