package modelo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.*;


import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AvaliadorTest {

	private Avaliador leiloeiro;

	private Usuario joao;
	private Usuario jose;
	private Usuario maria;

	@BeforeClass
	public static void testandoBeforeClass() {
		System.out.println("before class");
	}

	@AfterClass
	public static void testandoAfterClass() {
		System.out.println("after class");
	}

	@Before
	public void setup() {
		leiloeiro = new Avaliador();

		this.joao = new Usuario("João");
		this.jose = new Usuario("José");
		this.maria = new Usuario("Maria");

		System.out.println("Testando");

	}

	@After
	public void finaliza() {
		System.out.println("Finalizando");
	}

	@Test
	public void deveEntenderLancesEmOrdemCrescente() {
		// 1. montar um cenário

		Leilao leilao = new CriadorDeLeilao().
				 para("Playstation 3 Novo").
				 lance(joao, 300.0).
				 lance(jose, 400.0).
				 lance(maria, 550.0).
				 constroi();

		// 2. executar uma ação
		leiloeiro.avalia(leilao);

//		double maiorLance = 550;
//		double menorLance = 300;

		//assertEquals(maiorLance, leiloeiro.getMaiorLance(), 0.00001);
//		assertEquals(menorLance, leiloeiro.getMenorLance(), 0.00001);

		assertThat(leiloeiro.getMaiorLance(), equalTo(550.0));
		assertThat(leiloeiro.getMenorLance(), equalTo(300.0));
	}

	@Test
	public void deveEntenderLeilaoComApenasUmLance() {
		Leilao leilao = new Leilao("Playstation 3 Novo");

		leilao.propoe(new Lance(joao, 1000.0));

		leiloeiro.avalia(leilao);

		Assert.assertEquals(1000, leiloeiro.getMaiorLance(), 0.0001);
		Assert.assertEquals(1000, leiloeiro.getMenorLance(), 0.0001);
	}

	@Test
	public void deveEncontrarOsTresMaioresLances() {

		Leilao leilao = new CriadorDeLeilao().
				para("Playstation 3 Novo").
				lance(joao, 100.0).
				lance(maria, 200.0).
				lance(joao, 300.0).
				lance(maria, 400.0).
				constroi();

		leiloeiro.avalia(leilao);

		List<Lance> maiores = leiloeiro.getTresMaiores();

		assertEquals(3, maiores.size());
		
		
//		assertEquals(400, maiores.get(0).getValor(), 0.00001);
//		assertEquals(300, maiores.get(1).getValor(), 0.00001);
//		assertEquals(200, maiores.get(2).getValor(), 0.00001);
		
        assertThat(maiores, hasItems(
                new Lance(maria, 400), 
                new Lance(joao, 300),
                new Lance(maria, 200)
        ));

	}

	@Test(expected = RuntimeException.class)
	public void naoDeveAvaliarLeilaoSemLances() {

		Leilao leilao = new CriadorDeLeilao().para("Ultrabook Dell Core i7").constroi();

		Avaliador leiloeiro = new Avaliador();
		leiloeiro.avalia(leilao);

	}
}
