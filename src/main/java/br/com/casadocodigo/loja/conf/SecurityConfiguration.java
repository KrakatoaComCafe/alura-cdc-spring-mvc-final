package br.com.casadocodigo.loja.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

import br.com.casadocodigo.loja.dao.UsuarioDAO;

@EnableWebSecurity //anotação habilita o que a classe faz
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UsuarioDAO usuarioDao;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//****************
		//faz o heroku chorão usar o UTF-8
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);
		http.addFilterBefore(encodingFilter, CsrfFilter.class);
		//***************
		
		http.authorizeRequests()
			.antMatchers("/produtos/form").hasRole("ADMIN")
			.antMatchers("/produtos").hasRole("ADMIN")
			.antMatchers("/produtos/").hasRole("ADMIN")
			.antMatchers("/resources/**").permitAll()
			.antMatchers("/produtos/**").permitAll() //produtos e o que vier pra frente, permite tudo (que n foi bloqueado anteriormente)
			.antMatchers("/carrinho/**").permitAll()
			.antMatchers("/pagamento/**").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers("/url-magica-maluca-falsiba59flafn15d9audbg3f57a9rbga95l19sdk653jfnb").permitAll()
			.anyRequest().authenticated() //verifica autenticação em todo request
			.and().formLogin().loginPage("/login").defaultSuccessUrl("/produtos").permitAll()
			.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll().logoutSuccessUrl("/login");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usuarioDao) //usuarioDao -> precisa implementar UserDetailsService 
			.passwordEncoder(new BCryptPasswordEncoder());
	}
	
	// Forma recomendada de ignorar no filtro de segurança as requisições para recursos estáticos
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }
}
