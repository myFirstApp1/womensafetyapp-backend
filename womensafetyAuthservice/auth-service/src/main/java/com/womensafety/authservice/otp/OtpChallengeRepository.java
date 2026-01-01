package com.womensafety.authservice.otp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, Long> {

    Optional<OtpChallenge> findByTxnId(String txnId);

    // Optional helper if you want latest pending for (user, channel)
    Optional<OtpChallenge> findTopByUserIdAndChannelOrderByIdDesc(Long userId, OtpChannel channel);
}
