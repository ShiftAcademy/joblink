package com.example.backend.repositoy;

import com.example.backend.entity.User;
import com.example.backend.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByChatId(Long chatId);

    Optional<User> findByPhone(String phone);

    @Query(value = "SELECT DISTINCT u.id, u.phone,u.bot_active,u.expiration_date,u.category_id FROM users u ",
            nativeQuery = true)
    Optional<List<UserProjection>> findUsersByRoles(@Param("roles") List<String> roles);

    @Query(value = " SELECT DISTINCT u.id, u.phone, u.bot_active,u.expiration_date, u.category_id\n" +
            "               FROM users u\n" +
            "                        inner join public.users_roles ur on u.id = ur.user_id\n" +
            "                        inner join public.role r on r.id = ur.roles_id\n" +
            "               WHERE u.phone LIKE concat('%',:search,'%')\n" +
            "               AND r.name IN ('ROLE_CLIENT','ROLE_AGENT')",
            nativeQuery = true)
    Optional<List<UserProjection>> findUsersByRolesAndPhone(@Param("search") String search);


}
