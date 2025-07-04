package com.tongji.sportmanagement.GroupSubsystem.Repository;

import com.tongji.sportmanagement.GroupSubsystem.Entity.GroupMember;
import com.tongji.sportmanagement.GroupSubsystem.Entity.GroupMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    List<GroupMember> findGroupMembersByGroupId(Integer groupId);

    List<GroupMember> findGroupMembersByUserId(Integer userId);

    @Query("select exists( select g from GroupMember g where g.userId=?2 and g.groupId=?1 and g.role='leader')")
    boolean checkAuth(Integer groupId, Integer userId);

    void deleteByGroupId(Integer groupId);

    void deleteByGroupIdAndUserId(Integer groupId, Integer memberId);

    int countByGroupId(Integer groupId);

    @Modifying
    @Query("update GroupMember m set m.role=?3 where m.groupId=?1 and  m.userId=?2")
    void updateGroupMemberByGroupIdAndUserIdAndRole(Integer groupId, Integer targetId, GroupMemberRole role);

    boolean existsByUserId(Integer userId);

    boolean existsByUserIdAndGroupId(Integer memberId,Integer groupId);
}
