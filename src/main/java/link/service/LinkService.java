package link.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import link.dao.LinkRepository;
import link.model.Link;

@Service
public class LinkService {
	
	private final LinkRepository linkDao;

	@Autowired
	public LinkService(LinkRepository linkDao) {
		super();
		this.linkDao = linkDao;
	}
	
	
	public String saveOrGetLink(String href, Double time) {
		Optional<Link> linkOptional = linkDao.findByLinkRedirect(href);
		if(linkOptional.isPresent()) {
			return linkOptional.get().getGeneratedValue();
		}
		Link link = new Link();
		link.setGeneratedValue(getRandomString(5));
		link.setLinkRedirect(href);
		if(time!=null) {
			link.setTime(time);
		}
		return linkDao.save(link).getGeneratedValue();
	}


	@Transactional
	public Link getLinkByGenerated(String generated, HttpServletRequest request) throws NotFoundException {
		Link link = linkDao.findByGeneratedValue(generated).orElseThrow(NotFoundException::new);
		link.setCountClick(link.getCountClick()+1L);
		checkTimeLink(link);
		return link;
	}
	
	public Link getLinkByGeneratedToStats(String generated) throws NotFoundException {
		Link link = linkDao.findByGeneratedValue(generated).orElseThrow(NotFoundException::new);
		checkTimeLink(link);
		return link;
	}
	
	public void deleteLinkByGenerated(String generated) throws NotFoundException {
		Link link = linkDao.findByGeneratedValue(generated).orElseThrow(NotFoundException::new);
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
	
}
