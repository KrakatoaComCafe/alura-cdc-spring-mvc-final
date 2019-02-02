package br.com.casadocodigo.loja.conf;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServletSpringMVC extends AbstractAnnotationConfigDispatcherServletInitializer{

	//configurações para quando o sistema subir
	@Override
	protected Class<?>[] getRootConfigClasses() {
		//return null; configuração até o momento de adicionar segurança de login
		return new Class[] {SecurityConfiguration.class,
				AppWebConfiguration.class,
				JPAConfiguration.class,
				JPAProductionConfiguration.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {/*AppWebConfiguration.class, JPAConfiguration.class*/};//removido para ser colocado ao root por causar problemas ao injetar o DAO
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {"/"};
	}

	@Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        
        return new Filter[] {encodingFilter, new OpenEntityManagerInViewFilter()}; //OpenEntityManagerInViewFilter() resolve o problema de lazy fetch, mantendo o entitymanager aberto
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
			registration.setMultipartConfig(new MultipartConfigElement(""));
	}
	
//	//define o profile a ser usado como padrão
//	@Override
//	public void onStartup(ServletContext servletContext) throws ServletException {
//		super.onStartup(servletContext);
//		
//		servletContext.addListener(RequestContextListener.class);
//		servletContext.setInitParameter("spring.profiles.active", "dev");
//	}
}