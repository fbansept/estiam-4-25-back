package edu.ban7.estiam425back.dao;

import edu.ban7.estiam425back.model.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {
}