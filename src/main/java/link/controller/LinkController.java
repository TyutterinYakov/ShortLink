package link.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

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

import link.model.Link;
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
	public String generateLink(@RequestParam("redirectLink") String redirectLink, 
			@RequestParam(name="time", required = false) Double time, @RequestParam(name="key") String key, HttpServletRequest request) throws IOException {
		try {
			return host+linkService.saveOrGetLink(redirectLink, time, key, request);
		} catch (NoSuchAlgorithmException e) {
			logger.error("",e);
			return "Error create link";
		}
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
	public void deleteLink(@PathVariable("generated") String generated, HttpServletRequest request, @PathVariable("key") String key) throws NotFoundException, NoSuchAlgorithmException{
		linkService.deleteLinkByGenerated(generated, request, key);
		
	}
	
	
	@ExceptionHandler
	public ResponseEntity<Void> notFoundException(NotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	
	
}
