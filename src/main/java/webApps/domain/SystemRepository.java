package webApps.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SystemRepository extends CrudRepository<SystemDsc,Integer> {
    SystemDsc findByName(String name);
}