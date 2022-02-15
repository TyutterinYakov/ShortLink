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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
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

	@Autowired
	public LinkController(LinkService linkService) {
		super();
		this.linkService = linkService;
	}
	
	@PostMapping("/")
	public String generateLink(@RequestParam("redirectLink") String redirectLink, 
			@RequestParam(name="time", required = false) Double time, HttpServletRequest request) throws IOException {
		return host+linkService.saveOrGetLink(redirectLink, time);
	}
	
	@GetMapping("/{generated}")
	public ResponseEntity<?> redirectLink(@PathVariable("generated")String generated, HttpServletRequest request) throws NotFoundException {
		Map<String, Object> headers = new TreeMap<>();
		request.getHeaderNames().asIterator().forEachRemaining((s)->{
			headers.put(s, request.getHeader(s));
		});
		
		StringBuilder sb = new StringBuilder(headers.toString().replaceAll(", ", "&"));
		System.out.println(sha1(sb.substring(1, sb.length()-1)));
 
 
		
		HttpHeaders header = new HttpHeaders();
		header.add("Location", linkService.getLinkByGenerated(generated, request).getLinkRedirect());
		return new ResponseEntity<>(header,HttpStatus.FOUND);
	}
	@GetMapping("/{generated}/stats")
	public String statsLink(@PathVariable("generated") String generated) throws NotFoundException {
		Link link = linkService.getLinkByGeneratedToStats(generated);
		return "Редирект на: "+link.getLinkRedirect()
				+ " Переходы: "+link.getCountClick();
	}
	
	@GetMapping("/{generated}/delete")
	public ResponseEntity<?> deleteLink(@PathVariable("generated") String generated) throws NotFoundException{
		linkService.deleteLinkByGenerated(generated);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	
	@ExceptionHandler
	public ResponseEntity<Void> notFoundException(NotFoundException ex){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	
	public static String sha1(String input) {
	    String sha1 = null;
	    try {
	        MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
	        msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
	        sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
	    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
	    	e.printStackTrace();
	    }
	    return sha1;
	}
	
	
}
