package com.server.eventee.global.aws;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class S3Props {
  private String bucket;
  private String region;
  private String keyPrefix;
  private List<String> allowedContentTypes;
  private long maxUploadSizeBytes;
//  private String cloudfrontDomain;
}



