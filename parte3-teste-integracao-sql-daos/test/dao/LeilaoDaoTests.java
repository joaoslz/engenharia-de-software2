package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import modelo.Leilao;
import modelo.Usuario;

public class LeilaoDaoTests {

	private Session session;
	private LeilaoDao leilaoDao;
	private UsuarioDao usuarioDao;
	private Usuario usuario;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		session.getTransaction().begin();

		leilaoDao = new LeilaoDao(session);
		usuarioDao = new UsuarioDao(session);

		usuario = new Usuario("Fulano da silva", "fulano@dasilva.com.br");
	}

	@After
	public void depois() {
		// faz o rollback
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveContarLeiloesNaoEncerrados() {
		// criamos os dois leiloes
		Leilao leilaoAtivo = new Leilao("Geladeira", 1500.0, usuario, false);
		Leilao leilaoEncerrado = new Leilao("XBox", 700.0, usuario, false);

		leilaoEncerrado.encerra();

		// persistimos todos no banco
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(leilaoAtivo);
		leilaoDao.salvar(leilaoEncerrado);

		// invocamos a acao que queremos testar
		// pedimos o total para o DAO
		long total = leilaoDao.total();

		assertEquals(1L, total);
	}

	@Test
	public void deveRetornarZeroSeNaoHaLeiloesNovos() {

		Leilao encerrado = new Leilao("XBox", 700.0, usuario, false);
		Leilao tambemEncerrado = new Leilao("Geladeira", 1500.0, usuario, false);

		encerrado.encerra();
		tambemEncerrado.encerra();

		usuarioDao.salvar(usuario);
		leilaoDao.salvar(encerrado);
		leilaoDao.salvar(tambemEncerrado);

		long total = leilaoDao.total();

		assertEquals(0L, total);
	}

	@Test
	public void deveTrazerSomenteLeiloesAntigos() {

		Leilao recente = new Leilao("XBox", 700.0, this.usuario, false);
		Leilao antigo = new Leilao("Geladeira", 1500.0, this.usuario, true);

		Calendar dataRecente = Calendar.getInstance();
		Calendar dataAntiga = Calendar.getInstance();

		dataAntiga.add(Calendar.DAY_OF_MONTH, -10);

		recente.setDataAbertura(dataRecente);
		antigo.setDataAbertura(dataAntiga);

		usuarioDao.salvar(usuario);
		leilaoDao.salvar(recente);
		leilaoDao.salvar(antigo);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(1, antigos.size());
		assertEquals("Geladeira", antigos.get(0).getNome());
	}

	@Test
	public void deveTrazerSomenteLeiloesAntigosHaMaisDe7Dias() {

		Leilao noLimite = new Leilao("XBox", 700.0, this.usuario, false);

		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -7);

		noLimite.setDataAbertura(dataAntiga);

		usuarioDao.salvar(this.usuario);
		leilaoDao.salvar(noLimite);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(1, antigos.size());
	}

	@Test
	public void deveDeletarUmLeilao() {
		Leilao leilao = new Leilao();

		usuarioDao.salvar(usuario);
		leilaoDao.salvar(leilao);

		session.flush();

		leilaoDao.deleta(leilao);

		assertNull(leilaoDao.porId(leilao.getId()));
	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		
		Calendar fimDoIntervalo = Calendar.getInstance();
		
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		
		Calendar dataDoLeilao2 = Calendar.getInstance();
		dataDoLeilao2.add(Calendar.DAY_OF_MONTH, -20);


		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("XBox", 700.0, usuario, false);
		leilao1.setDataAbertura(dataDoLeilao1);
		
		Leilao leilao2 = new Leilao("Geladeira", 1700.0, usuario, false);
		leilao2.setDataAbertura(dataDoLeilao2);

		// persistindo os objetos no banco
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(1, leiloes.size());
		assertEquals("XBox", leiloes.get(0).getNome());
	}
}
