package leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import leilao.builder.CriadorDeLeilao;
import leilao.dominio.Leilao;
import leilao.infra.dao.LeilaoDao;
import leilao.infra.dao.LeilaoDaoFalso;
import leilao.infra.dao.RepositorioLeilao;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(2015, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();

		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		   
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

		 // criando o mock!
		 RepositorioLeilao daoMock = mock(LeilaoDao.class);
		 
		 // ensinando o mock a reagir da maneira que esperamos!
		 when(daoMock.correntes()).thenReturn(leiloesAntigos);
		 
		 EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock);
		 encerrador.encerra();
		 
 
		 assertTrue( leilao1.isEncerrado());
		 assertTrue( leilao2.isEncerrado());
		 
		 

		
	
		   
	}
}
