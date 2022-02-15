package link.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import link.service.LinkService;

@RestController
@RequestMapping
public class LinkController {
	
	@Value("${api.host}")
	private String host;
	private final LinkService linkService;

	private static final Logger logger = LoggerFactory.getLogger(LinkController.class);
	@Autowired
	public LinkController(LinkService linkService) {
		super();
		this.linkService = linkService;
	}
	
	@PostMapping("/")
	public ResponseEntity<String> generateLink(@RequestParam("redirectLink") String redirectLink, 
			@RequestParam(name="time", required = false) Double time, @RequestParam(name="key") String key, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
			return ResponseEntity.ok(host+linkService.saveOrGetLink(redirectLink, time, key, request));
	}
	
	@GetMapping("/{generated}")
	public ResponseEntity<?> redirectLink(@PathVariable("generated")String generated, HttpServletRequest request) throws NotFoundException, NoSuchAlgorithmException {
		HttpHeaders header = new HttpHeaders();
		header.add("Location", linkService.getLinkByGenerated(generated, request).getLinkRedirect());
		return new ResponseEntity<>(header,HttpStatus.FOUND);
	}
	@GetMapping("/{generated}/stats/{key}")
	public ResponseEntity<String> statsLink(@PathVariable("generated") String generated, @PathVariable("key") String key, HttpServletRequest request) throws NotFoundException, NoSuchAlgorithmException {
		return ResponseEntity.ok(linkService.getLinkByGeneratedToStats(generated, key, request));

	}
	
	@GetMapping("/{generated}/delete/{key}")
	public String deleteLink(@PathVariable("generated") String generated, HttpServletRequest request, @PathVariable("key") String key) throws NotFoundException, NoSuchAlgorithmException{
		linkService.deleteLinkByGenerated(generated, request, key);
		return "Short link deleted";
	}
	
	
	@ExceptionHandler
	public ResponseEntity<?> notFoundException(NotFoundException ex){
		return new ResponseEntity<>("Такая ссылка отсутствует или ее срок действия истек",HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler
	public ResponseEntity<?> noSuchAlgorithmException(NoSuchAlgorithmException ex){
		logger.error("",ex);
		return new ResponseEntity<>("Ошибка на стороне сервера",HttpStatus.BAD_GATEWAY);
	}
	
	
	
}
