package link.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="links")
public class Link {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long linkId;
	private String linkRedirect;
	private String userSha;
	private String generatedValue;
	private Long countClick=0L;
	private Double time;
	private LocalDateTime createDate=LocalDateTime.now();
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="link")
	private List<UniqueLinkClicks> uniques;
	
	public String getLinkRedirect() {
		return linkRedirect;
	}
	public void setLinkRedirect(String linkRedirect) {
		this.linkRedirect = linkRedirect;
	}
	public Long getLinkId() {
		return linkId;
	}
	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}
	public String getGeneratedValue() {
		return generatedValue;
	}
	public void setGeneratedValue(String generatedValue) {
		this.generatedValue = generatedValue;
	}
	public Long getCountClick() {
		return countClick;
	}
	public void setCountClick(Long countClick) {
		this.countClick = countClick;
	}
	public Double getTime() {
		return time;
	}
	public void setTime(Double time) {
		this.time = time;
	}
	public LocalDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	public String getUserSha() {
		return userSha;
	}
	public void setUserSha(String userSha) {
		this.userSha = userSha;
	}
	public List<UniqueLinkClicks> getUniques() {
		return uniques;
	}
	public void setUniques(List<UniqueLinkClicks> uniques) {
		this.uniques = uniques;
	}
	
	
	
	
	
	
	
}
