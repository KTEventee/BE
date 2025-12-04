package com.server.eventee.domain.file.service;

import com.server.eventee.domain.file.dto.FileRequest.FileConfirmRequest;
import com.server.eventee.domain.file.dto.FileRequest.FileDeleteRequest;
import com.server.eventee.domain.file.dto.FileRequest.FileUploadRequest;
import com.server.eventee.domain.file.dto.FileUploadResponse;
import com.server.eventee.domain.member.model.Member;

public interface FileService {

  FileUploadResponse createPresignedUrl(FileUploadRequest request);

  String confirmUpload(Member member,FileConfirmRequest request);

  void deleteFile(FileDeleteRequest request);
}
