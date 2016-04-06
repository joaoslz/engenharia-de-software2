package leilao.servico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import leilao.builder.CriadorDeLeilao;
import leilao.dominio.Leilao;
import leilao.infra.dao.LeilaoDao;
import leilao.infra.dao.RepositorioLeilao;
import leilao.infra.email.EnviadorDeEmail;

public class EncerradorDeLeilaoTest {

	private Calendar antiga;
	private RepositorioLeilao leilaoDaoMock;
	private EncerradorDeLeilao encerrador;
	private EnviadorDeEmail carteiroMock;

	@Before
	public void setup() {
		antiga = Calendar.getInstance();
		antiga.set(2015, 1, 20);

		leilaoDaoMock = Mockito.mock(LeilaoDao.class);
		carteiroMock = mock(EnviadorDeEmail.class);

		encerrador = new EncerradorDeLeilao(leilaoDaoMock, carteiroMock);

	}

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		// ensinando o mock a reagir da maneira que esperamos!
		Mockito.when(leilaoDaoMock.correntes()).thenReturn(leiloesAntigos);

		encerrador.encerra();

		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());

		Mockito.verify(leilaoDaoMock, times(1)).atualiza(leilao1);

		Mockito.verify(carteiroMock, times(1)).envia(leilao1);

		// passamos os mocks que serao verificados
		InOrder inOrder = Mockito.inOrder(leilaoDaoMock, carteiroMock);

		// a primeira invocação
		inOrder.verify(leilaoDaoMock, times(1)).atualiza(leilao1);

		// a segunra invocação
		inOrder.verify(carteiroMock, times(1)).envia(leilao1);

	}

	@Test
	public void leiloesQueComecaramOntemNaoPodemSerEncerrados() {
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);

		Leilao leilao1 = new CriadorDeLeilao().para("TV 4k").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("TV 8k").naData(ontem).constroi();

		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		when(leilaoDaoMock.correntes()).thenReturn(leiloesAntigos);

		encerrador.encerra();

		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveEncerrarCasoNaoHajaLeiloes() {

		when(leilaoDaoMock.correntes()).thenReturn(new ArrayList<Leilao>());

		encerrador.encerra();
		assertThat(encerrador.getTotalEncerrados(), equalTo(0));
	}

	@Test
	public void deveContinuarAExecucaoMesmoQuandoDaoFalha() {

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		when(leilaoDaoMock.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		Mockito.doThrow(new RuntimeException()).when(leilaoDaoMock).atualiza(leilao1);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDaoMock, carteiroMock);

		encerrador.encerra();

		Mockito.verify(leilaoDaoMock).atualiza(leilao2);
		Mockito.verify(carteiroMock).envia(leilao2);
	}

	@Test
	public void deveDesistirSeDaoFalhaPraSempre() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		RepositorioLeilao daoFalso = mock(RepositorioLeilao.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);

		Mockito.doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		Mockito.doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao2);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);

		encerrador.encerra();

		Mockito.verify(carteiroFalso, Mockito.never()).envia(leilao1);
		Mockito.verify(carteiroFalso, Mockito.never()).envia(leilao2);
	}

	@Test
	public void deveContinuarAExecucaoMesmoQuandoEnviadorDeEmaillFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		RepositorioLeilao daoFalso = mock(RepositorioLeilao.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		Mockito.doThrow(new RuntimeException()).when(carteiroFalso).envia(leilao1);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);

		encerrador.encerra();

		Mockito.verify(daoFalso).atualiza(leilao2);
		Mockito.verify(carteiroFalso).envia(leilao2);
	}

}
