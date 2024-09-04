package com.nbacm.newsfeed.domain.user.repository;

import com.nbacm.newsfeed.domain.user.entity.User;
import com.nbacm.newsfeed.domain.user.exception.NotMatchException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  default User findByEmailOrElseThrow(String email) {
    return this.findByEmail(email).orElseThrow(() -> new NotMatchException("올바른 계정 접근이 아닙니다"));
  }

  default User findByIdOrElseThrow(Long userId) {
    return findById(userId).orElseThrow(()->new NotMatchException("사용자를 찾을수 없습니다"));
  }

  @Query("SELECT u FROM User u WHERE u.isDeleted = true AND u.deletedAt < :deletedAt")
  List<User> findUsersDeletedBefore(LocalDateTime deletedAt);

  void deleteByUserIdIn(List<Long> userId);




}
