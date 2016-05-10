package webApps.domain;

import org.springframework.data.repository.CrudRepository;

public interface SystemRepository extends CrudRepository<SystemDsc,Integer> {
    SystemDsc findByName(String name);
}