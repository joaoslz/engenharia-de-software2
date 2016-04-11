package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import modelo.Usuario;

public class UsuarioDaoTest {
	
	
	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
	}
	
	
	@After
	public void depois() {
		session.close();
	}

	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {
	 
		Usuario novoUsuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		usuarioDao.salvar(novoUsuario);

		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");

		assertEquals("João da Silva", usuarioDoBanco.getNome());
		assertEquals("joao@dasilva.com.br", usuarioDoBanco.getEmail());

		/*
	
       Session session = Mockito.mock(Session.class);
	        Query query = Mockito.mock(Query.class);
	        UsuarioDao usuarioDao = new UsuarioDao(session);

	        Usuario usuario = new Usuario
	              ("João da Silva", "joao@dasilva.com.br");

	        String sql = "from Usuario u where u.nome = :nome and u.email = :email";

	        Mockito.when(session.createQuery(sql)).thenReturn(query);
	        Mockito.when(query.uniqueResult()).thenReturn(usuario);
	       
	        Mockito.when(query.setParameter("nome","João da Silva")). 
	                           thenReturn(query);
	      
	        Mockito.when(query.setParameter("email", 
	                          "joao@dasilva.com.br")).thenReturn(query);
	        
	        
	        Usuario usuarioDoBanco = usuarioDao
	                .porNomeEEmail("João da Silva", 
	                               "joao@dasilva.com.br");

	        assertEquals(usuario.getNome(), usuarioDoBanco.getNome());
	        assertEquals(usuario.getEmail(), usuarioDoBanco.getEmail());
	   */

	   }
	
	@Test
	public void deveRetornarNullParaUsuarioNaoCadastrado() {

		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("Fulano de Tal", "fulano@detal.com");

		assertNull(usuarioDoBanco);
	}

	
	@Test
	public void deveDeletarUmUsuario() {

		
		Usuario usuario = new Usuario("Fulano da silva", "fulano@dasilva.com.br");

		usuarioDao.salvar(usuario);
		usuarioDao.deletar(usuario);

		session.flush();
		session.clear();
		

		Usuario usuarioNoBanco = usuarioDao
				        .porNomeEEmail("Fulano da silva", "fulano@dasilva.com.br");

		assertNull(usuarioNoBanco);

	}
	
	 @Test
	    public void deveAlterarUmUsuario() {
	        
			Usuario usuario = new Usuario("Fulano da Silva", "fulano@dasilva.com.br");


	        usuarioDao.salvar(usuario);

	        usuario.setNome("João da Silva");
	        usuario.setEmail("joao@silva.com.br");

	        usuarioDao.atualizar(usuario);

	        session.flush();

	        Usuario novoUsuario = 
	                usuarioDao.porNomeEEmail("João da Silva", "joao@silva.com.br");
	       
	        assertNotNull(novoUsuario);

	        Usuario usuarioInexistente = 
	                usuarioDao.porNomeEEmail("Fulano da Silva", "fulano@dasilva.com.br");
	        assertNull(usuarioInexistente);

	    }
}
