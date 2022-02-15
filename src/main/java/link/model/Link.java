package link.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="links")
public class Link {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long linkId;
	private String linkRedirect;
	private String secretKey;
	private String generatedValue;
	private Long countClick=0L;
	private Double time;
	private LocalDateTime createDate=LocalDateTime.now();
	public String getLinkRedirect() {
		return linkRedirect;
	}
	public void setLinkRedirect(String linkRedirect) {
		this.linkRedirect = linkRedirect;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
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
	
	
	
	
	
}
