package link.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import link.dao.LinkRepository;
import link.dao.UniqueClicksRepository;
import link.model.Link;
import link.model.UniqueLinkClicks;

@Service
public class LinkService {
	
//	private static Logger logger = LoggerFactory.getLogger(LinkService.class);
	private final LinkRepository linkDao;
	private final UniqueClicksRepository uniqueDao;

	@Autowired
	public LinkService(LinkRepository linkDao, UniqueClicksRepository uniqueDao) {
		super();
		this.linkDao = linkDao;
		this.uniqueDao = uniqueDao;
	}

	
	public String saveOrGetLink(String href, Double time, String key, HttpServletRequest request) throws NoSuchAlgorithmException {
		String sh1 = sha1(key, request);
		Optional<Link> linkOptional = linkDao.findByLinkRedirectAndUserSha(href, sh1);
		if(linkOptional.isPresent()) {
			return linkOptional.get().getGeneratedValue();
		}
		Link link = new Link();
		link.setGeneratedValue(getRandomString(5));
		link.setLinkRedirect(href);
		link.setUserSha(sh1);
		if(time!=null) {
			link.setTime(time);
		}
		return linkDao.save(link).getGeneratedValue();
	}


	@Transactional
	public Link getLinkByGenerated(String generated, HttpServletRequest request) throws NotFoundException, NoSuchAlgorithmException {
		Link link = linkDao.findByGeneratedValue(generated).orElseThrow(NotFoundException::new);
		String sha = sha1("", request);
		link.setCountClick(link.getCountClick()+1L);
		if(!uniqueDao.findByShaAndLink(sha, link).isPresent()) {
			UniqueLinkClicks unique = new UniqueLinkClicks();
			unique.setLink(link);
			unique.setSha(sha);
			uniqueDao.save(unique);
		}
		checkTimeLink(link);
		return link;
	}
	
	public String getLinkByGeneratedToStats(String generated, String key, HttpServletRequest request) throws NotFoundException, NoSuchAlgorithmException {
		Link link = linkDao.findByGeneratedValueAndUserSha(generated, sha1(key, request)).orElseThrow(NotFoundException::new);
		checkTimeLink(link);
		return "Редирект на: "+link.getLinkRedirect()
		+ " Переходы: "+link.getCountClick() +" Уникальные переходы: "+uniqueDao.countByLink(link);
	}
	
	public void deleteLinkByGenerated(String generated, HttpServletRequest request, String key) throws NotFoundException, NoSuchAlgorithmException {
		Link link = linkDao.findByGeneratedValueAndUserSha(generated, sha1(key, request)).orElseThrow(NotFoundException::new);
		linkDao.delete(link);
	}
	
	
	private static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuilder sb=new StringBuilder();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(str.length()-1);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	
	private void checkTimeLink(Link link) throws NotFoundException {
		if(link.getTime()!=null) {
			Long minutes = (long)(link.getTime()*60L);
			if(LocalDateTime.now().minusMinutes(minutes).compareTo(link.getCreateDate())!=-1) {
				linkDao.delete(link);
				throw new NotFoundException();
			}
		}
	}
	
	private static String sha1(String key, HttpServletRequest request) throws NoSuchAlgorithmException {
		Map<String, Object> headers = new TreeMap<>();
		request.getHeaderNames().asIterator().forEachRemaining((s)->{
			if(s.equals("user-agent")||s.equals("sec-ch-ua-platform")||s.equals("cookie")) {
				headers.put(s, request.getHeader(s));
			}
		});
//		logger.info(headers.toString());
		StringBuilder strb = new StringBuilder(headers.toString().replaceAll(", ", "&"));
		strb.append(strb.substring(1, strb.length()-1));
		if(key!="") {
		strb.append("&"+key);
		}
		String input = strb.toString();
	    String sha1 = null;
	        MessageDigest sh1Digest = MessageDigest.getInstance("SHA-1");
	        byte[] bytes = sh1Digest.digest(input.getBytes());
	        StringBuilder sb = new StringBuilder();
	        for(byte b: bytes) {
	        	sb.append(String.format("%02X", b));
	        }
	        sha1=sb.toString();
//	        logger.info(sha1);
	    return sha1;
	}
	
}
