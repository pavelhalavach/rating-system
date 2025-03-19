package com.ratingsystem.repository;

import com.ratingsystem.entity.SellerOffer;
import com.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerOfferRepository extends JpaRepository<SellerOffer, Integer> {
    List<SellerOffer> findAllBySellerAndGameIsVerified(User seller, boolean isVerified);
    List<SellerOffer> findAllBySeller(User seller);
    void deleteBySellerAndGameId(User seller, Integer gameId);
    Optional<SellerOffer> findByGameId(Integer id);
}
