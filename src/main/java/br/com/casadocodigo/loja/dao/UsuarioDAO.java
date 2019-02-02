package br.com.casadocodigo.loja.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import br.com.casadocodigo.loja.models.Usuario;

@Repository
//@Transactional //não é recomendado colocar Transactional no DAO
public class UsuarioDAO implements UserDetailsService {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Usuario loadUserByUsername(String email) { //usuario precisa implementar UserDetails
		List<Usuario> usuarios = manager.createQuery("select u from Usuario u "
				+ "where u.email = :email", Usuario.class)
		.setParameter("email", email)
		.getResultList();
		
		if(usuarios.isEmpty()) {
			throw new UsernameNotFoundException("Usuário " + email +" não foi encontraro!");
		}
		
		return usuarios.get(0);
	}

	public void gravar(Usuario usuario) {
		manager.persist(usuario); //para persistir, deve ter uma transação
	}

	
	
}