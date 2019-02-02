package br.com.casadocodigo.loja.conf;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.common.cache.CacheBuilder;

import br.com.casadocodigo.loja.controllers.HomeController;
import br.com.casadocodigo.loja.dao.ProdutoDAO;
import br.com.casadocodigo.loja.infra.FileSaver;
import br.com.casadocodigo.loja.models.CarrinhoCompras;

@EnableCaching //pra não ficar indo no banco toda hora
@EnableWebMvc
@ComponentScan(basePackageClasses= {HomeController.class, ProdutoDAO.class, FileSaver.class, CarrinhoCompras.class})
public class AppWebConfiguration extends WebMvcConfigurerAdapter { //extends para o spring parar de procurar img,css e talz
	
	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix( ".jsp");
		
		//resolver.setExposeContextBeansAsAttributes(true);
		resolver.setExposedContextBeanNames("carrinhoCompras");
		
		return resolver;
	}

	@Bean
	public MessageSource messageSource(){
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("/WEB-INF/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(1);
		return messageSource;
	}
	
	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService conversionService = 
				new DefaultFormattingConversionService();
		DateFormatterRegistrar registrar = new DateFormatterRegistrar();
		registrar.setFormatter(new DateFormatter("dd/MM/yyyy"));
		registrar.registerFormatters(conversionService);
		
		return conversionService;
	}
	
    @Bean
	public MultipartResolver multipartResolver(){
		return new StandardServletMultipartResolver();
	}
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();//passa requisição default pro tomcat e ele que se vire
    }
	
    @Bean
    public RestTemplate restTemplate() {
    	return new RestTemplate();
    }
    
    @Bean //gerenciador de cache
    public CacheManager cacheManager() {
    	//Cache para 100 elementos; expira após 5 min
    	CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
    		.maximumSize(100)//tamanho de 100 elementos
    		.expireAfterAccess(5, TimeUnit.MINUTES);
    	
    	GuavaCacheManager manager = new GuavaCacheManager();
    	manager.setCacheBuilder(builder);
    	
    	return manager;
    }
    
    //define os diferentes tipos de reposta que pode ser enviado
	@Bean
	public ViewResolver contentNegotiationViewResolver(ContentNegotiationManager manager) {
		ArrayList<ViewResolver> viewResolvers = new ArrayList<>();//armazena os diferentes tipo
		viewResolvers.add(internalResourceViewResolver());//jsp
		viewResolvers.add(new JsonViewResolver());//json
		
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(viewResolvers);//adiciona os diferentes tipos
		resolver.setContentNegotiationManager(manager);
		
		return resolver;
	}
	
	//interceptador para trocar a localização (idioma)
	//------------------------------------------------
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LocaleChangeInterceptor());
	}
	//salva nos cookies
	@Bean
	public LocaleResolver localeResolver() {
		return new CookieLocaleResolver();
	}
	//-----------------------------------------------
	
	//configura o envio do email ao finalizar a compra
	@Bean
	public MailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com"); //servidor smtp
		mailSender.setUsername("email@bacanudo.com"); //seu email
		mailSender.setPassword("aqui_eh_segredo"); //password
		mailSender.setPort(587); //porta do serviço de email

		//habilita o sistema para usar tls
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", "true");
		mailProperties.put("mail.smtp.starttls.enable", "true");
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
	}
}
	