package link.dao;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import link.model.Link;
import link.model.UniqueLinkClicks;

@Repository
public interface UniqueClicksRepository extends JpaRepository<UniqueLinkClicks, Long>{
	Optional<UniqueLinkClicks> findByShaAndLink(String sha, Link link);
	Long countByLink(Link link);
}
