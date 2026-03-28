package com.lpu.groupservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lpu.groupservice.dto.GroupRequestDTO;
import com.lpu.groupservice.dto.GroupResponseDTO;
import com.lpu.groupservice.entity.Group;
import com.lpu.groupservice.entity.GroupMember;
import com.lpu.groupservice.repository.GroupMemberRepository;
import com.lpu.groupservice.repository.GroupRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;

    // Create Group
    public GroupResponseDTO create(GroupRequestDTO dto, Long userId) {

        Group group = Group.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        Group saved = groupRepo.save(group);

        // creator auto join
        memberRepo.save(GroupMember.builder()
                .groupId(saved.getId())
                .userId(userId)
                .status("APPROVED")
                .joinedAt(LocalDateTime.now())
                .build());

        return map(saved);
    }

    // Get All
    public List<GroupResponseDTO> getAllGroups() {
        return groupRepo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    // Join Request
    public String requestJoin(Long groupId, Long userId) {

        // Check group exists
        groupRepo.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        memberRepo.save(GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .status("PENDING")
                .joinedAt(LocalDateTime.now())
                .build());

        return "Request sent";
    }

    // Approve (ONLY CREATOR)
    public String approve(Long groupId, Long creatorId, Long userId) {

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!group.getCreatedBy().equals(creatorId)) {
            throw new RuntimeException("Only creator can approve members");
        }

        GroupMember member = memberRepo
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in group"));

        member.setStatus("APPROVED");
        memberRepo.save(member);

        return "Approved";
    }

    // Remove Member
    public String remove(Long groupId, Long creatorId, Long userId) {

        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        if (!group.getCreatedBy().equals(creatorId)) {
            throw new RuntimeException("Only creator can remove members");
        }

        GroupMember member = memberRepo
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found in group"));

        memberRepo.delete(member);

        return "Removed";
    }

    // Mapper
    private GroupResponseDTO map(Group g) {
        return GroupResponseDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .createdBy(g.getCreatedBy())
                .build();
    }
}