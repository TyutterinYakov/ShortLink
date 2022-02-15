package link.dao;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import link.model.Link;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long>{

	Optional<Link> findByLinkRedirect(String href);

	Optional<Link> findByGeneratedValue(String generated);

	Optional<Link> findByLinkRedirectAndUserSha(String href, String sha1);

	Optional<Link> findByGeneratedValueAndUserSha(String generated, String sha1);

	
}
