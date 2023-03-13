package study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
