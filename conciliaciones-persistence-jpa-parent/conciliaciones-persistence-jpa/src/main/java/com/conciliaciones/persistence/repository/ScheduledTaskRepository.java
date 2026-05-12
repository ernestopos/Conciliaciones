package com.conciliaciones.persistence.repository;

import com.conciliaciones.persistence.jpa.entity.ScheduledTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, Long> {
    List<ScheduledTaskEntity> findByActiveTrueAndStatus_NameIn(List<String> statusNames);
    List<ScheduledTaskEntity> findByActiveTrueAndTaskType_NameAndStatus_NameIn(String taskTypeName,List<String> statusNames);
}