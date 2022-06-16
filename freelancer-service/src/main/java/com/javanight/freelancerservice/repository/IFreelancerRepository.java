package com.javanight.freelancerservice.repository;

import com.javanight.freelancerservice.entity.Freelancer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFreelancerRepository extends JpaRepository<Freelancer, Integer> {

    @Query(
            value="SELECT * FROM Freelancer WHERE profession = ?1",
            countQuery = "SELECT COUNT(*) FROM Freelancer WHERE profession = ?1",
            nativeQuery=true
    )
    List<Freelancer> getFreelancersByProfession(String profession);
}
