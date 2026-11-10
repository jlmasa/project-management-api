package com.jlmasa.pms_backend.messaging.repository;

import com.jlmasa.pms_backend.files.domain.MessageAttachment;
import com.jlmasa.pms_backend.files.domain.MessageAttachmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, MessageAttachmentId> {

    List<MessageAttachment> findByFileVersionId(UUID fileVersionId);
}
