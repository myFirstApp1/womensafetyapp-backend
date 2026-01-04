package com.womensafety.authservice.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

    Optional<OtpChallenge> findByTxnId(UUID txnId);

    // Optional helper if you want latest pending for (user, channel)
    Optional<OtpChallenge> findTopByUserIdAndChannelOrderByIdDesc(UUID userId, OtpChannel channel);

    @Modifying
    @Query("""
    update OtpChallenge o
    set o.status = 'EXPIRED'
    where o.userId = :userId
      and o.channel = :channel
      and o.status = 'PENDING'
      and o.expiresAt > :now
""")
    void expireAllActive(
            @Param("userId") UUID userId,
            @Param("channel") OtpChannel channel,
            @Param("now") Instant now
    );

}
